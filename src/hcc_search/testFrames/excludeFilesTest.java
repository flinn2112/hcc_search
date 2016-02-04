/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.testFrames;
import searchRT.utils.* ;
/**
 *
 * @author Frank
 */
public class excludeFilesTest {
    public static void main(String[] args) {
        Boolean bTest = false ; 
        String strFile = null ;
        String strText = null ;
        try{
            excludedFiles.ht_build("D:\\SharedDownloads\\Projekte\\hcc\\hcc_search\\dist\\config\\excludeFiles.txt") ;
        }
        catch(Exception ex){
          //non lethal
        }
        System.out.println( Integer.toString(excludedFiles.sHT_Excluded.size()) );
        strFile = "D:\\SharedDownloads\\Projekte\\hcc\\hcc_search\\dist\\logs\\FileList.txt" ;
        bTest = excludedFiles.isContained(strFile) ;
        strText = "File [" + strFile  + "] was  INCLUDED ";
        if( true ==  bTest ) strText = "File [" + strFile  + "] was  EXCLUDED ";
        System.out.println(strText);
        strFile = "D:\\SharedDownloads\\Projekte\\hcc\\hcc_search\\dist\\test\\FileList.txt" ;
        bTest = excludedFiles.isContained(strFile) ;
        strText = "File [" + strFile  + "] was  INCLUDED ";
        if( true ==  bTest ) strText = "File [" + strFile  + "] was  EXCLUDED ";
        System.out.println(strText);
        return ;
    }
}
