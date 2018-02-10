/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.indexer;
import hcc_search.hResult;
import hcc_search.hcc_exception;
import hcc_search.hcc_utils;
import hcc_search.myClassLoader;
import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.* ;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
//import org.apache.lucene.document.NumericField;
//import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Paths;

import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date ;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

/**
 *
 * @author frankkempf
 * Indexes most things...
 */
public class Indexer {
    IndexWriter m_idxWriter = null ;
    private String  m_strIndexPath;
    private Integer m_iTraceLevel = 0 ;
    private boolean m_bCreate ;
    
    public static final int STATUS_UNKNOWN                  =      0 ;
    public static final int STATUS_OK                       =   1001 ;
    public static final int STATUS_ERR                      =   1002 ;
    public static final int ERR_IDX_ALL_GOOD                =      1 ;
    public static final int ERR_IDX_NOT_HANDLED             =  -1001 ;
    public static final int ERR_IDX_FILE_NOT_ACCESSIBLE     = -11001 ;
    public static final int ERR_IDX_FILE_NO_EXT             = -11002 ;
    public static final int ERR_IDX_FILE_EXTENSION_EXCLUDED = -11003 ;
    
    public Indexer( String strIndexPath, boolean bCreate, Integer iTraceLevel){
        m_strIndexPath = strIndexPath ;
        m_bCreate      = bCreate;
        m_iTraceLevel  = iTraceLevel ;
        m_idxWriter = this.openIndex() ;              
    }
    public IndexWriter openIndex(){        
        Date start = new Date();
        try {
            System.out.println("Index directory '" + m_strIndexPath + "'...");
            Directory dir = FSDirectory.open( Paths.get(m_strIndexPath) );
            Analyzer analyzer = new StandardAnalyzer( );
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            if (m_bCreate) {
              // Create a new index in the directory, removing any
              // previously indexed documents:
              iwc.setOpenMode(OpenMode.CREATE);
            } else {
              // Add new documents to an existing index:
              iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
            }

      // Optional: for better indexing performance, if you
      // are indexing many documents, increase the RAM
      // buffer.  But if you do this, increase the max heap
      // size to the JVM (eg add -Xmx512m or -Xmx1g):
      //
      // iwc.setRAMBufferSizeMB(256.0);
      iwc.setMaxBufferedDocs(8192) ;
      iwc.setCommitOnClose(true) ;
      m_idxWriter = new IndexWriter(dir, iwc);
      //m_idxWriter.setRAMBufferSizeMB(384);
      //m_idxWriter.setMaxBufferedDocs(50);
      } catch (IOException e) {          
        System.out.println("Cannot create idxWriter '" + e.toString() + "'...");
        m_idxWriter = null ; 
      }
      return  m_idxWriter ;
    }
    
    public boolean closeIndex(){
      System.out.println("!---------------------->Indexer::closeIndex: closing....<---------------------------- ");      
      if( null == this.m_idxWriter ) {
        System.out.println("Error! idxWriter is null. Cannot complete operation ");     
        return false ;
      }
      
      
      
      try{  
          //commit wurde über setCommitOnClose eingeschaltet.
          //System.out.println("Indexer::closeIndex: calling commit.... ");   
          //this.m_idxWriter.commit();
          System.out.println("Indexer::closeIndex: changes committed. ");
          this.m_idxWriter.close();  
          Date end = new Date();
          //System.out.println(end.getTime() - start.getTime() + " total milliseconds");
        }
      catch(org.apache.lucene.store.AlreadyClosedException eAC){
          System.out.println("---------->  !!!!!!!!!!!!!!!!! caught a " + eAC.getClass() +
           "\n with message: " + eAC.getMessage());
      }  catch (IOException e) {
          System.out.println("---------->  !!!!!!!!!!!!!!!!!  caught a " + e.getClass() +
           "\n with message: " + e.getMessage());
        }   
        return true ;
    }
    
    
      /**
   * Indexes the given file using the given writer, or if a directory is given,
   * recurses over files and directories found under the given directory.
   * 
   * NOTE: This method indexes one document per input file.  This is slow.  For good
   * throughput, put multiple documents into your input file(s).  An example of this is
   * in the benchmark module, which can create "line doc" files, one document per line,
   * using the
   * <a href="../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
   * >WriteLineDocTask</a>.
   *  
   * @param writer Writer to the index where the given file/dir info will be stored
   * @param file The file to index, or the directory to recurse into to find files to index
   * @throws IOException
   */
  
