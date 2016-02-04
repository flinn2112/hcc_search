/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
/**
 *
 * @author frankkempf
 */
public class HTMLExtractorPostProcess implements IPostprocessExtractor{
    private Document m_JSoupDoc = null ; 
    public HTMLExtractorPostProcess(){
        
    }
    public String postProcess(String strIn){
        Element elBody = null ;
        m_JSoupDoc =  Jsoup.parse(strIn) ;
        elBody = m_JSoupDoc.body() ;   
        return elBody.text() ;
    }
    
}
