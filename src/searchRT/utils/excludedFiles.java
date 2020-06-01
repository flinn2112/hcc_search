/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchRT.utils;
import hcc_search.config.IConfigProcessor; 
import hcc_search.hcc_utils;
import java.text.*;
import java.io.File;
import java.io.*;
import java.util.* ;
import java.net.*;
import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import javax.management.* ;

import java.nio.* ;
import java.nio.channels.FileChannel ;
import java.nio.channels.FileChannel.MapMode; 
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
/**
 *
 * @author Frank
 */
public class excludedFiles {    
    public static Hashtable sHT_Excluded = null ;  //regex repo
    public static boolean isContained( String  strFilename){
      Pattern pattern = null ;
      String strVal = null ;
      Boolean bFound = false ;
      String strChars =  "<([{\\^-=$!|]})?*+.>" ; //1.20.5.20
      char[] rChars = strChars.toCharArray();  //1.20.5.20
      if(null == excludedFiles.sHT_Excluded ) return false ;
      if( excludedFiles.sHT_Excluded.isEmpty()  ) return false ;
      if( null == strFilename ) return false ;
     
      //return null != excludedFiles.sHT_Excluded.get(strExt.toLowerCase()) ;
      if( null != excludedFiles.sHT_Excluded.get(strFilename) ){
          return true ; //found directly
      }
      //iterate table
      
      
      Set<String> keys = excludedFiles.sHT_Excluded.keySet();
 
    //Obtaining iterator over set entries
    Iterator<String> itr = keys.iterator();
 
    //Displaying Key and value pairs
    while (itr.hasNext()) { 
        strVal = itr.next();
         //1.20.5.20 - Escape Metas
        for(int i = 0; i<strChars.length();i++){
            String strX = String.valueOf(strChars.charAt(i)) ;
            strVal.indexOf(strChars.charAt(i)) ;            
            strVal = strVal.replace(strX, "\\" + strX);
        }
        pattern = Pattern.compile(strVal) ;
        Matcher matcher = pattern.matcher(strFilename) ;
         bFound = matcher.find() ;
         if( true == bFound ) break ;
    }
      
      return bFound ;
  }
  
  public static void ht_rebuild( String strFilename)  throws IOException{
      excludedFiles.sHT_Excluded = null ;
      excludedFiles.ht_build(strFilename);
  }
  
  public static void ht_build(String strFilename) throws IOException{      
      // Open the file that is the first 
      if( null == excludedFiles.sHT_Excluded ){
          excludedFiles.sHT_Excluded = new Hashtable() ;
      }
      else{  //does exist -> clear
          excludedFiles.sHT_Excluded.clear();
      }
  try{
      FileInputStream fstream = new FileInputStream(strFilename);
      // Get the object of DataInputStream
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String strLine;
  //Read File Line By Line
      while ((strLine = br.readLine()) != null)   {
      // Print the content on the console
      System.out.println (strLine);
      excludedFiles.sHT_Excluded.put(strLine.toLowerCase(), strLine.toLowerCase());
      }
  //Close the input stream
     in.close();
     br.close();
    }catch (Exception ex){   //Catch exception if any
       System.err.println("Error: " + ex.getMessage());
    }
  }
}
