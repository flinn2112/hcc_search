/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.testFrames;
import hcc_search.search.customSearchCtrl;
import hcc_search.hcc_search_Bean;
import hcc_search.hcc_search_opts;
import hcc_search.hcc_utils;
import hcc_search.logger.fileLogger ;

import hcc_search.PDFExtractor ;
import hcc_search.config.IConfigProcessor;
import hcc_search.hResult ;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.* ;

/**
 *
 * @author hcc
 */
enum _CLASS_ {
    FUEL, BANKSTATEMENT, INVOICE
}

class classResult{
    public _CLASS_ m_eType ;
    public String  m_strType ;
    public String  m_strExtract ;
    
}


class classData{
    public static String extractValues(String strText){
        return null ;
    }
    
    public static boolean isNear(String strTxtLower, String strWhat, int iStart, int iEnd, int iDist){
        
        String strTest = null ;
        int iOffsStart = 0 ;
        int iOffsEnd   = 0 ;
        StringBuilder sb = new StringBuilder() ;
        sb.append(".*") ;
        sb.append(strWhat) ;
        sb.append(".*") ;
        Pattern pattern = Pattern.compile(sb.toString());
        
        if( null == strTxtLower) return false ;
        iOffsStart =  (iStart < iDist)?0: iStart - iDist ; //do not go less than zero
        iOffsEnd   = ( (iEnd + iDist) > strTxtLower.length())?strTxtLower.length():iEnd + iDist ;
        
        //get the string
        strTest = strTxtLower.substring(iOffsStart, iOffsEnd) ;
        Matcher matcher = pattern.matcher(strTest);
        
      
       
        return matcher.find() ;
        
    }
    
}

class classDataBankstatement extends classData{
    public static String extractValues(String strTextLower){
         //Pattern for a date
        Pattern pattern = Pattern.compile("kontoauszug");
        boolean bFound = false ;
        String strTmp = null ;
        String strNear = null ;
        StringBuilder sb = new StringBuilder() ;
        
        // In case you would like to ignore case sensitivity you could use this
        // statement
        // Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(strTextLower);
        
        bFound = matcher.find() ;
        strTmp = "" ;
        if( true == bFound )
        {
           strTmp = strTextLower.substring(matcher.start(), matcher.end()) ;
           System.out.println("Datum: " + strTmp ) ;     
           //strTmp = 
        }
        sb.append(strTmp.trim()) ;
        sb.append(";") ;
        //Time 01:34:21
        pattern = Pattern.compile("[0-9]{2}:[0-9]{2}:[0-9]{2}\\s");
        matcher = pattern.matcher(strTextLower);
        
        bFound = matcher.find() ;
        strTmp = "" ;
        if( true == bFound )
        {
           strTmp = strTextLower.substring(matcher.start(), matcher.end()) ;
           System.out.println("Uhrzeit: " + strTmp ) ;          
        }
        sb.append(strTmp.trim()) ;  
        sb.append(";") ;
        pattern = Pattern.compile(".diesel.|e10|super");
        matcher = pattern.matcher(strTextLower);
        
        bFound = matcher.find() ;
        strTmp = "" ;
        if( true == bFound )
        {
           strTmp = strTextLower.substring(matcher.start(), matcher.end()) ;
           System.out.println("Treibstoff: " + strTmp ) ;           
        }
        sb.append(strTmp.trim()) ;
        sb.append(";") ;
        //Menge
        pattern = Pattern.compile("\\s+[0-9]+,[0-9]{2}\\s+|\\s+[0-9]+\\.[0-9]{2}\\s");
        matcher = pattern.matcher(strTextLower);
        strTmp = "" ;
        while( matcher.find() ) 
        {
           
           //auf Liter darf kein EUR folgen
           if(false == classData.isNear(strTextLower, "eur", matcher.start(), matcher.end(), 5) ){
               strTmp = strTextLower.substring(matcher.start(), matcher.end()) ;
               System.out.println("Menge: " + strTmp ) ;              
               break ;
           }
               
        }
       sb.append(strTmp.trim()) ;
       sb.append(";") ; 
        
        /*
        bFound = matcher.find() ;
        if( true == bFound )
        {
           strTmp = strTextLower.substring(matcher.start(), matcher.end()) ;
           System.out.println("Liter: " + strTmp ) ;              
        }
        */
        //Betrag
        pattern = Pattern.compile("\\s+[0-9]+,[0-9]{2}\\s+|\\s+[0-9]+\\.[0-9]{2}\\s");
        matcher = pattern.matcher(strTextLower);
        strTmp = "" ;
        //bFound = matcher.find() ;
        //if( true == bFound )
        while( matcher.find() ) 
        {
           
           if(true == classData.isNear(strTextLower, "eur", matcher.start(), matcher.end(), 5) ){
               strTmp = strTextLower.substring(matcher.start(), matcher.end()) ;
               System.out.println("Betrag: " + strTmp ) ;                     
               break ;
           }                   
        }
        sb.append(strTmp.trim()) ;
               sb.append(";") ;
        //System.out.println(sb.toString() ) ;      
        return sb.toString() ;
    }

    
}

