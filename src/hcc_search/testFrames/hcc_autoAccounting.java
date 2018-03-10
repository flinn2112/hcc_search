/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.testFrames;
import hcc_search.PDFExtractor ;
import hcc_search.hcc_search_opts;
import hcc_search.hcc_utils;
import hcc_search.result.searchResultPaths;
import hcc_search.search.customSearchCtrl;
import java.io.File ;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;
import java.nio.file.attribute.*;
import static java.nio.file.FileVisitResult.*;
import java.io.IOException;
import java.util.*;
/**
 *
 * @author Frank
 */
public class hcc_autoAccounting {
     public static void main(String[] args) throws Exception {
        hcc_search.hResult oResult = null ;
        hcc_search.result.sResult sResult  = null ;
        boolean bFound = false ;
        customSearchCtrl cs = null  ;
        hcc_search_opts so = null ;
        String strResult = null ;
        String strAuszug = "A6_7v7" ;
        String strBaseDir = "C:\\Users\\Frank\\Documents\\Buchhaltung\\1_2015\\Auszug6" ;        
        String strFilename = "C:\\Users\\Frank\\Documents\\Buchhaltung\\1_2015\\Auszug6\\"+ strAuszug + ".pdf" ;
      
        //Pattern pattern = Pattern.compile("PN:[0-9]{1,4}\\s*[0-9]{1,},[0-9]{2}|PN:[0-9]{1,4}\\s*[0-9]{1,}\\.[0-9]{0,},[0-9]{2}");
        Pattern pattern = Pattern.compile("[0-9]{1,},[0-9]{2}|[0-9]{1,}\\.[0-9]{0,},[0-9]{2}");
        java.io.File fIn = new  java.io.File(strFilename) ;
        
        //remove ext
        //strResult = "\\.[a-z]{3}"; 
        oResult = PDFExtractor.processDocument(fIn) ;
        if( null == oResult ){
            System.out.println("NO RESULT...");
            return ;
        }
        
        //hcc_autoAccounting.copyOrg(strBaseDir, fIn);
        
        cs = new customSearchCtrl() ;
        oResult.m_strText = oResult.m_oPayLoad.toString() ;
        System.out.println(oResult.m_strText);
        Matcher matcher = pattern.matcher(oResult.m_strText);        
        //bFound = matcher.find() ;
        while(matcher.find()){
            System.out.println(matcher.start() + "/" +  matcher.end());
             strResult = oResult.m_strText.substring(matcher.start(), matcher.end()) ;
             strResult = strResult.replaceAll("PN:[0-9]*\\s*", "") ;
             //System.out.println(strResult) ;
            /* 
             so = new hcc_search_opts("+doctype:pdf +year:2015 +" + strResult, 
               "content", "E:\\SharedDownloads\\Projekte\\hcc\\hcc_search\\index", true,  
               hcc_search_opts._HTML_, "TEST") ;
            */
            so = new hcc_search_opts("+doctype:pdf +year:2015 +" + strResult, "contents", 
                    "e:\\SharedDownloads\\Projekte\\hcc\\hcc_search\\index",
               false, hcc_search_opts._PATHS_, null, true, "highlightText" ) ;
            so.clientCtxt = strAuszug + "_" + strResult ;
            sResult = cs.search2(  so, new searchResultPaths()) ;
            System.out.println("\nResult for: " + strResult + " \n" + sResult.m_strSearchResult );
            hcc_autoAccounting.processResult(strBaseDir, fIn, so, sResult);
        }
     }
     
     //Filter and store files to a dedicated location.
     static void processResult(String strBaseDir, File fOrg, hcc_search_opts so, hcc_search.result.sResult sResult){
         //1. store BaseFile to BaseDir
         //2. itereate resultlist from sbResult.m_strSearchResult by line
         //3. Transfer each file to BaseDir but not if identical with basefile. 
         String strTarget    = null ;
         String strFilename  = null ;
         String rFilenames[] = null ;
         File   fOut = null ;
         int i = 0 ;
         //strBaseDir + File.separatorChar + fOrg.getName() ;
         //store base file
         //hcc_autoAccounting.copyFile(Paths.get(fOrg.getPath()), Paths.get(strTarget), false, false);
         rFilenames = sResult.m_strSearchResult.split("\n") ;
         for(i=0;i<rFilenames.length; i++){
             fOut = new File(rFilenames[i]) ;
             strFilename = fOut.getName() ;
             //prefix the filename with the searched string
             if( null != so.clientCtxt ){                 
                 strFilename = so.clientCtxt.toString() + "_" + strFilename ;
                 strFilename = strFilename.replace(",", "_") ;
             }
             strTarget = strBaseDir + File.separatorChar  + strFilename ;
             hcc_autoAccounting.copyFile(Paths.get(rFilenames[i]), Paths.get(strTarget), false, false);
         }
         return ;
     }
     
     
     static void copyOrg(String strBaseDir, File fOrg){
         //1. store BaseFile to BaseDir
         //2. itereate resultlist from sbResult.m_strSearchResult by line
         //3. Transfer each file to BaseDir but not if identical with basefile. 
         String strTarget = strBaseDir + File.separatorChar + fOrg.getName() ;
        
        //store base file
         hcc_autoAccounting.copyFile(Paths.get(fOrg.getPath()), Paths.get(strTarget), false, false);
         
     }
     
     
     /**
     * Copy source file to target location. If {@code prompt} is true then
     * prompt user to overwrite target if it exists. The {@code preserve}
     * parameter determines if file attributes should be copied/preserved.
     */
    static void copyFile(Path source, Path target, boolean prompt, boolean preserve) {
        CopyOption[] options = (preserve) ?
            new CopyOption[] { COPY_ATTRIBUTES, REPLACE_EXISTING } :
            new CopyOption[] { REPLACE_EXISTING };
        
            try {
                Files.copy(source, target, options);
            } catch (IOException x) {
                System.err.format("Unable to copy: %s: %s%n", source, x);
            }
    }
     
     
     
     
}
