/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.result;

import hcc_search.hcc_search_ExtendedAttributes;
import hcc_search.result.searchResultOut;
import hcc_search.indexer.IndexData;

/**
 *
 * @author frankkempf
 */
public class searchResult_ISHMED_HTML implements searchResultOut{
    @Override
    public String out(String strTitle, String strFilename, String strLastModified, hcc_search_ExtendedAttributes o){
        return "" ;
    }
    public String out(String strTitle, String strFilename, String strLastModified){
        
        return "<p class=\"search_result_p\">"
                + "<a href=\"/hcc_search_jsp/getContentSrvlt?fullpath=" 
                + strFilename + "\" target=\"_blank\">" 
                + strTitle +  "</a>\n"
                + "&nbsp;&nbsp;&nbsp;<small class=\"lastModified\">" + strLastModified +  "</small></p>\n"
                + "<p class=\"searchResultFileDetails\">" + strFilename + "</p>\n" ;
    }  
    public String out(IndexData idxD[]){
        return "" ;
    }
    public String outURL(String strTitle, String strShortText, String strURL){
        return null ;
    }
}
