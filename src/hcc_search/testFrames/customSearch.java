/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.testFrames;
 
import hcc_search.search.customSearchCtrl;
import hcc_search.hcc_search_Bean;
import hcc_search.hcc_search_opts;
import hcc_search.hcc_utils;
import hcc_search.result.searchResultHTML;
import hcc_search.result.searchResultJSON;
import hcc_search.logger.fileLogger ;

/**
 *
 * @author frankkempf
 */

public class customSearch{
  public static void main(String[] args) throws Exception {
       customSearchCtrl cs = null  ;
       hcc_search_opts so = null ;
       String strResult = null ;
       hcc_search.result.sResult oResult  = null ;
       fileLogger fLog = new fileLogger("customSearch.java", "d:\\SharedDownloads\\Projekte\\hcc\\hcc_search\\hcc_search.index.jsp.txt" );         
             
       hcc_search.hcc_search_Bean searchBean = new hcc_search.hcc_search_Bean() ; 
  //test  () ;   
       //hcc_utils.getDocument("/Users/frankkempf/java_src/url.html") ;
       
       cs = new customSearchCtrl() ;
       //testURNSearchAndHighlighting() ;
       //testContentSearchAndHighlighting() ;
       
       so = new hcc_search_opts("+revolt*", "contents", "d:\\SharedDownloads\\Projekte\\hcc\\hcc_search\\index",
               false, hcc_search_opts._HTML_, null, true, "highlightText" ) ;
       
       fLog.log("index.jsp.log - query.", "Index: " + so.m_strIndexDir, 0) ; 
       fLog.log("index.jsp.log - query.", "Field: " + so.m_strField, 0) ; 
       
       
       //oResult = cs.search2(so,  new searchResultHTML());    
       
       //oResult = searchBean.search2(  so.m_strIndexDir,  so, null, false);
       oResult = searchBean.search2(so.m_strIndexDir, so, null, false);
       //hcc_utils.escapeTerm("healthcare-components.com/inforecord/hcc_inforecord.php?lang=en")
  
       so = new hcc_search_opts(hcc_utils.escapeTerm("healthcare-components.com/inforecord/hcc_inforecord.php"), 
               "URN", "d:\\SharedDownloads\\Projekte\\hcc\\hcc_search\\index", true,  
               hcc_search_opts._HTML_, "TEST") ;   
     
       oResult = cs.search2(  so, new searchResultHTML()) ;
       
       System.out.println("   Result: " + oResult.m_strSearchResult );
       //hcc_search_Bean b = new hcc_search_Bean() ;
       //b.search("/Users/frankkempf/Documents/index", "GRN") ;
  }
  
  
  
  public static String testURNSearchAndHighlighting() throws Exception{
       hcc_search.hcc_search_Bean oSearchBean = new hcc_search.hcc_search_Bean() ; 
   String  strFSPath = null ;
   String  strQuery  = null ; 
   String strRet = null ;
   hcc_search.hcc_search_opts so = null ;
   hcc_search.result.sResult oResult  = null ;
     strQuery = hcc_utils.escapeTerm("2112portals.com/PMD_ShowCases/DialysePMD_2010.htm") ;
                    so = new hcc_search.hcc_search_opts( 
                             strQuery,
                             "URN", 
                            "\\\\DLINK-12F187\\Volume_1\\SharedDownloads\\Projekte\\hcc\\index", 
                            true, 
                             hcc_search_opts._HTML_, null ) ;   
                    
                    oResult = oSearchBean.search2(  so.m_strIndexDir,  so, null, false);
                    if( null != oResult ){                        
                        strRet = html.htmlUtils.highlightQuery(oResult.m_strSearchResult, "dialyse") ; 
                        strRet += html.htmlUtils.highlightHTML(strRet, "smartforms") ; 
                    }
        return strRet ;
  }
  
  public static String testContentSearchAndHighlighting() throws Exception{
       hcc_search.hcc_search_Bean oSearchBean = new hcc_search.hcc_search_Bean() ; 
   String  strFSPath = null ;
   String  strQuery  = null ; 
   String strRet = null ;
   hcc_search.hcc_search_opts so = null ;
   hcc_search.result.sResult oResult  = null ;
     strQuery = "sapscript" ;
                    so = new hcc_search.hcc_search_opts( 
                             strQuery,
                             "CONTENT", 
                            "\\\\DLINK-12F187\\Volume_1\\SharedDownloads\\Projekte\\hcc\\index", 
                            true, 
                             hcc_search_opts._HTML_, null ) ;   
                    
                    oResult = oSearchBean.search2(  so.m_strIndexDir,  so, null, false);
                    if( null != oResult ){                        
                        strRet = hcc_utils.highlightQuery(oResult.m_strSearchResult, "+Sapscript SmartF*") ; 
                        strRet += hcc_utils.highlightHTML(strRet, "smartforms") ; 
                    }
        return strRet ;
  }
}