     public hResult extractText(File file,  Long lTotalFiles, Long lCurrentFile) throws IOException {
        hResult   oResult = new hResult() ; //search Result object
        
        String strTmp = null;
        String strClassName = null ;
        Integer iID ;        
        myClassLoader ccl = new myClassLoader(0) ; //0=no trace
        
        
        oResult.m_iStatusCode = hResult.STATUS_UNKNOWN  ;
        
         if (false == file.canRead()) {             
              return oResult ;
         }

         //it is a file - get extension and call extractor
            
            
            /*
             if( null == oResult.m_strExt || 0 == oResult.m_strExt.length()  ){
                 oResult.m_iStatusCode =  hResult.ERR_IDX_FILE_NO_EXT ;
                 return oResult ;
             }
             */

             if( false == hcc_utils.isContained(hcc_utils.getExtensionUpper(file))){
                 System.out.println("no file extension configured [" + file.getName() + "]" ) ;
                 oResult.m_iStatusCode =  hResult.ERR_IDX_FILE_EXTENSION_EXCLUDED ;
                 return oResult ;
             }
             System.out.println("Processing [" + file.getName() + "] [" 
                     + lTotalFiles.toString() + "/" + lCurrentFile.toString() + "]" ) ;
             //compile classname
             strClassName = "hcc_search." + hcc_utils.getExtensionUpper(file) + "Extractor" ;
             //do  we have such a class?
             if( null == ccl.findClass(strClassName) ){
              strClassName = "hcc_search.BINExtractor" ;
             }

             try{
                     oResult = (hResult)ccl.invokeClassMethod(strClassName, "processDocument", file) ;
                     switch (oResult.m_eRetType){
                         case hResult.DOCUMENT :
                             oResult.m_iStatusCode = hResult.STATUS_OK ;
                             break ;
                         case hResult.CONTENT: 
                         case hResult.UNKNOWN : //a string
                             oResult.m_iStatusCode = hResult.STATUS_OK ;
                              break ; 
                         default:                         
                              oResult.m_iStatusCode = hResult.STATUS_ERR ; //nothing to do
                     }

                }
                catch(Exception ex){
                    Throwable t = ex.getCause() ;
                    System.out.println("indexFile failed [" + ex.toString() + "]" ) ;
                    throw new IOException( ex.toString() ) ;
                }
             
        if(file.isDirectory()){
                oResult.m_strType = "DIR" ;
        }     
        oResult.m_strFilename = file.getName() ;
        oResult.m_strPath = file.getPath() ;
        oResult.m_lLastModified = file.lastModified() ;
        oResult.m_strLastModified = hcc_utils.getLastModifiedString(file) ;
        oResult.m_strYear = hcc_utils.getYear(file) ;
        oResult.m_strExt = hcc_utils.getExtensionUpper(file) ;
        oResult.m_lFileLength = file.length() ;
        
        return oResult ;
     }
     /*
        2018 Apache Tika Extractor
     */
      /*
        2018 Apache Tika Extractor
     */
     
