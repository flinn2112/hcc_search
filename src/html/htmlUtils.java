/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package html;

import hcc_search.hcc_utils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author hcc
 */
public class htmlUtils {
   
    /*
     * From a single HTML Element get the class name (<p class="myClass">TEXT</p>
     */
    public static String getClass(String strHTML){
        Pattern p = Pattern.compile(".*class=\"(.*)\".*", Pattern.CASE_INSENSITIVE ) ;
        Matcher m = p.matcher(strHTML) ;
        String strRet = null ;
        if( m.matches())
        {        
            strRet = m.replaceFirst("$1") ;
        }
        return strRet ;
    }
    
    /*
     * Get the HTML Context of a Substring.
     * Used by highlightHTML to determine the Environment of a given keyword
     * 1 ist OK
     * 0 - nicht highlighten
     */
    public static int getHTMLContext(String strHTML, int iKeyStartPos, int iKeyEndPos){
        int iPos0 = 0 ;
        int iPos1 = 0 ;
        int i = 0 ;
        String strTmp = null ;
        
        if( (iKeyEndPos - iKeyStartPos) < 3 ){ //kleine Bremse
            return 0 ;
        }
        
        
        
        //is it within a TAG? i.e. PMD -> <a href="#PMD">PMD</a> 
        //System.out.println(strHTML.substring(iStartPos, iEndPos)) ;
        iPos0 = strHTML.indexOf(">", iKeyEndPos);
        
        if(iPos0 > 0){ //wenn gefunden, dann darf aber kein < vorher kommen
            iPos1 = strHTML.indexOf("<", iPos0);
            //zwischen > und < darf dann aber kein '<' kommen
            //System.out.println(strHTML.substring(iEndPos, iPos1)) ;
            if( iPos1 > iPos0 &&  strHTML.substring(iKeyEndPos, iPos1).contains("<") == false ){ //wenn die Klammer auf erst nach der Klammer zu kommt - es ist innerhalb eines TAGS <a href="...keyword">                                
                return 0 ;
            }
             //kein else -> weiter   
        }
        //is it within a TAG?
        iPos0 = strHTML.indexOf("</", iKeyEndPos);
        if(iPos0 > 0){
            iPos1 = strHTML.indexOf(">",  iPos0) ;
            if(iPos1 > 0){
                strTmp = strHTML.substring(  iPos0+2,  iPos1) ; 
                //das tag ist SPAN - aber nur, wenn nicht ein offenes span nach dem keyword kommt.
                //System.out.println(strHTML.substring(iStartPos, iPos1)) ;
                if( strTmp.toLowerCase().equals("span") &&  strHTML.substring(iKeyEndPos, iPos0).contains("<span") == false ){ //no nesting span
                    return 0 ;                     
                }                
            }
            
            
            
            
           //do not replace within the file details paragraph (searchResultFileDetails)
            //walk reverse direction from the keyword start and get the start of the < bracket
            
            //da jetzt =ssalc.*> 
            Pattern p = Pattern.compile("searchResultFileDetails", Pattern.CASE_INSENSITIVE ) ;
            Matcher m = p.matcher(strHTML) ;
            //if we find this string, we need to check if our Keyword is within this TAG
            while(m.find()){
                iPos0 = m.start() ;
                if( m.start() > iKeyStartPos) continue ;
                strTmp = strHTML.substring( m.start(), iKeyStartPos ) ; //in the substring there should not an </ closing tag be found before the Keyword
                
                iPos0 = strTmp.indexOf("</");
                
                if( iPos0 < 0 ){ //wenn nicht gefunden, dann ist es innerhalb des Strings.
                    return 0 ;
                }
            }
        } 
        
        return 1 ;
    }
    
    /*
     * This is quite crude.
     * TODO: 
     *    1. do not replace within anchor tags(it would destroy the link)
     *    2. do not replace within span tags
     */
    public static String highlightHTML(String strIn, String strKeyword){
        StringBuilder sbPattern = new StringBuilder() ;
        StringBuilder sbResult = null ;
        int iStart = 0 ;
        int iEnd   = 0 ;
        int iOrigin = 0 ;
        int iCount =  0 ;
        
        sbPattern.append("(") ;
        sbPattern.append(strKeyword) ;
        sbPattern.append(")") ;
        
        Pattern p = Pattern.compile(sbPattern.toString(), Pattern.CASE_INSENSITIVE ) ;
        Matcher m = p.matcher(strIn) ;
        
        
        sbResult = new StringBuilder() ;
        while(m.find()){
            iStart = m.start() ;
            iCount++ ;
            sbResult.append(strIn.substring(iOrigin, iStart)) ; //Übernehmen bis zum Treffer
            
            iEnd   = m.end() ;
            iOrigin = iEnd ; //weiter
            if( 0 == html.htmlUtils.getHTMLContext(strIn, iStart, iEnd) ){
                //sbResult.append("----- no replacement -----") ;
                sbResult.append( strIn.substring(iStart, iEnd)) ; //ohne Änderung übernehmen
            }
            else  //das Keyword zwischen Start und End highlighten...
                sbResult.append(p.matcher(strIn.substring(iStart, iEnd)).replaceAll("<span class=\"highlightText\">$0</span>"));
            //sbResult.append(" n e x t   h i t") ;
        }
        if( 0 == iCount )
            return strIn ; //komplett übernehmen
        
        //when there is content beyond the last hit
        if( iOrigin < strIn.length() ){
            sbResult.append( strIn.substring(iOrigin, strIn.length())) ; 
        }
        return sbResult.toString() ;
        
        //return  strIn.replaceAll(p, "<span class=\"highlightText\">$0</span>") ;
    }
    
    /*
     * highlightQuery should parse a query and make use of the keywords within to highlight each of it.
     */
    public static String highlightQuery(String strIn, String strQuery){
        String[] rStrings = null ;
        String strRet = null ;
        int i = 0 ;
        strQuery = strQuery.replaceAll("[\\*\\+]", "") ;
        rStrings = strQuery.split(" ") ;
        strRet = strIn ;
        for( i = 0; i< rStrings.length;i++){
            strRet = html.htmlUtils.highlightHTML( strRet, rStrings[i]) ;
        }
        return strRet ; //sb.toString() ;
    }
}
