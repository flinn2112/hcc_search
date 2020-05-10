/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.indexer; 

import hcc_pdfWndExtractor2020.pdfWndExtractor;
import hcc_search.config.IConfigProcessor;
import hcc_search.hResult;
import hcc_search.hcc_utils;
import hcc_search.logger.*;
import searchRT.utils.* ;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
//import org.apache.lucene.document.NumericField;
//import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
//import org.apache.commons.io ;


import java.util.Vector ;
import java.util.Hashtable ;


 //md5



import java.io.File;

import java.io.IOException;
import java.util.Calendar;

import java.util.Date;
import org.apache.lucene.document.FieldType;
import searchRT.utils.deltaMan;

//1.8.3.10 - under construction
/*
  put all necessary stuff in here to pass it between calls
*/
class clientCtxt{
    Long m_lThreadTimeout = new Long(30000) ;
    Integer m_iReorgAfter = 0 ;
}

/*
2018 1.8.4.15 for delta enum files
*/
class fileFilter{
    public Date m_dtLow = null ;
    public Date m_dtHigh = null ;
    public fileFilter(Date dtHigh, Date dtLow){
        m_dtLow  = dtLow ;
        m_dtHigh = dtHigh ;
    }
    
    public fileFilter(int iDiff){  
        if( 0 == Math.abs(iDiff) ){ //set default
            iDiff = -10 ; 
        }
        
        if(iDiff > 0) iDiff = iDiff * -1 ;
        
        //iDiff should be a negative number - files created in the future would not exist.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());  //now
        //calendar.add(Calendar., beforeMonths);
        calendar.add(Calendar.DAY_OF_MONTH, iDiff) ;
        this.m_dtLow = calendar.getTime() ;
        
    }
    
