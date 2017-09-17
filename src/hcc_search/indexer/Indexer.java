/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.indexer;
import hcc_search.hResult;
import hcc_search.hcc_exception;
import hcc_search.hcc_utils;
import hcc_search.indexer.IndexField;
import hcc_search.indexer.IndexData;
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
import org.apache.lucene.util.Version;
import java.nio.file.Paths;

import java.io.File;

import java.io.IOException;
import java.util.Date ;

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
      iwc.setMaxBufferedDocs(1000) ;
      iwc.setCommitOnClose(true) ;
      m_idxWriter = new IndexWriter(dir, iwc);
      //m_idxWriter.setRAMBufferSizeMB(384);
      //m_idxWriter.setMaxBufferedDocs(50);
      } catch (IOException e) {          
        System.err.println("Cannot create idxWriter '" + e.toString() + "'...");
        m_idxWriter = null ; 
      }
      return  m_idxWriter ;
    }
    
    public boolean closeIndex(){
      System.out.println("Indexer::closeIndex: closing.... ");      
      if( null == this.m_idxWriter ) {
        System.out.println("Error! idxWriter is null. Cannot complete operation ");     
        return false ;
      }
      try{         
          System.out.println("Indexer::closeIndex: calling commit.... ");   
          this.m_idxWriter.commit();
          System.out.println("Indexer::closeIndex: changes committed. ");
          this.m_idxWriter.close();  
          Date end = new Date();
          //System.out.println(end.getTime() - start.getTime() + " total milliseconds");
        } catch (IOException e) {
          System.out.println(" caught a " + e.getClass() +
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
  
     
  
  
      public void indexFile(IndexWriter writer, File file,  int iMaxFileSizeMB, Long lTotalFiles, Long lCurrentFile)
        throws IOException {
            String strExt = null ;
            String strTmp = null;
            String strClassName = null ;
            Integer iID ;
            String strDate = new String("") ;
            Field fDocField = null ; //Lucene attribute field
            myClassLoader ccl = new myClassLoader(0) ; //0=no trace
            hResult   oResult = new hResult() ; //search Result object 
            Document doc = null ;
        // do not try to index files that cannot be read
            if (false == file.canRead()) {
              return ;
            }

         //it is a file - get extension and call extractor

             strExt = hcc_utils.getExtensionUpper(file) ;
             if( null == strExt || 0 == strExt.length()  ){
                 return ;
             }

             if( false == hcc_utils.isContained(strExt)){
                 System.out.println("no file extension configured [" + file.getName() + "]" ) ;
                 return ;
             }
             System.out.println("Processing [" + file.getName() + "] [" 
                     + lTotalFiles.toString() + "/" + lCurrentFile.toString() + "]" ) ;
             //compile classname
             strClassName = "hcc_search." + strExt + "Extractor" ;



             //do  we have such a class?
             if( null == ccl.findClass(strClassName) ){
              strClassName = "hcc_search.BINExtractor" ;
             }

             try{
                     oResult = (hResult)ccl.invokeClassMethod(strClassName, "processDocument", file) ;
                     switch (oResult.m_eRetType){
                         case hResult.DOCUMENT :
                             doc = (Document)oResult.m_oPayLoad ;
                             break ;
                         case hResult.CONTENT: 
                         case hResult.UNKNOWN : //a string
                              // make a new, empty document

                             //TXTExtractor t = new TXTExtractor() ;
                            // TXTExtractor.processDocument(file) ;

                              doc = new Document();
                              doc.add(new Field("contents", (String)oResult.m_oPayLoad,
                                        Field.Store.YES, Field.Index.ANALYZED));
                              break ; 
                         default:                         
                              return ; //nothing to do
                     }

                }
                catch(Exception ex){
                    Throwable t = ex.getCause() ;
                    System.out.println("Failed [" + ex.toString() + "]" ) ;
                    return ;
                }
             
            try {   

              // Add the path of the file as a field named "path".  Use a
              // field that is indexed (i.e. searchable), but don't tokenize 
              // the field into separate words and don't index term frequency
              // or positional information:
              Field pathField = new Field("path", file.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
              //pathField.setIndexOptions(IndexOptions.DOCS_ONLY);
              doc.add(pathField);
              strDate = hcc_utils.getLastModifiedString(file) ;
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
                  Field hostName = new Field("source", file.getPath(), Field.Store.YES, Field.Index.ANALYZED);
                  //pathField.setIndexOptions(IndexOptions.DOCS_ONLY);
                  doc.add(hostName);                     
              }

              fDocField = new Field("doctype", strExt, Field.Store.YES, Field.Index.ANALYZED);
              //fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
              doc.add(fDocField);  

              //the Filename (without path) will be included as field, 
              //so users may search for parts of it (i.e. name:CV*).
              //Useful when searching for documents and remember parts of its name.
              fDocField = new Field("filename", hcc_utils.getFilename(file.getPath()), Field.Store.YES, Field.Index.ANALYZED);
              //fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
              doc.add(fDocField);  

              fDocField = new Field("year", hcc_utils.getYear(file), Field.Store.YES, Field.Index.ANALYZED);
              //fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
              doc.add(fDocField);  

              // Add the last modified date of the file a field named "modified".
              // Use a NumericField that is indexed (i.e. efficiently filterable with
              // NumericRangeFilter).  This indexes to milli-second resolution, which
              // is often too fine.  You could instead create a number based on
              // year/month/day/hour/minutes/seconds, down the resolution you require.
              // For example the long value 2011021714 would mean
              // February 17, 2011, 2-3 PM.
              
            
            Long lLastMod = file.lastModified() ;
              org.apache.lucene.document.LongField modifiedField = 
                  new org.apache.lucene.document.LongField("lastModified", lLastMod, 
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
                    System.out.println("adding file [" + file + "]");
                    try{
                        writer.addDocument(doc);
                    }catch (java.lang.OutOfMemoryError ex){

                        System.out.println("Ex " + ex.toString());

                    }
                }
               else {
                // Existing index (an old copy of this document may have been indexed) so 
                // we use updateDocument instead to replace the old one matching the exact 
                // path, if present:
                System.out.println("updating " + file);
                writer.updateDocument(new Term("path", file.getPath()), doc);
              }
            } finally {

            }     
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

 
    
