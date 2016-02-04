/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.result;
import java.util.regex.* ;
/**
 *
 * @author frankkempf
 * Extract a suitable shortText from a Document text
 */
public class documentShorttext {
    private String m_strShortText = null ;

    public documentShorttext() {
    }
    
    public documentShorttext(String strDocumentText, String strQuery){
        
    }
    
    //get the short text
    //A short Text should reflect the query to present the reader with a small extract of relevant text.
    //Here we  do a simple extract for a single word of the query - to be refined.
    public static String shortText(String strDocumentText, String strQuery, int iMaxCharsDistance,
                                   boolean bMarkupHiglight, String strHighlightClass, int iMaxLen){
        String[] rQuery = null ;
        String strPattern = new String("(([\\+]{0,1}[a-zA-Z0-9]*:.*) )?") ; //to macht URN:/
        
        int i = 0 ;
        //replace some stuff that could stem from a wildcard search or so
        strQuery = strQuery.replaceAll("[\\+\"\\*]", "") ;
        rQuery = strQuery.split(" ") ;
        for( i = 0; i < rQuery.length; i++){
            rQuery[i] = rQuery[i].replaceAll(strPattern, "") ;            
        }        
        return documentShorttext.getSentence(strDocumentText, rQuery, iMaxCharsDistance, bMarkupHiglight, strHighlightClass, iMaxLen) ;
    }
    
    
    /*
     * From the document text try to extract a sentence with the relevant keyword
     * In this Version we simplify to match a single keyword.
     * The start of the sentence is:
     * a. At a preceding '.'
     * b. Maximum N-Characters before from the keyword.
     * c. If not a and b then at the start of the text
     */
    public static String getSentence(String strDocumentText, String[] rKeyWords, int iMaxCharsBeforeKeyword,
                                      boolean bHighlightMarkup, String strHighlightClass, int iMaxLen){
        int i = 0;
        int iStartPos = 0 ;
        int iEndPos = 0 ;
        int iLen = 0 ;
        String strRet = null ;
        String strMatchingKeyword = rKeyWords[0] ; //for temporary storing the Keyword
        StringBuilder sbRet = new StringBuilder() ;
        StringBuilder sbPattern = new StringBuilder() ;
        for(i= 0; i<rKeyWords.length;i++){
            iStartPos = strDocumentText.indexOf(rKeyWords[i]);
            if(iStartPos >= 0 ){
                strMatchingKeyword = rKeyWords[i] ;
                iStartPos = documentShorttext.getStartOfSentence(strDocumentText, iStartPos, iMaxCharsBeforeKeyword, '.') ;
                break; 
            }
        }
        
        if( iStartPos < 0) iStartPos = 0 ;
        //determine from where to start searching for the end of the sentence
        iEndPos = iStartPos + iMaxLen ;
        if( iEndPos >= strDocumentText.length() ){
            iEndPos = strDocumentText.length() ;
        }
        else{
            iEndPos = getEndOfSentence( strDocumentText, iStartPos,  iEndPos, iMaxLen) ;
        }
        
        
        if( 0 == iEndPos ){
            iLen = strDocumentText.length() - iStartPos ;
        }
        else{
            iLen = iEndPos - iStartPos ;
        }
        
        
        if( iLen > iMaxLen  ) iLen = iMaxLen ;
        //set the short text
        strRet = strDocumentText.substring(iStartPos, (iStartPos + iLen)) ;
        //Style f. Highlight, wenn angefordert.
        if( true == bHighlightMarkup){ //Using BACKREF $0
            /* obsolete: use hcc_utils.highlightHTML instead
            sbPattern.append("(") ;
            sbPattern.append(strMatchingKeyword) ;
            sbPattern.append(")") ;
            strRet = strRet.replaceAll(sbPattern.toString(), "<span class=\"highlightText\">$0</span>") ;
            */
        }
        
        sbRet.append(strRet) ;
        sbRet.append("...") ;
        
        return sbRet.toString() ;     
    }
    
    /*
     *  From a keywords position, try to find the start of the sentence, where the Keyword is located.
     * iMaxCharsBeforeKeyword: How may characters backwards from the matching keyword do we start our sentence at most.
     */
    public static int getStartOfSentence(String strDocumentText, int iKeywordPos, int iMaxCharsBeforeKeyword, char cFindThis ){
        int iPos = 0 ;
        int iStartRev = strDocumentText.length() - iKeywordPos ;
        //"Rückwärtssuche"
        String strReverse = new StringBuffer(strDocumentText).reverse().toString();
        iPos = strReverse.indexOf(cFindThis, iStartRev) ;  //find .
        //iPos is then of course the reverse position
        if(iPos > 0){
            iPos = strDocumentText.length() - iPos + 1 ;
            if( iKeywordPos - iPos > iMaxCharsBeforeKeyword ){ //don't go to far
                iPos = iKeywordPos - iMaxCharsBeforeKeyword ;
            }
        }
        else
            iPos = 0 ;
        
        return iPos ;
    }
    
    
    public static int findReverse(String strText, String strFindThis){
        int iPos = 0 ;
        String strReverse = new StringBuffer(strText).reverse().toString();
        iPos = strReverse.indexOf(strFindThis) ;  //fin
        return iPos ;
    }
    
    
    
    
    //Search for a '.' Stop
    //When exceeding the MaxLen then try to not cut a word but find a space before
    //Endpos is the current End
    public static int getEndOfSentence(String strDocumentText, int iStartPos, int iEndPos, int iMaxLen){
        int iPos = 0 ;
        int iLen = 0 ; 
        
        int iTxtLen = strDocumentText.length() ;
        
        iPos = strDocumentText.indexOf(".", iEndPos) ;  //find from startpos.
        if( iPos < 0) return 0 ;
        iLen =  iPos - iStartPos  ;
        //if(iLen > iMaxLen) iLen = iMaxLen ;
        
        if( iPos >=  iTxtLen ){ //das geht schonmal garnicht
            
            iPos = iTxtLen - documentShorttext.findReverse(strDocumentText.substring(iStartPos, iLen), " ") ;
            
        }    
        //a simplification: when maxlen is exceeded we return the next space position
        else if( iLen > iMaxLen ){
            //trotzdem könnte der Offset ausserhalb des Stringendes liegen
            if( iTxtLen < iStartPos + iMaxLen)
                iEndPos=iTxtLen;
            else
                iEndPos=iStartPos + iMaxLen ;
                
            //do a reverse search for a space
            iPos =  iStartPos + iMaxLen - documentShorttext.findReverse(strDocumentText.substring(iStartPos, iEndPos), " ") ;
                                
        }
        return iPos;
    }
    
    
    
    
}
