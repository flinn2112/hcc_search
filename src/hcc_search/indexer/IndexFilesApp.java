/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.indexer; 

import hcc_search.indexer.Indexer; 
import hcc_search.config.IConfigProcessor;
import hcc_search.hResult;
import hcc_search.hcc_utils;
import hcc_search.logger.*;
import searchRT.utils.* ;

import hcc_search.myClassLoader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
//import org.apache.commons.io ;


import java.util.Vector ;
import java.util.Hashtable ;


 //md5



import java.io.File;

import java.io.IOException;

import java.util.Date;
import searchRT.utils.deltaMan;




/** Index all text files under a directory.
 * <p>
 * This is a command-line application demonstrating simple Lucene indexing.
 * Run it with no command-line arguments for usage information.
 */
public class IndexFilesApp implements IConfigProcessor{
    Vector<String> m_VFiles = null ;
    static Long m_lThreadTimeout = new Long(30000) ;
    static Hashtable m_htConfig = null ;
    static Hashtable m_htFiles = null ;  //for delta updates
    static boolean m_bUpdateFileDelta = false ;
    String strTmp1 = null ;
    String strTmp2 = null ;
    static Integer m_iTraceLevel = 0 ;
    static objectLogger m_ObjectLog = null ;
    static objectLogger m_DupLog = null ;
    static simpleLogger m_FileListLog = null ;
    static simpleLogger m_FileExcludeLog = null ;
    static MD5DB  m_MD5DB  ;
  public IndexFilesApp() {
      m_VFiles = new Vector<String>();
  }

