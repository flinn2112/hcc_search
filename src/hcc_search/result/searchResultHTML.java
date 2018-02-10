/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.result; 
import hcc_search.hcc_search_ExtendedAttributes;
import hcc_search.indexer.IndexData;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
/**
 *
 * @author frankkempf
 */
public class searchResultHTML implements searchResultOut{
    
    
    private boolean  m_bGenLocalPath ; //Controls  generation of links (true = file:\\
    private String   m_strPreferredHost ; //result will be served by a local server(security)
    private String   m_strClientPrefs ;
    public searchResultHTML(){
        m_bGenLocalPath = false ;
        
    }
    public searchResultHTML(String strClientPrefs){
        m_bGenLocalPath    = false ;
        m_strPreferredHost = "" ;  //yes empty not null
        if(null == strClientPrefs){
            return;
        }
        m_strClientPrefs = strClientPrefs ;
        m_bGenLocalPath = strClientPrefs.contains("useFile") ; //actually this will not work since browser do not support the file:/// much
        if(!m_bGenLocalPath){//no local path found maybe a preferred server
            if( strClientPrefs.startsWith("http")){
                m_strPreferredHost = strClientPrefs ; 
            }
        }
    }
    
    
    @Override
    
    public String out(String strTitle, String strFilename, String strLastModified, hcc_search_ExtendedAttributes oEA){
        String strRet = null ;
        URL oURL = null ;
        StringBuilder sb = new StringBuilder() ;
        /*
        try{
            -> would display some nonsense chars: strFilename = URLEncoder.encode( strFilename, "UTF-8") ;
        }
        catch(UnsupportedEncodingException ex){
        }
        */
        //localpath? - when files are stored on the local harddrive
        if( true == m_bGenLocalPath){
            sb.append("<p class=\"search_result_p\">" );
            sb.append("<a href=\"file:///" );
            sb.append( strFilename + "\" target=\"_blank\">" ); 
            sb.append(strTitle +  "</a>\n" );
            sb.append("&nbsp;&nbsp;&nbsp;<small class=\"lastModified\">" + strLastModified +  "</small></p>\n" );
            sb.append("<p class=\"searchResultFileDetails\">Local:" + strFilename + "</p>\n" );
        }
        else{
                sb.append( "<p class=\"search_result_p\">" ) ;
                sb.append( "<a href=\"");
                sb.append( m_strPreferredHost ) ;  //1.8.1.4
                sb.append( "/hcc_search_jsp/getContentSrvlt?fullpath=" ) ;
                sb.append(  strFilename + "\" target=\"_blank\">" ) ;
                sb.append(  strTitle +  "</a>\n" ) ;
                sb.append(  "&nbsp;&nbsp;&nbsp;<small class=\"lastModified\">" + strLastModified +  "</small></p>\n" ) ;
                sb.append(  "<p class=\"searchResultFileDetails\">" + strFilename + "</p>\n" ) ;
             
             //Append extended Attributes if available.
                if( null != oEA ){
                    if( null != oEA.m_strFilesize )
                        sb.append(  "<p class=\"searchResultFileDetails\">" ) ; 
                        if( null!= oEA.m_strFilesize){
                            sb.append(  "Size:&nbsp;" ) ;
                            sb.append(  oEA.m_strFilesize ) ;
                        }
                        if( null!= oEA.m_strCheckSum ){
                            sb.append(  "&nbsp;CX:&nbsp;" ) ;
                            sb.append(  oEA.m_strCheckSum ) ; 
                        }
                        sb.append("</p>\n" ) ;
                }
        }
        
        return sb.toString() ;
    }
    
    public String out(String strTitle, String strFilename, String strLastModified){
        String strRet = null ;
        URL oURL = null ;
        
        
        if( true == m_bGenLocalPath){
            /*
            try{
                oURL = new URL("file://" + strFilename ) ;
            }
             catch (MalformedURLException ex) {
               strRet = "Error in URL" + ex.toString() ;   
               return strRet ;
            }
            */
            
            strRet =  "<p class=\"search_result_p\">"
                + "<a href=\"file:///" 
                + strFilename + "\" target=\"_blank\">" 
                + strTitle +  "</a>\n"
                + "&nbsp;&nbsp;&nbsp;<small class=\"lastModified\">" + strLastModified +  "</small></p>\n"
                + "<p class=\"searchResultFileDetails\">Local:" + strFilename + "</p>\n" ;
        }
        else{
             strRet =  "<p class=\"search_result_p\">"
                + "<a href=\"/hcc_search_jsp/getContentSrvlt?fullpath=" 
                + strFilename + "\" target=\"_blank\">" 
                + strTitle +  "</a>\n"
                + "&nbsp;&nbsp;&nbsp;<small class=\"lastModified\">" + strLastModified +  "</small></p>\n"
                + "<p class=\"searchResultFileDetails\">" + strFilename + "</p>\n" ;
        }
        
        return strRet ;
    }
    
    /*file://D/SharedDownloads/Projekte/GRN/PMD2011/VDoku/VDokuPMD_2011.doc
     * Output of Webcrawler results (bit different to local search).
     */
    public String outURL(String strTitle, String strShortText, String strURL){
        
        return "<p class=\"search_result_p\">"
                + "<a href=\"" 
                + strURL + "\" target=\"_blank\">" 
                + strTitle +  "</a>\n"
                + "&nbsp;&nbsp;&nbsp;</p>\n"
                + "<p class=\"searchResultShortText\">"   + strShortText + "</p>\n"
                + "<p class=\"searchResultFileDetails\">" + strURL + "</p>\n" ;
    }
    
    
    public String out(IndexData idxD[]){
        return "" ;
    }
}
