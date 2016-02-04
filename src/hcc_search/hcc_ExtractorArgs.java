/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search;
import java.io.* ;
/**
 *
 * @author frankkempf
 */

/*
public class hcc_ExtractorArgs {
    public InputStream m_is ;
    public String m_strEncoding = null ;
}
*/
//Wrapping Input Stream for DynCalls(WebCrawler).
class hcc_ExtractorArgsInputStream{
    public InputStream m_is ;
    public String m_strEncoding = null ;
    public IPostprocessExtractor m_ppEx = null ;
    
    public hcc_ExtractorArgsInputStream(){
        m_is = null ;
        m_strEncoding = "UTF-8" ;
    }
    public hcc_ExtractorArgsInputStream(InputStream is, String strEncoding ){
        m_is = is ;
        m_strEncoding = strEncoding ;
    }
    
    public hcc_ExtractorArgsInputStream(InputStream is, String strEncoding, IPostprocessExtractor oPostProcessor ){
        m_is = is ;
        m_strEncoding = strEncoding ;
        m_ppEx = oPostProcessor ;  //can be null
    }
}