  /** Index all text files under a directory. */
  public static void main(String[] args) {
    String usage = "java org.apache.lucene.demo.IndexFiles"
                 + " [-index INDEX_PATH] [-docs DOCS_PATH] [-cfg CONFIG_PATH] [-update]\n\n"
                 + "This indexes the documents in DOCS_PATH, creating a Lucene index"
                 + "in INDEX_PATH that can be searched with SearchFiles";
    String strIndexPath   = null ;
    String strDocPath     = null ;
    String strConfigPath  = null ;
    boolean bAppendFiles = false ;
    
    boolean bCreateIndex   = false ;
    
    
    Vector<String> vFiles = null ;
    int iMaxDocs = 10 ;
    int iTest = 0 ;
    IndexFilesApp ifa = new IndexFilesApp() ;
    Vector vTmp = new Vector() ;
    String strCurrFile  = null ;
    m_MD5DB = new MD5DB(System.getProperty("user.dir") + File.separator + "logs" + File.separator + "IndexFilesApp.txt") ;
        /*
        ("D:" + File.separator + "SharedDownloads" + File.separator 
                  + "Projekte" + File.separator + "hcc" + File.separator + "hcc_search" + File.separator 
                  + "dist" + File.separator + "logs" + File.separator + "IndexFilesApp.txt") ;
    */
    m_MD5DB.workDo();  //do collect processed entries before truncating the logs.
    
    
    strIndexPath  = "/Users/frankkempf/Documents/index";
    strDocPath    = "/Users/frankkempf/Documents/Volume_2/SharedDownloads/Projekte";
    bCreateIndex  = false ; //true;

    /*
    
    */
    //docsPath = "/Users/frankkempf/Documents/Volume_2/SharedDownloads/Dokus" ;
    
    for(int i=0;i<args.length;i++) {
      if ("-index".equals(args[i])) {
        strIndexPath = args[i+1];
        i++;
      } else if ("-docs".equals(args[i])) {
        strDocPath = args[i+1];
        i++;
      } else if ("-update".equals(args[i])) {
        bCreateIndex = false;
      }
      else if ("-cfg".equals(args[i])) {
        System.out.println("Argument  cfg is set  [" + args[i+1] + "]") ;
        strConfigPath = args[i+1];
      }
      if ("-delta".equals(args[i])){//use only files that where added recently
          System.out.println("Argument  delta is set  [" + args[i+1] + "]") ;
          bAppendFiles = true ;
          IndexFilesApp.m_bUpdateFileDelta = true ;
      }
    }
      
  
   IndexFilesApp.m_htConfig = new Hashtable() ;
   
   if( null == strConfigPath ){
       strConfigPath = System.getProperty("user.dir") + File.separator + "config" ;
    }
   
 //bAppendFiles = true ;
   
   IndexFilesApp.m_ObjectLog   = new objectLogger("IndexFilesApp.txt", System.getProperty("user.dir") + File.separator + "logs",
                                                                                                false  //no append, overwrite
                                                                                                );
   IndexFilesApp.m_DupLog      = new objectLogger("duplicates.txt", System.getProperty("user.dir") + File.separator + "logs", 
                                                                                                false  //no append, overwrite
                                                                                                     );
   IndexFilesApp.m_FileListLog = new simpleLogger("FileList.txt", System.getProperty("user.dir") + File.separator + "logs", bAppendFiles);
   
   IndexFilesApp.m_FileExcludeLog = new simpleLogger("FileExcludeList.txt", System.getProperty("user.dir") + File.separator + "logs", bAppendFiles);
   
   
   
    
   //only delta files requested - try to build a ht with already processed files.
   if( true == IndexFilesApp.m_bUpdateFileDelta  ){
       IndexFilesApp.m_htFiles = IndexFilesApp.m_FileListLog.buildFileList();
   }
   System.out.println("Config Path was set to [" + strConfigPath + "]") ;
   
   if( false == hcc_utils.checkPath(strConfigPath)){
    System.err.println("Path [ " + strConfigPath + " ] does not exist - check argument please.") ;
    return ;
    }
   strCurrFile  = strConfigPath + File.separator + "includeExt.txt" ;
   System.out.println("includeExt [ " + strCurrFile + " ]") ;
   if( false == hcc_utils.checkPath(strCurrFile)){
    System.err.println("Path [ " + strCurrFile + " ] does not exist - check argument please.") ;
    return ;
    }
    
    try{  //compile accepted extension table 
        hcc_utils.ht_build(strCurrFile);
      }catch(IOException ex){
          return ;
    }
   
   
   try{
       excludedFiles.ht_build(strConfigPath + File.separator + "excludeFiles.txt") ;
    }
    catch(Exception ex){
      //non lethal
    }
    
    
    
    //dT.run();
    //ifa is we...  -  hosts the callback for the config.
    strCurrFile = strConfigPath + File.separator + "settings.cfg";
    System.out.println("settings: [ " + strCurrFile + " ]") ;
    if( false == hcc_utils.checkPath(strCurrFile)){
    System.err.println("Path [ " + strCurrFile + " ] does not exist - check argument please.") ;
    return ;
    }
    
    
    
    
    //get index path from config
    hcc_utils.processCfgFile(strConfigPath + File.separator + "settings.cfg", ifa , "cfg");
    strIndexPath = (String)IndexFilesApp.m_htConfig.get("index");
    IndexFilesApp.m_iTraceLevel = Integer.parseInt( (String) IndexFilesApp.m_htConfig.get("trace") );
    System.out.println("Index Directory [ " + strIndexPath + " ]") ;
    if( false == hcc_utils.checkPath(strIndexPath)){
        System.err.println("Index Directory [ " + strIndexPath + " ] does not exist - check path please.") ;
        return ;
    }
    
    
    hcc_utils.processCfgFile(strConfigPath + File.separator + "directories.cfg", ifa , "dir");
    
    
    
    
    System.out.println("Path for indexing [ " + strIndexPath + " ].");
   
    
    dextorT dT = null; //new dextorT( strIndexPath, bCreateIndex, new Long(ifa.m_VFiles.size())) ;
    
    System.out.println("Collected [" + String.valueOf(ifa.m_VFiles.size()) + "] files." ) ;
    int iChk = 0 ;
    //iterate all files
    
    
    
    for(int i=0;i<ifa.m_VFiles.size();i++)  //
    // nur f. Debug for(int i=0;i<1000;i++)
    {
        //System.out.println("Vector Element "+i+" :"+a.m_VFiles.get(i));
        //processFile(writer, new File(ifa.m_VFiles.get(i).toString())) ;
        //last parameter is close Index -> last thread should close
        dT = new dextorT( strIndexPath, false, 5000L, (i == ifa.m_VFiles.size() - 1 ) ) ;
        
        vTmp.add(ifa.m_VFiles.get(i)) ;  //copy over
        //if(  i > 0 && vTmp.size() % iMaxDocs == 0 ){
        dT.m_VFiles = vTmp ;
        //nur Test f. Debug
        //set 2112 into iChk to process files without starting a thread - only for dev
        //if( 2112 == iChk )
          //  dT.processFiles() ;    
               
           System.out.println("Processing [" + ifa.m_VFiles.get(i) + "] [" 
               + Integer.toString(i+1) +  "/" + ifa.m_VFiles.size() + "]" ) ;
           if(  0 == i%10000 ){
               System.out.println("----------->  Reached next batch mark [" + Integer.toString(i) + "] reorg index...");
               //dT.reorg() ;
           }
           
           
//           Thread monitor = ThreadMonitor.start(timeoutInMillis);
           
           //dT.setDaemon(true);
           dT.start() ; //start thread
           //dT.processFile(dextorT.m_Indexer.m_idxWriter, new File(ifa.m_VFiles.get(i)));
           try{
                System.out.println("Waiting for thread [" + dT.getName() + "]");
                dT.join(m_lThreadTimeout);
                if( dT.isAlive()){
                    iTest = 2112 ;
                    
                    try{
                        dT.kill() ;
                    }
                    catch(java.nio.channels.ClosedByInterruptException exC){ 
                        continue ;
                    }
                    
                    System.out.println("Thread [" + dT.getName() + "]runs longer than [" + m_lThreadTimeout + "] - stopped.") ;
                }
                else{
                    System.out.println("Thread [" + dT.getName() + "] finished.");
                }
                dT.m_VFiles.removeAllElements();
            }
            catch (java.lang.InterruptedException e){
               System.out.println("Thread interrupted [" + m_lThreadTimeout + "]");
               dT.dumpStack();
            }
           finally{
               vTmp.removeAllElements();
           }
           
           
        //}
    }
    if( null != dT )
        dT.closeDex();  //At end the index must be closed.
    
    //maybe there is a remainder.
 /*
    if( true ==  vTmp.isEmpty() ){
        if( null != dT )
            dT.closeDex();  //At end the index must be closed.
        return ;
    }
    
 this code was eliminated 2/2015 - it should not happen to have a remainder.    
    //go ahead
    dT = new dextorT( strIndexPath, bCreateIndex, 500L ) ;
    dT.m_VFiles = vTmp ;
    
    //nur Test f. Debug
   //set 2112 into iChk to process files without starting a thread - only for dev
    if( 2112 == iChk )
        dT.processFiles() ;    
    
    
    dT.start();
    try{
        dT.join(m_lThreadTimeout);
        if( dT.isAlive()){
            dT.interrupt() ;
            System.out.println("Thread runs longer than [" + m_lThreadTimeout + "] - stopped.") ;
        }
        
    }
    catch (java.lang.InterruptedException e){
        
    }   
    finally{
        dT.closeDex();  //At end the index must be closed.
    }
  */ 
  }
 
