/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchRT.utils; 

import java.io.* ;
import java.util.Hashtable ;
/**
 * 
 * @author Frank
 * uses a log file IndexFilesApp.txt to build a hash table.
 * Estimated size for hashtable will be 100K entries+
 */
public class deltaMan { 
    static Hashtable m_htMD5 = null ;
    //static Hashtable m_htMD5New = null ;
    
    public static final int IS_NEW = 0;
    public static final int IS_CONTAINED = 1;
    public static final int IS_DUP = 2;
    public static int check(String strMD5){ 
        
     //erstmal weg - Fehlersuche 11/2017: Vermutung lag nahe, das die Duplikaterkennung den Index durcheinander bringt. - Hat sich aber nicht bestätigt. 
     
        if( null != deltaMan. m_htMD5 && true ==  deltaMan.m_htMD5.containsKey(strMD5) ){
            return  deltaMan.IS_DUP ;
        }
     
        return deltaMan.IS_NEW ;  //not contained
    }
    
    public static void discovered(String strMD5, String strFilename){
        if( null == deltaMan.m_htMD5){
                deltaMan. m_htMD5 = new Hashtable() ;              
        }
            deltaMan. m_htMD5.put(strMD5, strFilename) ;        
    }
    
    /*
        Create a database from a log file that contains all files that were indexed.
        The CheckSums can be found there.
        This is different, to the discovery of new MD5, - to detect duplicates
    
    */
    public static boolean createDB(String strFilename){
       String strTmp = null ;
       String regex = "'.+'*";       
       int count = 0 ;
      DataInputStream in = null ;
      BufferedReader br  = null;
      String strLine;
      String[] rCols = null ;
      FileInputStream fstream = null ;
      
      deltaMan.m_htMD5    = new Hashtable() ;
     // deltaMan.m_htMD5New = new Hashtable() ;
  try{
       fstream = new FileInputStream(strFilename);
      // Get the object of DataInputStream
       in = new DataInputStream(fstream);
       br = new BufferedReader(new InputStreamReader(in));
      
      
  //Read File Line By Line
       //i.e. ct1422.pdf;29.10.2015 19:37:12;FILE;20026034;1. Dezember 2014;dc29d65a7273ce534fa9a8361d8fe8fe;
       //we want the MD5 as key and the filename as value
      while ((strLine = br.readLine()) != null)   {
      // Print the content on the console
          //System.out.println (strLine);
          rCols = strLine.split(";") ; 
          deltaMan.m_htMD5.put(rCols[5], rCols[0]) ;
      }
  //Close the input stream
     in.close();
     br.close();
    }catch (Exception ex){   //Catch exception if any
       System.err.println("Error: " + ex.getMessage());
    }
        return true ;
    }
}
