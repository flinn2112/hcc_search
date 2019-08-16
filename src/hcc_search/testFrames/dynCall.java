/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.testFrames;

/**
 *
 * @author frankkempf
 */

import hcc_search.ExcelFileWrapper;
import hcc_search.indexer.IndexData;
import hcc_search.indexer.Indexer;
import hcc_search.hResult;
import hcc_search.hcc_exception;
import hcc_search.indexer.hcc_index_bean;
import hcc_search.hcc_utils;
import hcc_search.logger.fileLogger;
import hcc_search.myClassLoader;
import hcc_search.result.documentShorttext;
import java.io.* ;
import java.util.* ;
import searchRT.utils.mime;

public class dynCall{

	public static void main(String[] args) {
            String[] rArgs = new String[1]; 
            File f = new File( "dynCall.java" ) ;
            String strRet = null ;
            StringBuilder sbResult = new StringBuilder() ;
            myClassLoader ccl = new myClassLoader(0) ;
            System.out.println("main") ;
             Calendar calendar = Calendar.getInstance();
             Date today = calendar.getTime();
             calendar.add(Calendar.DATE, 365);
             Date dt = calendar.getTime() ;
             hcc_search.ht_container htc = new hcc_search.ht_container() ; 
             htc.m_ht = mime.ht_MapMime ;
             
             strRet = "X:/ScanRoot/2012/autobahn_min.pdf" ;
             strRet = strRet.replaceAll("[a-zA-z]:[\\/\\\\]", "") ;
             
             
             String strMime = searchRT.utils.mime.get(htc, 
                     "D:\\SharedDownloads\\Projekte\\hcc\\hcc_search_jsp\\build\\web\\config\\mimemap.txt", "doc", sbResult);
             System.out.println(hcc_utils.getDateString(dt)) ;
             
             strRet = html.htmlUtils.getClass("<p class=\"testclass\">zzz</p>") ;
  
             //strRet = hcc_utils.highlightHTML("<a href=\"#fox\">the quick brown fox jumps over the lazy bone</a>Trailer...", "FOX") ; 
             
             strRet = html.htmlUtils.highlightHTML("<p class=\"searchResultShortText\"><!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"> hcc::medical SAPscript to Smartforms SAPscriptnach Smartforms Konvertierung    DATE: 2012/2013 STATUS: PRODUCTIVE MANUFACTURER hcc::medical Anforderung Migration vonPMD Generator I nach Generator II inclusive SAPscript Formularen nach Smartforms. Code von PMDs(Parametrierbare Medizinische Dokumente) auf neue ...</p><p class=\"searchResultFileDetails\">http://www.hcc-medical.com/buildingblocks/pmd_migrate/hcc_pmd_migrate.php</p>", "hcc_pmd_migrate.php") ;
             
             
             String path = "z:\\xxxx\\dfgsdfg" ;
             String pattern = "([a-z]:)";
             
             strRet = documentShorttext.shortText("Dear Reader. Some say: \"the quick brown fox jumps over the lazy bone.\" What now? Nothing."
                       + "Parametrierbare Medizinische Dokumente Seit Jahren arbeiten wir im Bereich klinische Dokumentation mit PMD(parametrierbare medizinische Dokumente) und haben diesem Bereich durch unsere eigenen Verfahren neu definiert. "
                                  
                                  , "fox", 128,
                                  true, //boolean bMarkupHiglight, 
                                  "highlightText",   //String strHighlightClass)
                                  256
               ) ;
             path = path.replaceAll(pattern, "") ;
             path = path.replaceAll(pattern, "so.m_strSubstDriveLetter") ;
             
          System.out.println(path);
             
             
            //dynCall.testIndexBean();
            //dynCall.testExcelExtractor() ; 
             strRet = System.getProperty("user.dir") + File.separator + "logs" ;
             fileLogger fl = new fileLogger("Test.txt", strRet);
             fl.log("TestLog", "TEST", 0);
             String strLine = "aos	application/x-nokia-9000-communicator-add-on-software" ;
             String[] rData = null ;
             rData = strLine.split("\\s") ;
             rData = null ;
            /*
            IndexFilesApp a = new IndexFilesApp() ;
            a.collectFiles("/Users/frankkempf/Documents/Volume_2/SharedDownloads/2112Portals") ;
            for(int i=0;i<a.m_VFiles.size();i++)
            {
                //System.out.println("Vector Element "+i+" :"+a.m_VFiles.get(i));
            }
            System.out.println("Vector Size "+ a.m_VFiles.size());
            //strRet = PDFExtractor.processDocument2("/Users/frankkempf/Documents/Volume_2/SharedDownloads/Drei Weltreligionen (Koran-Buddha-Talmud).pdf");
            strRet = PDFExtractor.processDocument2("/Users/frankkempf/Documents/Volume_2/SharedDownloads/Dokus/Horstbox_DVA-G3342SD_howto_de_USB-Installation.pdf");
            rArgs[0] = "2112" ;         
            try{
                 ccl.invokeClassMethod("hcc_search.customSearch", "test", f) ;
            }
            catch(Exception ex){
                System.out.println("Failed [" + ex.toString() + "]" ) ;
            }
            */
            
        }//main
        /*
         * Adding plain text to an index
         */
        public static void testIndexer(){
            Indexer idx = new Indexer("/Users/frankkempf/Documents/index", false, 5);
            IndexData idxD = new IndexData("hcc_medical:test", 
                    "the quick brown fox jumps over the lazy bone", "TXT") ;
            try{
                idx.indexData(idxD);
                idx.closeIndex();
            }catch(hcc_exception ex){}
        }
        
        public static void testIndexBean(){
            
            hcc_index_bean idxB = new hcc_index_bean();
            Date now = new Date() ;
            idxB.index("/Users/frankkempf/Documents/index", 
                    "testdomain",
                    "TestTitle",
                    "TestShortText",
                    "/Users/frankkempf/Documents/memos",
                    "test:test:" + idxB.getDateString(), idxB.getDateString(), "memo", "", "INet", "", now,
                     "the quick brown fo jumps over the lazy bone", null);
            
            
            
        }
        
        public static void testExcelExtractor(){
              FileInputStream fis = null ;
              BufferedReader br = null ;
              StringBuilder sb = new StringBuilder() ;
              String strTmp = new String() ;
              hResult oResult = new hResult() ;
              File file = new File("/Users/frankkempf/Documents/V1_clalit_requirements.xls");
              ExcelFileWrapper xls = null ;
              String strRet = null ;
      
      try {
          fis = new FileInputStream(file);
          

            } catch (FileNotFoundException ex) {
              // at least on windows, some temporary files raise this exception with an "access denied" message
              // checking if the file can be read doesn't help         
         
        }
       try{
            xls = new ExcelFileWrapper(fis) ;
            strRet = xls.readContents() ;
        }
       catch( IOException ex2){
    
        }
        System.out.println(strRet) ;
   }
       

}