  //objectname can be a filename or dir
  public void collectFiles(String strObjectName, Hashtable htAlreadyProcessed, Hashtable htExcludedFiles){
      String strFilename = null ;
      String strPath     = null ;
      String strExt      = null ;
      String rAttributes[] = null ;
      //The configuration may be colon separated - split      
      File file = new File(strObjectName) ;    
      if (file.isDirectory()) {
          File[] files = file. listFiles();
        // an IO error could occur
        if (files != null) {
              if( IndexFilesApp.m_iTraceLevel > 2)
                    System.out.println("Directory " + file.getName() + " will be collected.");
          for (int i = 0; i < files.length; i++) {  
              {
                collectFiles(file.getAbsolutePath() + File.separator + files[i].getName(), htAlreadyProcessed, htExcludedFiles) ;
              }
            }  //files != null        
        }
        else{
            if( IndexFilesApp.m_iTraceLevel > 2)
                System.out.println("Directory " + file.getName() + " contains no files.");       
        }
        /*
         strExt = hcc_utils.getExtension(files[i]) ;  //only add certain types
              if( null != strExt && true == hcc_utils.isContained(strExt) ){ 
                  */
          return ; //do not add directories
        }
        //collectFiles(file.getName()) ;
      //  
      strExt = hcc_utils.getExtension(strObjectName) ;  //only add certain types      
      if( null != strExt && true == hcc_utils.isContained(strExt) ){ 
          if( null != htAlreadyProcessed ){ //delta requested - check if not already processed
             if( null != htAlreadyProcessed.get(strObjectName) ){ //already IN
                 if( IndexFilesApp.m_iTraceLevel > 2 )
                    System.out.println("collectFiles: " + "strObjectName already processed." );
                 return ;
             }
          }
       
         if( true == excludedFiles.isContained(strObjectName) ){
             IndexFilesApp.m_FileExcludeLog.log(strObjectName) ;
             return ;
         }
          
          
        this.m_VFiles.add(strObjectName) ;
        IndexFilesApp.m_FileListLog.log(strObjectName) ;
      }
   }//collectFiles
  