     private static String parseUsingAutoDetect(String filename, int iLimit) throws Exception {
        System.out.println("Handling using AutoDetectParser: [" + filename + "]");
        TikaConfig         tikaConfig    = TikaConfig.getDefaultConfig();
        AutoDetectParser parser = new AutoDetectParser(tikaConfig);
        BodyContentHandler handler = new BodyContentHandler(iLimit);
        Metadata           metadata      = new Metadata();
        TikaInputStream stream = TikaInputStream.get(new File(filename), metadata);
        parser.parse(stream, handler, metadata, new ParseContext());
        return handler.toString();
    }
     
     
     public hResult extractTextTika(File file, int iLimitLen, Long lTotalFiles, Long lCurrentFile) throws IOException, SAXException, TikaException {
            hResult   oResult = new hResult() ; //search Result object      
            
             //org.apache.tika
            oResult.m_iStatusCode = hResult.STATUS_ERR ; //DEFAULT
            try{
               oResult.m_oPayLoad = Indexer.parseUsingAutoDetect(file.getAbsolutePath(), iLimitLen) ;
                       //com.hcc_medical.hcctika.extract.extractTextTika(file.getAbsolutePath(), lTotalFiles, lCurrentFile) ;
               oResult.m_iStatusCode = hResult.STATUS_OK ;
               oResult.m_eRetType = hResult.CONTENT ;
            }catch(Exception ex){
               System.err.println(ex.toString()) ;
            }finally{
                
            }
            
            
     System.out.println("Tika Extracted [" + oResult.m_oPayLoad.toString().length() + "] bytes.");
     
    /* 
        if( oResult.m_oPayLoad.toString().length() < 1000 ){
             System.out.println( oResult.m_oPayLoad.toString()) ;
        }  
      */  
            if(file.isDirectory()){
                oResult.m_strType = "DIR" ;
            }     
            oResult.m_strFilename = file.getName() ;
            oResult.m_strPath = file.getPath() ;
            oResult.m_lLastModified = file.lastModified() ;
            oResult.m_strLastModified = hcc_utils.getLastModifiedString(file) ;
            oResult.m_strYear = hcc_utils.getYear(file) ;
            oResult.m_strExt = hcc_utils.getExtensionUpper(file) ;
            oResult.m_lFileLength = file.length() ;
            
            return  oResult ;
     }
  
  
      public int obsolete_indexFile(IndexWriter writer, hResult oResult) 
        throws IOException {
           
            String strTmp = null;
            Integer iID ;
            String strDate = new String("") ;
            Field fDocField = null ; //Lucene attribute field
            Document doc = null ;
       
             
            try {   

              // Add the path of the file as a field named "path".  Use a
              // field that is indexed (i.e. searchable), but don't tokenize 
              // the field into separate words and don't index term frequency
              // or positional information:
              Field pathField = new Field("path", oResult.m_strPath, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
              //pathField.setIndexOptions(IndexOptions.DOCS_ONLY);
              doc.add(pathField);
              strDate = oResult.m_strLastModified ; 
              Field dateField = new Field("date",  strDate, 
                                    Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
              //dateField.setIndexOptions(IndexOptions.DOCS_ONLY);
              doc.add(dateField);
              //System.out.println("adding field lastModified, value [" + strDate + "]");
              strTmp = System.getenv("COMPUTERNAME") ;
              if( null != strTmp && strTmp.length() == 0){
                   strTmp = System.getenv("HOSTNAME") ;
              }
              if( null != strTmp && strTmp.length() > 0 ){
                  Field hostName = new Field("source", oResult.m_strPath, Field.Store.YES, Field.Index.ANALYZED);
                  //pathField.setIndexOptions(IndexOptions.DOCS_ONLY);
                  doc.add(hostName);                     
              }

              fDocField = new Field("doctype", oResult.m_strExt, Field.Store.YES, Field.Index.ANALYZED);
              //fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
              doc.add(fDocField);  

              //the Filename (without path) will be included as field, 
              //so users may search for parts of it (i.e. name:CV*).
              //Useful when searching for documents and remember parts of its name.
              fDocField = new Field("filename", hcc_utils.getFilename(oResult.m_strPath), Field.Store.YES, Field.Index.ANALYZED);
              //fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
              doc.add(fDocField);  

              fDocField = new Field("year", oResult.m_strYear, Field.Store.YES, Field.Index.ANALYZED);
              //fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
              doc.add(fDocField);  

              // Add the last modified date of the file a field named "modified".
              // Use a NumericField that is indexed (i.e. efficiently filterable with
              // NumericRangeFilter).  This indexes to milli-second resolution, which
              // is often too fine.  You could instead create a number based on
              // year/month/day/hour/minutes/seconds, down the resolution you require.
              // For example the long value 2011021714 would mean
              // February 17, 2011, 2-3 PM.
              
            
            
              org.apache.lucene.document.LongField modifiedField = 
                  new org.apache.lucene.document.LongField("lastModified", oResult.m_lLastModified , 
                                           org.apache.lucene.document.Field.Store.YES) ;
              doc.add(modifiedField);
      /*        
              NumericField sizeField = new NumericField("size");
              modifiedField.setLongValue(file.length());
              doc.add(sizeField);
    */
              // Add the contents of the file to a field named "contents".  Specify a Reader,
              // so that the text of the file is tokenized and indexed, but not stored.
              // Note that FileReader expects the file to be in UTF-8 encoding.
              // If that's not the case searching for special characters will fail.


              if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                  if( m_iTraceLevel > 2)
                    System.out.println("adding file [" + oResult.m_strFilename + "]");
                    try{
                        writer.addDocument(doc);
                    }catch (java.lang.OutOfMemoryError ex){
                        System.out.println("Ex " + ex.toString());
                         throw new IOException( ex.toString() ) ;
                    }
                }
               else {
                // Existing index (an old copy of this document may have been indexed) so 
                // we use updateDocument instead to replace the old one matching the exact 
                // path, if present:
                System.out.println("updating " + oResult.m_strFilename);
                writer.updateDocument(new Term("path", oResult.m_strPath), doc);
              }
            } finally {

            }     
            return ERR_IDX_ALL_GOOD ;
      }//indexFile
      
      public void indexData( IndexData oIdxData ) throws hcc_exception
         {
            Field fDocField = null ; //Lucene attribute field
            Document doc = null ;
            IndexField idxField = null ;
            Term oTerm = null ;

             try{                   

                  doc = new Document();
                  doc.add(new Field("contents", oIdxData.m_strText,
                            Field.Store.YES, Field.Index.ANALYZED));
                }
                catch(Exception ex){
                    Throwable t = ex.getCause() ;
                    System.out.println("Indexer::indexData failed [" + ex.toString() + "]" ) ;
                    return ;
                }


//              These fields are not tokenized.
                try {   
                   for(int i=0;i<oIdxData.m_VFields.size();i++)
                   {
                      idxField = oIdxData.getField(i) ;                      
                      if( null == idxField){
                          continue ;
                      }
                      fDocField = new Field( idxField.m_strName, idxField.m_strValue, 
                              Field.Store.YES, Field.Index.NOT_ANALYZED);
                     // fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
                      doc.add(fDocField);  
                   }
              
              // Add the contents of the file to a field named "contents".  Specify a Reader,
              // so that the text of the file is tokenized and indexed, but not stored.
              // Note that FileReader expects the file to be in UTF-8 encoding.
              // If that's not the case searching for special characters will fail.


              if (this.m_idxWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                  if( m_iTraceLevel > 2)
                    System.out.println("adding data [" + oIdxData.m_strURN + "]");
                    try{
                        this.m_idxWriter.addDocument(doc);
                        //this.m_idxWriter.updateDocument(null, doc);
                    }catch (java.lang.OutOfMemoryError ex){

                        System.out.println("Ex " + ex.toString());
                    }
                   catch (Exception ex2){

                        System.out.println("Ex " + ex2.toString());
                    } 
                }
               else { //update
                // Existing index (an old copy of this document may have been indexed) so 
                // we use updateDocument instead to replace the old one matching the exact 
                // path, if present:
                  if( m_iTraceLevel > 2)
                     System.out.println("updating " + oIdxData.m_strURN );
                try{                    
                    oTerm = new Term("ID", oIdxData.m_strID) ;
                    //if( m_iTraceLevel > 2)
                        System.out.println("Term for update is:" + oTerm.text()) ;
                    this.m_idxWriter.updateDocument(oTerm, doc);
                }catch(Exception ex){
                    System.out.println(ex.toString()) ;
                }
              }
            } finally {

            }     
      }//indexData
      
    }

 
    
