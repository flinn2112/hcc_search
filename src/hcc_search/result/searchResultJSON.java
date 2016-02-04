/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.result;

import hcc_search.hcc_search_ExtendedAttributes;
import hcc_search.indexer.IndexData;

/**
 *
 * @author frankkempf
 */
public class searchResultJSON implements searchResultOut{
    @Override
    public String out(String strTitle, String strFilename, String strLastModified, hcc_search_ExtendedAttributes o){
        return "" ;
    }
    public String out(String strTitle, String strFilename, String strLastModified){
        
        return "{ \"Title\":\"" + strTitle + "\", Filename\":\"" 
                + strFilename + "\", \"lastModified\":" 
                + strLastModified +  "\"}\n" ;
    }   
    public String out(IndexData idxD[]){
        return "" ;
    }
    public String outURL(String strTitle, String strShortText, String strURL){
        return null ;
    }
}