    public boolean accept(File f){
        if(  null == f ) return false ;
        boolean bRet = true ;
        long lMod = f.lastModified() ;
        Date dtMod = new Date(lMod) ;
        if( null != m_dtLow && null != m_dtHigh){
            bRet = ( (dtMod.after(m_dtLow) )  && (dtMod.before(m_dtHigh)) ) ;            
        }
        if( null != m_dtLow && null == m_dtHigh){
            bRet = ( dtMod.after(m_dtLow) ) ;
        }
        if( null == m_dtLow && null != m_dtHigh){
            bRet = ( dtMod.before(m_dtHigh) ) ;
        }               
        return bRet ;
    }
}

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
    static Integer m_iReorgAfter = 5000 ; //1.9.8.16 - variables Einstellen v. Reorg nach n-Dokumenten.
    static objectLogger m_ObjectLog = null ;
    static objectLogger m_DupLog = null ;
    static objectLogger m_ReviewLog = null ; //1.8.3.1 Files that have low extraction rates for review
    static simpleLogger m_FileListLog = null ;
    static simpleLogger m_FileExcludeLog = null ;
    static MD5DB  m_MD5DB  ;
    //1.8.4.15 -flag for deltaIndex -> force indexing of changed files
    static boolean m_bForceUpdate = false ; //default is false
    public String m_strDir = null ;  //1.8.4.15 delta directory
    public fileFilter m_fileFilter = null ;
    public int m_lastModSince = 0 ; //days modified(for args) DEFAULT is 0 which means: do not use lastMod
    
    
  public IndexFilesApp() {
      m_VFiles = new Vector<String>();
  }

  /** Index all text files under a directory. */
  public static void main(String[] args) {
    String usage = "java -jar hcc_search.jar"
                 + " [-deltaDir PATH] [-lastMod Number(in Days)] [-forceUpd (update Content even if present)] \n\n";
    String strIndexPath   = null ;
    String strDocPath     = null ;
    String strConfigPath  = null ;
    boolean bAppendFiles = false ;    
    boolean bCreateIndex   = false ;
    
    pdfWndExtractor pWE = null ;
    pWE = new pdfWndExtractor() ;
    pWE.extractTest("x:\\tmp\\83275707_2020_Nr.004_Kontoauszug_vom_30.04.2020_20200509031949.pdf") ; //nur Test
    stateMonitor oStateMon = new stateMonitor( );
    
    Vector<String> vFiles = null ;
    int iMaxDocs = 10 ;
    int iTest = 0 ;
    IndexFilesApp ifa = new IndexFilesApp() ;
    Vector vTmp = new Vector() ;
    String strCurrFile  = null ;
    //Create a map from all indexed files(these are logged in IndexFilesApp.txt).
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
        System.out.println("Argument  [" + args[i] + "]") ;       
        
      if ("-help".equals(args[i])) {
          System.out.println(usage); 
          return ;
      }   
        
        
      if ("-index".equals(args[i])) {
        strIndexPath = args[i+1];
        i++;
      } else if ("-docs".equals(args[i])) {
        strDocPath = args[i+1];
        i++;
      } else if ("-update".equals(args[i])) {
        bCreateIndex = false;
      }
       else if ("-forceUpd".equals(args[i])) {//1.8.4.15
        IndexFilesApp.m_bForceUpdate = true ;
      }
       else if ("-deltaDir".equals(args[i])) {//1.8.4.15 index this directory only
        ifa.m_strDir =  args[i+1] ;
      }
       else if ("-lastMod".equals(args[i])) {//1.8.4.15 index this directory only
        ifa.m_lastModSince =  Integer.parseInt(args[i+1]) ;
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
   IndexFilesApp.m_ReviewLog   = new objectLogger("reviewResult.txt", System.getProperty("user.dir") + File.separator + "logs", 
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
    System.out.println("Path [ " + strConfigPath + " ] does not exist - check argument please.") ;
    return ;
    }
   strCurrFile  = strConfigPath + File.separator + "includeExt.txt" ;
   System.out.println("includeExt [ " + strCurrFile + " ]") ;
   if( false == hcc_utils.checkPath(strCurrFile)){
    System.out.println("Path [ " + strCurrFile + " ] does not exist - check argument please.") ;
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
    System.out.println("Path [ " + strCurrFile + " ] does not exist - check argument please.") ;
    return ;
    }   
    
    //get index path from config
    hcc_utils.processCfgFile(strConfigPath + File.separator + "settings.cfg", ifa , "cfg");
    strIndexPath = (String)IndexFilesApp.m_htConfig.get("index");
    IndexFilesApp.m_iTraceLevel = Integer.parseInt( (String) IndexFilesApp.m_htConfig.get("trace") );
    IndexFilesApp.m_iReorgAfter = Integer.parseInt( (String) IndexFilesApp.m_htConfig.get("reorgAfter") );
    
    System.out.println("Index Directory [ " + strIndexPath + " ]") ;
    if( false == hcc_utils.checkPath(strIndexPath)){
        System.out.println("Index Directory [ " + strIndexPath + " ] does not exist - check path please.") ;
        return ;
    }
    
    //1.8.15.4 in case of delta indexing dirs
   //dev only 
   //IndexFilesApp.m_strDir  = "X:\\ScanRoot\\docs.allis1.com" ;
   if( 0 != ifa.m_lastModSince ){
       ifa.m_fileFilter = new fileFilter(ifa.m_lastModSince) ;
   }
   
    if( null != ifa.m_strDir ){        
        ifa.processLine(ifa.m_strDir, "dir") ;
    }
    else{
        hcc_utils.processCfgFile(strConfigPath + File.separator + "directories.cfg", ifa , "dir");
    }    
    
    System.out.println("Path for indexing [ " + strIndexPath + " ].");   
//if(1==1) return ; //dev


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
        dT = new dextorT( strIndexPath, false, 5000L, (i == ifa.m_VFiles.size() - 1 ), oStateMon ) ; //Arg #3: closes Index when loop reached vector size
        
        vTmp.add(ifa.m_VFiles.get(i)) ;  //copy over
        //if(  i > 0 && vTmp.size() % iMaxDocs == 0 ){
        dT.m_VFiles = vTmp ;
        
/* only for local debug without threads
        */
/*
        try {            
            dT.processFiles() ;
        } catch (Exception ex) {
            Logger.getLogger(IndexFilesApp.class.getName()).log(Level.SEVERE, null, ex);
        }
   */         
           System.out.println("Processing [" + ifa.m_VFiles.get(i) + "] [" 
               + Integer.toString(i+1) +  "/" + ifa.m_VFiles.size() + "]" ) ;
           if( i> 0 && i%ifa.m_iReorgAfter == 0 ){
               System.out.println("----------->  Reached next batch mark [" + Integer.toString(i) + "] reorg index...");
               dT.reorg() ; //12/2017 - wieder aktiviert
           }
           
           
//           Thread monitor = ThreadMonitor.start(timeoutInMillis);
           
           //dT.setDaemon(true);
           dT.start() ; //start thread
/*
           try{
                dT.processFile(dextorT.m_Indexer.m_idxWriter, new File(ifa.m_VFiles.get(i)));
            }catch(Exception ex){

            }
*/
           try{
                System.out.println("IndexFilesApp Waiting for thread [" + dT.getName() + "]");
                dT.join(m_lThreadTimeout);
                if( dT.isAlive() ){ //update wird nicht gestoppt
                    iTest = 2112 ;    
                    
                    try{
                        
                        dT.kill() ;
                        System.out.println("<----------------  IndexFilesApp  Thread Interrupt: kill called..");
                        //dT.closeDex(); //close all stuff so it would be re-opened by the next thread.
                    }
                    catch(java.nio.channels.ClosedByInterruptException exC){ 
                        System.out.println("<----------------  IndexFilesApp  ClosedByInterruptException: closing index.");
                        //dT.closeDex();                        
                        continue ;
                    }
                    
                    System.out.println("Thread [" + dT.getName() + "]runs longer than [" + m_lThreadTimeout + "] - stopped.") ;
                }
                else{
                    System.out.println("Thread [" + dT.getName() + "] finished.");
                }
                dT.m_VFiles.removeAllElements(); //clear the processing Vector of this thread
            }
            catch (java.lang.InterruptedException e){
               System.out.println("Thread interrupted [" + m_lThreadTimeout + "]");
               //dT.dumpStack();
            }
           finally{
               vTmp.removeAllElements();
           }
           
           
        //}
    }
    if( null != dT ){
        System.out.println("<----------------  IndexFilesApp  Main closing index.");
        dT.closeDex();
    }
          //At end the index must be closed.
    
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
                    System.out.println("Directory " + file.getPath() + " will be collected.");
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
      
      if( null != this.m_fileFilter){
          if(!this.m_fileFilter.accept(file)){
              System.out.println("collectFiles: " + strObjectName + " rejected by file filter." );
              return ;
          }
          else{
              System.out.println("collectFiles: " + strObjectName + " accepted by file filter." );
          }
      }
      
      strExt = hcc_utils.getExtension(strObjectName) ;  //only add certain types      
      if( null != strExt && true == hcc_utils.isContained(strExt) ){ 
          if( null != htAlreadyProcessed ){ //delta requested - check if not already processed
             if( null != htAlreadyProcessed.get(strObjectName) ){ //already IN
                 if( IndexFilesApp.m_iTraceLevel > 2 )
                    System.out.println("collectFiles: " + strObjectName + "already processed." );
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
  
  
  
  static void indexFile(IndexWriter writer, hResult oResult,  int iMaxFileSizeMB, Long lTotalFiles, Long lCurrentFile)
        throws IOException {
        String strTitle = null ;
        String strTmp = null;
        
        String strID = null ;
        String pattern = "[[^a-z0-9A-Z]]";  //f. ID
        Integer iID ;
        
        Field fDocField = null ; //Lucene attribute field
        Field field_V50         = null ; //1.8.20.5.1
        FieldType ft    = null ; //1.8.20.5.1
        Document doc = null ;
    
        ft = new FieldType() ;                          
        ft.setStored(true) ;  //1.8.20.5.1 - gilt f. alle - werden gespeichert
                 
                 
                 //1.6.11 - delta processing
                 //even though it is tiresome to have read the file to decide wether to index it,
                 //it is necessary to get the MD5 and it will anyway accelerate the processing
                 //Es ist ein Unterschied, ob es ein Duplikat ist, oder einfach schonmal indiziert wurde.
                 //Das muss noch herausgearbeitet werden.
                if( null != oResult  ){
                    oResult.m_strHash =  fileLogger.md5( oResult.m_oPayLoad.toString() ) ;
                    
                    switch(deltaMan.check( oResult.m_strHash )){ 
                        case deltaMan.IS_CONTAINED:
                            // no logging IndexFilesApp.m_DupLog.log(file, strMD5 );
                            System.out.println("indexFile Skipped: deltaMan returned IS_CONTAINED [" + oResult.m_strFilename + "]" ) ;
                            return ;
                        case deltaMan.IS_DUP:
                            IndexFilesApp.m_DupLog.log(oResult );
                            System.out.println("indexFile Skipped: deltaMan returned IS_DUPLICATE [" + oResult.m_strFilename + "]" ) ;
                            return ;
                        default:
                            break ;
                    }                  
                    deltaMan.discovered(oResult.m_strHash, oResult.m_strFilename);  //enrich our map                    
                }
                 
                 switch (oResult.m_eRetType){
                     case hResult.DOCUMENT :
                         doc = (Document)oResult.m_oPayLoad ;
                         break ;
                     case hResult.CONTENT: 
                     case hResult.UNKNOWN : //a string                     
                          doc = new Document();
                          
                          field_V50 = new Field("contents", (String)oResult.m_oPayLoad, ft);       
                          //1.8.20.5.1 Deprecated Crap - aber ob das neue noch wirklich funktioniert?? - testen
                          doc.add(field_V50) ;
                          //doc.add(new Field("contents", (String)oResult.m_oPayLoad,
                          //          Field.Store.YES, Field.Index.ANALYZED)); 
                          
                          Long lCheckSum = hcc_utils.getCheckSum((String)oResult.m_oPayLoad) ;
                          strTmp = lCheckSum.toString() ;
                          field_V50 = new Field("checksum", strTmp, ft);  
                          /*
                          doc.add(new Field("checksum", 
                                    strTmp,
                                    Field.Store.YES, Field.Index.ANALYZED));
                          */
                          doc.add(field_V50) ;
                          strTmp = String.valueOf(oResult.m_lFileLength ) ;
                          field_V50 = new Field("checksum", strTmp, ft);  
                          doc.add(field_V50);
                          /*
                          doc.add(new Field("filesize", 
                                    strTmp,
                                    Field.Store.YES, Field.Index.ANALYZED));
                          */
                          break ; 
                     default:         
                         System.out.println("indexFile Skipped: unknown Content Type [" + oResult.m_strFilename + "]" ) ;
                          return ; //nothing to do
                 }
                 oResult.m_iPayLoadLen = oResult.m_oPayLoad.toString().length() ; 
            
            

        try {   

          // Add the path of the file as a field named "path".  Use a
          // field that is indexed (i.e. searchable), but don't tokenize 
          // the field into separate words and don't index term frequency
          // or positional information:
          
         
         
          Field pathField = new Field("URN", oResult.m_strPath, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
//          pathField.setIndexOptions(IndexOptions.DOCS_ONLY);
          doc.add(pathField);
          
          fDocField = new Field("ID", oResult.m_strPath.replaceAll(pattern, "_") , Field.Store.YES, Field.Index.ANALYZED);
     //     fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
          doc.add(fDocField); 
           
          
          
          
          Field dateField = new Field("date",  oResult.m_strLastModified, 
                                Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
       //   dateField.setIndexOptions(IndexOptions.DOCS_ONLY);
          doc.add(dateField);
          //System.out.println("adding field lastModified, value [" + strDate + "]");
          strTmp = System.getenv("COMPUTERNAME") ;
          if( null != strTmp && strTmp.length() == 0){
               strTmp = System.getenv("HOSTNAME") ;
          }
          if( null != strTmp && strTmp.length() > 0 ){
              Field hostName = new Field("source", oResult.m_strPath, Field.Store.YES, Field.Index.ANALYZED);
            //  pathField.setIndexOptions(IndexOptions.DOCS_ONLY);
              doc.add(hostName);                     
          }
          
          fDocField = new Field("doctype", oResult.m_strExt, Field.Store.YES, Field.Index.ANALYZED);
         // fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
          doc.add(fDocField);  
          
          //the Filename (without path) will be included as Title.
          strTitle = oResult.m_strFilename ;
          if(null != strTitle ){
            fDocField = new Field("title", strTitle, Field.Store.YES, Field.Index.ANALYZED);
           // fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
            doc.add(fDocField); 
          }
          
          //the Filename (without path) will be included as field, 
          //so users may search for parts of it (i.e. name:CV*).
          //Useful when searching for documents and remember parts of its name.
          fDocField = new Field("filename", oResult.m_strPath, Field.Store.YES, Field.Index.ANALYZED);
        //  fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
          doc.add(fDocField);  
          
          fDocField = new Field("year", oResult.m_strYear, Field.Store.YES, Field.Index.ANALYZED);
      //    fDocField.setIndexOptions(IndexOptions.DOCS_ONLY);
          doc.add(fDocField);  
          
          // Add the last modified date of the file a field named "modified".
          // Use a NumericField that is indexed (i.e. efficiently filterable with
          // NumericRangeFilter).  This indexes to milli-second resolution, which
          // is often too fine.  You could instead create a number based on
          // year/month/day/hour/minutes/seconds, down the resolution you require.
          // For example the long value 2011021714 would mean
          // February 17, 2011, 2-3 PM.
          org.apache.lucene.document.LongField modifiedField = 
                  new org.apache.lucene.document.LongField("lastModified", oResult.m_lLastModified, 
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
                    System.out.println("adding file [" + oResult.m_strFilename + "]");
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
                System.out.println("updating " + oResult.m_strFilename);
            writer.updateDocument(new Term("URN", oResult.m_strPath), doc);
            if( IndexFilesApp.m_iTraceLevel > 2)   
                System.out.println("done updating " + oResult.m_strFilename);
          }
         
        } 
         catch(Exception ex){
             System.out.println("-----indexFile-----> !!!File [" + oResult.m_strFilename + "] crashed with exception: " + ex.toString());
        }
        
        finally {            
              IndexFilesApp.m_ObjectLog.log(oResult);
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
            System.out.println("run::MD5DB error - " + ex.getMessage());
        }
    }
    
    public void run(){
        
       this.workDo() ;
      //if( true == m_bCloseIdxIndicator ) this.closeDex() ; //last thread should close
    }
}

class stateMonitor{
    public static final int LIMBO       =     0 ;  
    public static final int EXTRACTING = 10000 ;  
    public static final int DEXING     = 10001 ;  
    private int m_iCurrentState = 0 ;
    
    public stateMonitor(){
        this.m_iCurrentState = LIMBO ;
    }
    
    public void setState(int iState){
        this.m_iCurrentState = iState ;
    }
    public void setStateExtracting(){
        this.m_iCurrentState = EXTRACTING ;
    }
    public void setStateIndexing(){
        this.m_iCurrentState = DEXING ;
    }
     public void setStateLimbo(){
        this.m_iCurrentState = LIMBO ;
    }
    public boolean isExtracting(){
        return this.m_iCurrentState == EXTRACTING ;
    }
    public boolean isUpdating(){
        return this.m_iCurrentState == DEXING ;
    }
    public boolean isLimbo(){
        return this.m_iCurrentState == LIMBO ;
    }
}

/*

1.8.3.7 parallelize some stuff.
*/
class checkerT extends Thread{
    private hResult m_oResult = null ;
    private int     m_iRatio = 250 ; //ration between size and recognition.
    private int     m_iMinTxtLen = 1000 ;
   public checkerT(hResult r, int iMinTxtLen, int iRatio){
       m_oResult = r ;
       m_iMinTxtLen = iMinTxtLen ;
       m_iRatio     = iRatio ;
   }
    public void run(){
        //1.8.3.1 
            if( Indexer.poorContent(m_oResult, m_iMinTxtLen, m_iRatio)){
                IndexFilesApp.m_ReviewLog.log(m_oResult) ;              
            }
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
    public static Long     m_lReorgAfter = 4096L ; //reorg index after processing a certain number of files
    public Long            m_lCurrentFile = 0L ;
    public static String   m_strPath = null ;
    public boolean         m_bCloseIdxIndicator = false ;    
    public static Indexer  m_Indexer = null ;
    private stateMonitor   m_sMon    = null ;
    
    public dextorT( String strIndexPath, boolean bCreateIndex, 
        Long lReorgAfterNumFiles, boolean bCloseDex, stateMonitor oSMon){
        m_VFiles       = null ; 
        m_strPath = strIndexPath ;
        m_bCloseIdxIndicator = bCloseDex ;
        m_sMon = oSMon ;
        dextorT.m_lTotalFiles  = 0L ;
        dextorT.m_lReorgAfter = lReorgAfterNumFiles ;
        if( null == dextorT.m_Indexer ){  //Singleton
            System.out.println("dextorT: !----------------> indexer --------   o p e n i n g... ");
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
            System.out.println("closeDex: !----------------> indexer is null - cannot close... ");
            return false ;
        }
        System.out.println("closeDex: closing... ");
        dextorT.m_Indexer.closeIndex();
        dextorT.m_Indexer = null ;
        System.out.println("...Index closed");
        return true ;
    }
    
    public boolean reorg(){
        System.out.println("reorg: calling closeDex. ");
        this.closeDex();
        dextorT.m_Indexer = null ; //will cause a new creation on next run.
        //!!check if the create option true is really optimal and working.
        System.out.println("reorg: re-creating index. ");
        dextorT.m_Indexer = new Indexer(m_strPath,  
                                        false, //meaning: do append not create
                                        IndexFilesApp.m_iTraceLevel );
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
            System.out.println("###############run::processFiles error - Exception occured! ############" + ex.getMessage());
            
        }
      //if( true == m_bCloseIdxIndicator ) this.closeDex() ; //last thread should close
    }
    
    /*
        Das gibt Schwierigkeiten mit dem Indexer - ein Interrupt kann den IO Channel zerstören,
        dann kommt das ganze System durcheinander.
        :If a thread is blocked in an I/O operation on an interruptible channel 
        then another thread may invoke the blocked thread's interrupt method. 
        This will cause the channel to be closed
    */
    public void kill() throws java.nio.channels.ClosedByInterruptException{
        if(this.m_sMon.isExtracting() || this.m_sMon.isLimbo()){
          this.interrupt() ;
          System.out.println("--->!!!!!!!!!!!!!!!!!!     dt::kill - this thread gets killed... ");          
        }else{
            System.out.println("--->!!!!!!!!!!!!!!!!!!     dt::kill - !NOT IN EXTRACTING - cannot kill this thread... ");      
        
        }
        
        
        
        
          //throw new java.nio.channels.ClosedByInterruptException() ; //?warum das?
    }
    

    /*
        1.8.12.8 - split Operation int two parts:
        1. Extract Text
        2. Index
        in order to support interruption of thread.
    */
    public void processFile(IndexWriter w, File f) throws Exception{
        //Thread customization
        hResult  oResult = null ;
        checkerT ct      = null ;
        this.setName(f.getName()); //Thread's name
        System.out.println("<processFile> ---------> Current Docs in Index. Total: [" + w.numDocs() + "] In RAM [" + w.numRamDocs() + "]") ;
        try{
            m_sMon.setStateExtracting();
            //oResult =  oResult = dextorT.m_Indexer.extractText(f, m_lTotalFiles, m_lCurrentFile) ;
//2018 using Apache Tika
            oResult = dextorT.m_Indexer.extractTextTika(f,Integer.MAX_VALUE, m_lTotalFiles, m_lCurrentFile) ;            
            if(oResult.m_iStatusCode != hResult.STATUS_OK){
                System.out.println("<processFile> ---------> SKIPPED. Status was [" + oResult.m_iStatusCode + "]") ;
                return ;
            }
                
            //1.8.3.1 
            ct = new checkerT(oResult, 1000, 100) ;
            ct.run(); //replaces direct call to poorContent
            /*
            if( Indexer.poorContent(oResult, 1000, 250)){
                IndexFilesApp.m_ReviewLog.log(oResult) ;              
            }
            */
 /*           
 if( 2112 == 2112){
     return ;
 } 
*/
            
            if(this.isInterrupted()){  //1.8.12.8
                //thread was killed - when calling indexFile then the whole process would crash.                
                System.out.println("processFiles: Error this thread was interrupted. ");
            }
            else{
                m_sMon.setStateIndexing();
                IndexFilesApp.indexFile(w, oResult, 5, m_lTotalFiles, m_lCurrentFile);
            }
            m_sMon.setStateLimbo();
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
            throw new IOException( ex.toString() ) ;
        }        
    }
    
    public void processFiles() throws java.nio.channels.ClosedByInterruptException, Exception{    
        
        if( null == dextorT.m_Indexer.m_idxWriter ){ 
            System.out.println("processFiles: Error Indexer.m_idxWriter is null. ");
            return ;  //MORE HANDLING!!!
        }
        
        for(int i=0;i<this.m_VFiles.size();i++){
            m_lCurrentFile++ ;
            if( IndexFilesApp.m_iTraceLevel > 2 )
                System.out.println("Vector Element "+ i +" :" + this.m_VFiles.get(i));
            try{
                processFile( m_Indexer.m_idxWriter, new File(this.m_VFiles.get(i).toString()) ) ;
            }catch( Exception ex){
                throw ex ; 
            }
        }
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
