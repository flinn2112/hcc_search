/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_sensors;

import hcc_sensors.defs.clsAddressDef;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *Very Basic, just to start.
 * @author frank
 */
public class clsAddressSensor {
    public static int isAddress(String strText){
        int iRet = 0 ;
        Pattern p = Pattern.compile(clsAddressDef.get(), Pattern.CASE_INSENSITIVE ) ;
        Matcher m = p.matcher(strText) ;
        while(m.find()){
            iRet++ ;
            /*
                iPos0 = m.start() ;
                if( m.start() > iKeyStartPos) continue ;
                strTmp = strHTML.substring( m.start(), iKeyStartPos ) ; //in the substring there should not an </ closing tag be found before the Keyword
                
                iPos0 = strTmp.indexOf("</");
                
                if( iPos0 < 0 ){ //wenn nicht gefunden, dann ist es innerhalb des Strings.
                    return 0 ;
                }
            }
            */
        } 
        return iRet ;
    }
}