class classDataFuel extends classData{
    //to keep things easy(in terms of design -> we return just a csv formatted string
     public static String extractValues(String strTextLower){
         //Pattern for a date
        Pattern pattern = Pattern.compile("[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}|[0-9]{4}\\.[0-9]{2}\\.[0-9]{2}");
        boolean bFound = false ;
        String strTmp = null ;
        String strNear = null ;
        StringBuilder sb = new StringBuilder() ;
        
        // In case you would like to ignore case sensitivity you could use this
        // statement
        // Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(strTextLower);
        
        bFound = matcher.find() ;
        strTmp = "" ;
        if( true == bFound )
        {
           strTmp = strTextLower.substring(matcher.start(), matcher.end()) ;
           System.out.println("Datum: " + strTmp ) ;     
           //strTmp = 
        }
        sb.append(strTmp.trim()) ;
        sb.append(";") ;
        //Time 01:34:21
        pattern = Pattern.compile("[0-9]{2}:[0-9]{2}:[0-9]{2}\\s");
        matcher = pattern.matcher(strTextLower);
        
        bFound = matcher.find() ;
        strTmp = "" ;
        if( true == bFound )
        {
           strTmp = strTextLower.substring(matcher.start(), matcher.end()) ;
           System.out.println("Uhrzeit: " + strTmp ) ;          
        }
        sb.append(strTmp.trim()) ;  
        sb.append(";") ;
        pattern = Pattern.compile(".diesel.|e10|super");
        matcher = pattern.matcher(strTextLower);
        
        bFound = matcher.find() ;
        strTmp = "" ;
        if( true == bFound )
        {
           strTmp = strTextLower.substring(matcher.start(), matcher.end()) ;
           System.out.println("Treibstoff: " + strTmp ) ;           
        }
        sb.append(strTmp.trim()) ;
        sb.append(";") ;
        //Menge
        pattern = Pattern.compile("\\s+[0-9]+,[0-9]{2}\\s+|\\s+[0-9]+\\.[0-9]{2}\\s");
        matcher = pattern.matcher(strTextLower);
        strTmp = "" ;
        while( matcher.find() ) 
        {
           
           //auf Liter darf kein EUR folgen
           if(false == classData.isNear(strTextLower, "eur", matcher.start(), matcher.end(), 5) ){
               strTmp = strTextLower.substring(matcher.start(), matcher.end()) ;
               System.out.println("Menge: " + strTmp ) ;              
               break ;
           }
               
        }
       sb.append(strTmp.trim()) ;
       sb.append(";") ; 
        
        /*
        bFound = matcher.find() ;
        if( true == bFound )
        {
           strTmp = strTextLower.substring(matcher.start(), matcher.end()) ;
           System.out.println("Liter: " + strTmp ) ;              
        }
        */
        //Betrag
        pattern = Pattern.compile("\\s+[0-9]+,[0-9]{2}\\s+|\\s+[0-9]+\\.[0-9]{2}\\s");
        matcher = pattern.matcher(strTextLower);
        strTmp = "" ;
        //bFound = matcher.find() ;
        //if( true == bFound )
        while( matcher.find() ) 
        {
           
           if(true == classData.isNear(strTextLower, "eur", matcher.start(), matcher.end(), 5) ){
               strTmp = strTextLower.substring(matcher.start(), matcher.end()) ;
               System.out.println("Betrag: " + strTmp ) ;                     
               break ;
           }                   
        }
        sb.append(strTmp.trim()) ;
        sb.append(";") ;
        
        
        pattern = Pattern.compile("(?<=[0-9]{5}).*");  //76684 Östringen
        matcher = pattern.matcher(strTextLower);
        
        bFound = matcher.find() ;
        strTmp = "" ;
        if( true == bFound )
        {
           strTmp = strTextLower.substring(matcher.start(), matcher.end()) ;
           System.out.println("Ort: " + strTmp ) ;           
        }
        sb.append(strTmp.trim()) ;
        sb.append(";") ;
        
        //System.out.println(sb.toString() ) ;      
        return sb.toString() ;
    }
}

class classifyText{

