/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.testFrames;

import hcc_miners.NamedEntityParserTest;
import hcc_pdfWndExtractor2020.pdfWndExtractor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import searchRT.utils.excludedFiles;

//import org.apache.commons.text.StringEscapeUtils;
/**
 *
 * @author frank
 */
public class hccAI2021 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Pattern p = null ; //
        Matcher m = null ;
        //nur ein Experiment mit Tika NER - fruchtlos
        /*
        NamedEntityParserTest ner = new NamedEntityParserTest() ;
        String strVal = "*.*, + (xxx)";
        String strChars =  "<([{\\^-=$!|]})?*+.>" ;
        //rChars = "<([{\\^-=$!|]})?*+.>" ;
        char[] rChars = strChars.toCharArray();
        for(int i = 0; i<strChars.length();i++){
            String strX = String.valueOf(strChars.charAt(i)) ;
            strVal.indexOf(strChars.charAt(i)) ;
            
            strVal = strVal.replace(strX, "\\" + strX);
        }
        System.out.println(strVal);
        //<([{\^-=$!|]})?*+.>
        Pattern  pattern = Pattern.compile(strVal) ;//Crash Test 
        
        try{
            //ner.testParse(); 
            ner.testNerChain();
        }catch(Exception ex){
            System.err.println(ex.toString()) ;
        }
        int iRet = 0 ;
        String strRegex = "(([A-Za-z\\.-])+\\s*)+([0-9])+\\s+" ;
        m = Pattern.compile(strRegex, Pattern.CASE_INSENSITIVE).matcher("Wieslocher Str. 30 " );
            if( m.matches() ){
                iRet++ ;
            }
        */
        
        pdfWndExtractor pWE = null ;
        pWE = new pdfWndExtractor() ;
        //pWE.extractTest("x:\\tmp\\83275707_2020_Nr.004_Kontoauszug_vom_30.04.2020_20200509031949.pdf") ; //nur Test
        //pWE.extractTest("x:\\tmp\\14052020_BRN3C2AF4ADF55B_20200514_064614_001466.pdf") ; //nur Test Kassenbeleg
        //pWE.extractTestAddress("x:\\tmp\\290322020_BRN3C2AF4ADF55B_20200316_105748_001138.pdf"); // Test Address
        pWE.extractTestInvoice("x:\\tmp\\07082011162135817_min.pdf"); // Invoice hcc
    }    
}
