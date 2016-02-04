/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchRT.utils;
import hcc_search.logger.fileLogger;
import java.util.* ;
import java.io.File;
import java.io.*;
import java.net.*;
import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 *
 * @author frankkempf
 */
public class mime {
    
    public static Hashtable ht_MapMime = null ;
    public static Hashtable ht_MapExt = null ; //File Extension
    
    
    //util: get Mimetype from a file extension - very simple
    public static String get(hcc_search.ht_container htc, String strMapFilename , String strExt, StringBuilder sbResult){
        Object oType = null ;
        //fileLogger fLog = new fileLogger("customSearch.java", 
        //         "d:\\SharedDownloads\\Projekte\\hcc\\hcc_search\\hcc_search.index.jsp.txt" );         
       
       //fLog.log("index.jsp.log - mime.", "path: " + strMapFilename, 0) ;
       //fLog.log("index.jsp.log - mime.", "ext: "  + strExt, 0) ;
        if( null == htc.m_ht ){
            sbResult.append("Creating HashTable<br>") ;
            htc.m_ht = new Hashtable() ;          
            mime.ht_build(htc, strMapFilename,  //FileExt will be the key
                        sbResult) ;            
        }        
        oType = htc.m_ht.get(strExt.toLowerCase()) ;  
        return  oType != null?oType.toString():"application/unknown";
    }
    
    /*
     *  HT can be built in two ways:
     * 1. Mime type as key (for web crawler)
     * 2. Fileext as key (for indexer)
     * This can be controlled by bExtAsKey
     */
    public static boolean ht_build(hcc_search.ht_container htc, String strFilename, StringBuilder sbResult) { 
      
      // Open the file that is the first 
        String[] rData = null ;
        Integer iCount = 0 ;
        boolean bRet = true ;
        //fileLogger fLog = new fileLogger("customSearch.java", 
          //       "d:\\SharedDownloads\\Projekte\\hcc\\hcc_search\\hcc_search.index.jsp.txt" );         
        
        if( null == strFilename ){
            return false ;
        }
        
          try{
              sbResult.append("Opening file: ") ;
              sbResult.append(strFilename) ;
              sbResult.append("<br>") ;
              FileInputStream fstream = new FileInputStream(strFilename);
              // Get the object of DataInputStream
              DataInputStream in = new DataInputStream(fstream);
              BufferedReader br = new BufferedReader(new InputStreamReader(in));
              String strLine;
          //Read File Line By Line
              while ((strLine = br.readLine()) != null)   {
              // Print the content on the console
              //System.out.println (strLine);
                  sbResult = new StringBuilder() ;
                  sbResult.append(strLine) ;
                  sbResult.append("\n") ;
                  rData = strLine.split(";") ;  //split at semicolon
                  
                  sbResult.append(rData[0]) ;
                  sbResult.append("<br>") ;
                  iCount++;
                  //fLog.log("index.jsp.log - mime.", "path: " + sbResult.toString(), 0) ;
                  htc.m_ht.put(rData[0].toLowerCase(), rData[1].toLowerCase());
                  
              }
              sbResult.append(iCount.toString()) ;
          //Close the input stream
             in.close();
             br.close();
            }catch (Exception ex){   //Catch exception if any
               System.err.println("Error: " + ex.getMessage());
               bRet = false ;
            }
            return bRet ;
          }
    
    
}