  public String processLine(String strLine, String strCfgType){
      String strNameValue[] = null ;
      System.out.println("processLine: " + strLine );
      
      if(strLine.startsWith("*")){
          return "*" ;
      }
      
      if(strCfgType.equals("dir")){
          this.collectFiles(strLine, IndexFilesApp.m_htFiles,  excludedFiles.sHT_Excluded  );         
      }
      
      if(strCfgType.equals("cfg")){
         strNameValue = strLine.split(":", 2) ;
         IndexFilesApp.m_htConfig.put(strNameValue[0], strNameValue[1]) ;
      }
      
      return "" ;      
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
  
  
  
  static void indexFile(IndexWriter writer, File file,  int iMaxFileSizeMB, Long lTotalFiles, Long lCurrentFile)
        throws IOException {
        String strTitle = null ;
        String strPath = null ;
        String strExt = null ;
        String strTmp = null;
        String strMD5 = null ;
        String strClassName = null ;
        String strID = null ;
        String pattern = "[[^a-z0-9A-Z]]";  //f. ID
        Integer iID ;
        String strDate = new String("") ;
        Field fDocField = null ; //Lucene attribute field
        myClassLoader ccl = new myClassLoader(IndexFilesApp.m_iTraceLevel) ;
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
         
         //compile classname
         strClassName = "hcc_search." + strExt + "Extractor" ;
         
         
         //do  we have such a class?
         if( null == ccl.findClass(strClassName) ){
          strClassName = "hcc_search.BINExtractor" ;
         }
         
         
         //try{
             
         //}catch(Exception ex){
            //move on
         //}
         
         try{
                 //get content
                 oResult = (hResult)ccl.invokeClassMethod(strClassName, "processDocument", file) ;
                 if( null == oResult ){
                     return ;
                 }
                 //1.6.11 - delta processing
                 //even though it is tiresome to have read the file to decide wether to index it,
                 //it is necessary to get the MD5 and it will anyway accelerate the processing
                 //Es ist ein Unterschied, ob es ein Duplikat ist, oder einfach schonmal indiziert wurde.
                 //Das muss noch herausgearbeitet werden.
                if( null != oResult  ){
                    strMD5 =  fileLogger.md5( oResult.m_oPayLoad.toString() ) ;
                    
                    switch(deltaMan.check( strMD5 )){ 
                        case deltaMan.IS_CONTAINED:
                            // no logging IndexFilesApp.m_DupLog.log(file, strMD5 );
                            return ;
                        case deltaMan.IS_DUP:
                            IndexFilesApp.m_DupLog.log(file, strMD5 );
                            return ;
                        default:
                            break ;
                    }                  
                    deltaMan.discovered(strMD5, file.getName());  //enrich our map                    
                }
                 
                 switch (oResult.m_eRetType){
                     case hResult.DOCUMENT :
                         doc = (Document)oResult.m_oPayLoad ;
                         break ;
                     case hResult.CONTENT: 
                     case hResult.UNKNOWN : //a string                     
                          doc = new Document();
                          doc.add(new Field("contents", (String)oResult.m_oPayLoad,
                                    Field.Store.YES, Field.Index.ANALYZED));                          
                          Long lCheckSum = hcc_utils.getCheckSum((String)oResult.m_oPayLoad) ;
                          strTmp = lCheckSum.toString() ;
                          doc.add(new Field("checksum", 
                                    strTmp,
                                    Field.Store.YES, Field.Index.ANALYZED));
                          strTmp = String.valueOf(file.length() ) ;
                          doc.add(new Field("filesize", 
                                    strTmp,
                                    Field.Store.YES, Field.Index.ANALYZED));
                          break ; 
                     default:                         
                          return ; //nothing to do
                 }
                 oResult.m_iPayLoadLen = oResult.m_oPayLoad.toString().length() ; 
            }
            catch(Exception ex){
                Throwable t = ex.getCause() ;
                System.out.println("indexFile - Failed [" + ex.toString() + "]" ) ;
                return ;
            }

        try {   

          // Add the path of the file as a field named "path".  Use a
          // field that is indexed (i.e. searchable), but don't tokenize 
          // the field into separate words and don't index term frequency
          // or positional information:
          
         
          strPath = file.getPath() ;
          Field pathField = new Field("URN", strPath, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
//          pathField.setIndexOptions(IndexOptions.DOCS_ONLY);
          doc.add(pathField);
          
          fDocField = new Field("ID", strPath.replaceAll(pattern, "_") , Field.Store.YES, Field.Index.ANALYZED);
     //     fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
          doc.add(fDocField); 
           
          
          
          strDate = hcc_utils.getLastModifiedString(file) ;
          Field dateField = new Field("date",  strDate, 
                                Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
       //   dateField.setIndexOptions(IndexOptions.DOCS_ONLY);
          doc.add(dateField);
          //System.out.println("adding field lastModified, value [" + strDate + "]");
          strTmp = System.getenv("COMPUTERNAME") ;
          if( null != strTmp && strTmp.length() == 0){
               strTmp = System.getenv("HOSTNAME") ;
          }
          if( null != strTmp && strTmp.length() > 0 ){
              Field hostName = new Field("source", file.getPath(), Field.Store.YES, Field.Index.ANALYZED);
            //  pathField.setIndexOptions(IndexOptions.DOCS_ONLY);
              doc.add(hostName);                     
          }
          
          fDocField = new Field("doctype", strExt, Field.Store.YES, Field.Index.ANALYZED);
         // fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
          doc.add(fDocField);  
          
          //the Filename (without path) will be included as Title.
          strTitle = file.getName() ;
          if(null != strTitle ){
            fDocField = new Field("title", strTitle, Field.Store.YES, Field.Index.ANALYZED);
           // fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
            doc.add(fDocField); 
          }
          
          //the Filename (without path) will be included as field, 
          //so users may search for parts of it (i.e. name:CV*).
          //Useful when searching for documents and remember parts of its name.
          strPath = hcc_utils.getFilename(file.getPath()) ;
          fDocField = new Field("filename", strPath, Field.Store.YES, Field.Index.ANALYZED);
        //  fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
          doc.add(fDocField);  
          
          fDocField = new Field("year", hcc_utils.getYear(file), Field.Store.YES, Field.Index.ANALYZED);
      //    fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
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
              if( IndexFilesApp.m_iTraceLevel > 2)
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
              if( IndexFilesApp.m_iTraceLevel > 2)   
                System.out.println("updating " + file.getName());
            writer.updateDocument(new Term("URN", file.getPath()), doc);
            if( IndexFilesApp.m_iTraceLevel > 2)   
                System.out.println("done updating " + file);
          }
        } finally {
            if( null != oResult ){
                IndexFilesApp.m_ObjectLog.log(file, strMD5 );  
            }              
            else
              IndexFilesApp.m_ObjectLog.log(file);
        }     
  }
}

class MD5DB extends Thread{
    private String m_strFilename = null ;
    public MD5DB(String strLogFilename){ //ohne pfad normalerweise
        m_strFilename = strLogFilename ;
    }
    
    public void workDo(){
         try{
            deltaMan.createDB(m_strFilename);
        }
        catch(Exception ex){
            System.err.println("run::MD5DB error - " + ex.getMessage());
        }
    }
    
    public void run(){
        
       this.workDo() ;
      //if( true == m_bCloseIdxIndicator ) this.closeDex() ; //last thread should close
    }
}

/*
 * 
 * MORE CHECKING! 
 * Paths need to be accessible!
 */
class dextorT extends Thread{    
    public Vector          m_VFiles = null ;
    public static Long     m_lTotalFiles = 0L ;
    public static Long     m_lBatchFiles = 0L ;  //Current Number of files in batch
    public static Long     m_lReorgAfter = 1000L ; //reorg index after processing a certain number of files
    public Long            m_lCurrentFile = 0L ;
    public static String   m_strPath = null ;
    public boolean         m_bCloseIdxIndicator = false ;
    
    public static Indexer  m_Indexer = null ;
    
  
    
    
    public dextorT( String strIndexPath, boolean bCreateIndex, 
            Long lReorgAfterNumFiles, boolean bCloseDex){
        m_VFiles       = null ; 
        m_strPath = strIndexPath ;
        m_bCloseIdxIndicator = bCloseDex ;
        dextorT.m_lTotalFiles  = 0L ;
        dextorT.m_lReorgAfter = lReorgAfterNumFiles ;
        if( null == dextorT.m_Indexer ){  //Singleton
            dextorT.m_Indexer  = new Indexer(strIndexPath,  bCreateIndex, IndexFilesApp.m_iTraceLevel );
        }
        
    }
    
    
    public boolean closeDex(){
        //try{
            //System.out.println("closeDex: Committing. ");
            //dextorT.m_Indexer.m_idxWriter.commit();
        //}
        //catch( IOException ex){
            //System.err.println("dextorT - reorg: Error on commit");
            //return false ;
        //}
        if( null == dextorT.m_Indexer ){
            return false ;
        }
        System.out.println("closeDex: closing... ");
        dextorT.m_Indexer.closeIndex();
        System.out.println("...Index closed");
        return true ;
    }
    
    public boolean reorg(){
        System.out.println("reorg: calling closeDex. ");
        this.closeDex();
        dextorT.m_Indexer = null ; //will cause a new creation on next run.
        //!!check if the create option true is really optimal and working.
        dextorT.m_Indexer = new Indexer(m_strPath,  true, IndexFilesApp.m_iTraceLevel );
        return true ;
    }
    
    protected void finalize() throws Throwable
    {
      //do finalization here
      super.finalize(); //not necessary if extending Object
    } 
    
    public void run(){
        try{
            this.processFiles() ;
        }
        catch(Exception ex){
            System.err.println("run::processFiles error - " + ex.getMessage());
        }
      //if( true == m_bCloseIdxIndicator ) this.closeDex() ; //last thread should close
    }
    
    public void kill() throws java.nio.channels.ClosedByInterruptException{      
          System.err.println("dt::kill - this thread gets killed... ");
          this.interrupt() ;
          throw new java.nio.channels.ClosedByInterruptException() ;
    }
    

    
    public void processFile(IndexWriter w, File f){
        //Thread customization
        this.setName(f.getName());
        try{
            IndexFilesApp.indexFile(w, f, 5, m_lTotalFiles, m_lCurrentFile);
            /*
            dextorT.m_lTotalFiles++ ;
            dextorT.m_lBatchFiles++ ;
            if( dextorT.m_lBatchFiles > dextorT.m_lReorgAfter ){
                dextorT.m_lBatchFiles = 0L ;
                dextorT.m_Indexer.closeIndex();
                System.out.println("Reached max batch size of [" + dextorT.m_lBatchFiles.toString() + "] closing index...");
                dextorT.m_Indexer = null ; //will cause a new creation on next run.
            }
            */
        }
        
        catch(org.apache.lucene.store.AlreadyClosedException exC){
            
        }        
        catch(org.apache.lucene.util.ThreadInterruptedException exI){
            
        }  
        catch(IOException ex){
            
        }        
    }
    
    public void processFiles() throws java.nio.channels.ClosedByInterruptException{    
        
        if( null == dextorT.m_Indexer.m_idxWriter ){ 
            System.out.println("processFiles: Error Indexer.m_idxWriter is null. ");
            return ;  //MORE HANDLING!!!
        }
        
        
        
        for(int i=0;i<this.m_VFiles.size();i++)
        {
            m_lCurrentFile++ ;
            if( IndexFilesApp.m_iTraceLevel > 2 )
                System.out.println("Vector Element "+ i +" :" + this.m_VFiles.get(i));
            processFile( m_Indexer.m_idxWriter, new File(this.m_VFiles.get(i).toString())) ;
        }
        //m_Indexer.closeIndex( m_Indexer.m_idxWriter);
    }
 /*       
    public void processFiles(){    
        final File docDir = new File(m_strDocPath);
        if (!docDir.exists() || !docDir.canRead()) {
          System.out.println("Document directory '" +docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
          System.exit(1);
        }
       
        
        IndexWriter writer = this.openIndex() ;
        if( null == writer) return ;  //MORE HANDLING!!!
        
        //processFile(new File(v.get(i).toString())) ;
        
        
    
    Date start = new Date();
    try {
      System.out.println("Indexing to directory '" + m_strIndexPath + "'...");

      Directory dir = FSDirectory.open(new File(m_strIndexPath));


      // Optional: for better indexing performance, if you
      // are indexing many documents, increase the RAM
      // buffer.  But if you do this, increase the max heap
      // size to the JVM (eg add -Xmx512m or -Xmx1g):
      //
      // iwc.setRAMBufferSizeMB(256.0);

     
     // IndexFilesApp.indexDocs(writer, docDir, 5);

      // NOTE: if you want to maximize search performance,
      // you can optionally call optimize here.  This can be
      // a costly operation, so generally it's only worth
      // it when your index is relatively static (ie you're
      // done adding documents to it):
      //
      // writer.optimize();

      writer.close();

      Date end = new Date();
      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

    } catch (IOException e) {
      System.out.println(" caught a " + e.getClass() +
       "\n with message: " + e.getMessage());
    }
    
    }
   */
}
