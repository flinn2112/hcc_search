/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.result;
import hcc_search.hcc_search_ExtendedAttributes;
import hcc_search.indexer.IndexData;
import java.net.MalformedURLException;
import java.net.URL;
/**
 *
 * @author frankkempf
 */
public class searchResultPaths implements searchResultOut{
    
    
    private boolean  m_bGenLocalPath ; //Controls  generation of links (true = file:\\
    private String   m_strClientPrefs ;
    public searchResultPaths(){
        m_bGenLocalPath = false ;
        
    }
    public searchResultPaths(String strClientPrefs){
        m_bGenLocalPath = false ;
        if(null == strClientPrefs){
            return;
        }
        m_strClientPrefs = strClientPrefs ;
        m_bGenLocalPath = strClientPrefs.contains("local") ;
    }
    
    
    @Override
    
    public String out(String strTitle, String strFilename, String strLastModified, hcc_search_ExtendedAttributes oEA){
        String strRet = null ;
        URL oURL = null ;
        StringBuilder sb = new StringBuilder() ;
                
        sb.append( strFilename ) ; 
        sb.append("\n") ;
        return sb.toString() ;
    }
    
    public String out(String strTitle, String strFilename, String strLastModified){
        
       StringBuilder sb = new StringBuilder() ;
        
        //localpath? - when files are stored on the local harddrive
        
        sb.append( strFilename ) ;
        sb.append( "\n" ) ;
        
        
        
        return sb.toString() ;
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
