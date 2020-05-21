/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_sensors;

import hcc_miners.clsSensor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rechnung erkennen
 * @author frank
 */
public class clsInvoiceSensor extends clsSensor{
    
    //ein
    public int hasDate( String strText ){        
        return 0 ;
    }
    /*
        Rechnung, Invoice, Kostennote
    */
    public int hasInvoiceToken( String strText ){        
        return 0 ;
    }
    public int hasIBAN( String strText ){        
        return 0 ;
    }
    
    public int hasAdress(String strText){
        Pattern p = Pattern.compile(".*class=\"(.*)\".*", Pattern.CASE_INSENSITIVE ) ;
        Matcher m = p.matcher(strText) ;
        String strRet = null ;
        while(m.find()){
            
        /*
                iPos0 = m.start() ;
                if( m.start() > iKeyStartPos) continue ;
                strTmp = strHTML.substring( m.start(), iKeyStartPos ) ; //in the substring there should not an </ closing tag be found before the Keyword
                
                iPos0 = strTmp.indexOf("</");
                
                if( iPos0 < 0 ){ //wenn nicht gefunden, dann ist es innerhalb des Strings.
                    return 0 ;
        */
        }
        return 0 ;
    }
}
