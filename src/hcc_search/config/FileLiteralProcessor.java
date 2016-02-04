/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.config;

import java.io.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.MatchResult ;
import java.util.ArrayList ;
import java.util.List ;

/**
 *
 * @author flinn2112
 * Testframe for Ini Processing.
 */
public class FileLiteralProcessor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try{
            FileLiteralProcessor.processFile("C:\\Users\\flinn2112\\Documents\\templates\\test.txt");
        }catch (java.io.IOException ex){
            
        }
    }
    public static void processFile(String strFilename) throws IOException{ 
      
      // Open the file that is the first 
  // 
       String strTmp = null ;
       String regex = "'.+'*";
       List<MatchResult> results = new ArrayList<MatchResult>();
       int count = 0 ;
  try{
      FileInputStream fstream = new FileInputStream(strFilename);
      // Get the object of DataInputStream
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String strLine;
      Matcher m = null ;
  //Read File Line By Line
      while ((strLine = br.readLine()) != null)   {
      // Print the content on the console
          System.out.println (strLine);
          
           m = Pattern.compile(regex).matcher(strLine); 
           //if( true == m.find() ){
               //strTmp = m.group().toString() ;
               count = m.groupCount();
               
               while (m.find())
                    System.out.println("[" + m.group() + "]");
              /*
              for(int i = 1; i < count;i++){
                System.out.println(m.group(i));                
              }
              */
               
           
            
           
          /*
           for(int i = 1, count = m.groupCount();i < count;i++){
                System.out.println(m.group(i));
           }
           */
          /*
         Matcher matcher = Pattern.compile(regex).matcher(strLine);
         if (matcher.matches()) {
            
            for(int i = 1, count = matcher.groupCount();i < count;i++){
                System.out.println(matcher.group(i));
            }
          
        }
      */
      
      }
  //Close the input stream
     in.close();
     br.close();
    }catch (Exception ex){   //Catch exception if any
       System.err.println("Error: " + ex.getMessage());
    }
  }
}