    public static classResult classify(String strText){
        classResult res = new classResult() ;
        Pattern pattern = Pattern.compile(".*diesel.*|.*tankstelle.*|.*tank.*");
        boolean bFound = false ;
        
        String strLower = strText.toLowerCase() ;
        
        // In case you would like to ignore case sensitivity you could use this
        // statement
        // Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(strLower);
        
        bFound = matcher.find() ;
        if( true == bFound ){
            res.m_eType = _CLASS_.FUEL ;
            System.out.println("'Diesel' gefunden - das könnte ein Tankbeleg sein...") ;            
        }
        else{
           // return null ;
        }
        if( null == res.m_eType ){
            pattern = Pattern.compile("volksbank");
            matcher = pattern.matcher(strLower);
            bFound = matcher.find() ;
            pattern = Pattern.compile("(?<= k o n t o a u s z u g).*SEPA");
            matcher = pattern.matcher(strLower);
            bFound = matcher.find() ;
            if( true == bFound ){
                res.m_eType = _CLASS_.BANKSTATEMENT ;
                System.out.println("Das könnte ein Auszug sein...") ;            
            }
            else{
            // return null ;
            }
        }
        if( null == res.m_eType ) return res ;
        
        switch(res.m_eType){
            case FUEL:
                res.m_strType = "FUEL" ;
                res.m_strExtract = classDataFuel.extractValues(strLower);
                break ;
            case BANKSTATEMENT:
                res.m_strType = "BANK STATEMENT" ;
                res.m_strExtract = classDataBankstatement.extractValues(strLower);
                break ;
            default:
                res.m_strType = "UNKNOWN" ;
                break ;        
        }
        
        
        
        return res ;
    }

}
public class classificationAnalyzer {
    
    
    private static void writeResult(String strFile, String strPath, String strUniverseID, classResult res  ){
        StringBuilder sb = new StringBuilder() ;
        hcc_search.logger.fileLogger log = new hcc_search.logger.fileLogger(strFile, strPath, true) ;
        
        
        sb.append(strUniverseID) ;
        sb.append(";") ;
        sb.append(res.m_strType) ;
        sb.append(";") ;
        sb.append(res.m_strExtract) ;
        log.logLine(sb.toString()) ;
        log.close(); 
    }
    
    
    
    
     //used to process a config line by line and handle it by a Config Processor
  public static void processTodoList(String strPath){
      FileInputStream fis = null ;
      BufferedReader br = null ;
      StringBuilder sb = new StringBuilder() ;
      String strTmp = null ;
      hResult oResult = null ;
      classResult res = null ;
      String  strResult = null ;
      String[] rParts = null ;
      oResult = null ;
      //System.out.println(oResult.m_oPayLoad);
      
      try {
          fis = new FileInputStream(new File(strPath));
          br = new BufferedReader(new InputStreamReader(fis, "UTF-8")) ;
          System.out.println("processCfgFile - opened: " + strPath );
        } catch (Exception ex) {
          // at least on windows, some temporary files raise this exception with an "access denied" message
          // checking if the file can be read doesn't help    
            System.err.println("processCfgFile - error: " + ex.toString());
            return ;
        }

        try{  
             //Linese consist(currently) of UNIVERSE_ID and PATH separated by ';'
             while ((strTmp = br.readLine()) != null) {
                   rParts = strTmp.split(";") ;
                   oResult = new hResult() ;
                   res = new classResult() ;
                  
                   oResult = PDFExtractor.processDocument(rParts[1]) ;
                   //System.out.println(oResult.m_oPayLoad);
                   if( null == oResult.m_oPayLoad ) continue ;
                   res = classifyText.classify(oResult.m_oPayLoad.toString()) ;
                   if( null != res.m_eType )
                    classificationAnalyzer.writeResult("xtract.txt", "c:\\tmp", rParts[0], res) ;
             }
             
             br.close();
             fis.close();
        } catch (java.io.IOException ex){
           
        }finally{
           
           
        }     
     }
    
    
    public static void main(String[] args) throws Exception {
        /*
        hResult oResult = new hResult() ;
        classResult res = new classResult() ;
        String  strResult = new String() ;
        oResult = PDFExtractor.processDocument("c:\\tmp\\0pdftest.pdf") ;
        //System.out.println(oResult.m_oPayLoad);
        res = classifyText.classify(oResult.m_oPayLoad.toString()) ;
        classificationAnalyzer.writeResult("xtract.txt", "c:\\tmp", res.m_strExtract) ;
        
        oResult = PDFExtractor.processDocument("c:\\tmp\\1pdftest.pdf") ;
        res = classifyText.classify(oResult.m_oPayLoad.toString()) ;
        classificationAnalyzer.writeResult("xtract.txt", "c:\\tmp", res.m_strExtract) ;
        * 
        */
        classificationAnalyzer.processTodoList("c:\\tmp\\classtodolist.txt") ;
        
        System.out.println("END");
    }
    
    
    
}


