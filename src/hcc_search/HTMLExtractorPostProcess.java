/*
   ____  ____ _____ ____  __    ____________________
  / __ \/ __ ) ___// __ \/ /   / ____/_  __/ ____/ /
 / / / / __  \__ \/ / / / /   / __/   / / / __/ / / 
/ /_/ / /_/ /__/ / /_/ / /___/ /___  / / / /___/_/  
\____/_____/____/\____/_____/_____/ /_/ /_____(_)   
                                                    

    ____  __________  __    ___   ________________     ______  __
   / __ \/ ____/ __ \/ /   /   | / ____/ ____/ __ \   / __ ) \/ /
  / /_/ / __/ / /_/ / /   / /| |/ /   / __/ / / / /  / __  |\  / 
 / _, _/ /___/ ____/ /___/ ___ / /___/ /___/ /_/ /  / /_/ / / /  
/_/ |_/_____/_/   /_____/_/  |_\____/_____/_____/  /_____/ /_/   
                                                                 
  ____________ __ ___ 
 /_  __/  _/ //_//   |
  / /  / // ,<  / /| |
 / / _/ // /| |/ ___ |
/_/ /___/_/ |_/_/  |_|
                      
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
