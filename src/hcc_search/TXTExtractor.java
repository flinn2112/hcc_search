/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search;

/**
 *
 * @author frankkempf
 */
//package org.apache.lucene.demo;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.lucene.document.Document;
import java.util.Hashtable ;


import java.io.File;

import java.io.IOException;
import java.util.*;
import java.text.*;
import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;



public class TXTExtractor {
  public static hResult processDocument(java.io.File file)
    throws IOException {
      FileInputStream fis;
      BufferedReader br = null ;
      StringBuilder sb = new StringBuilder() ;
      String strTmp = new String() ;
      hResult oResult = new hResult() ;
      
      
      
      try {
          fis = new FileInputStream(file);
          br = new BufferedReader(new InputStreamReader(fis, "UTF-8")) ;
        } catch (FileNotFoundException ex) {
          // at least on windows, some temporary files raise this exception with an "access denied" message
          // checking if the file can be read doesn't help         
          oResult.m_eRetType = hResult.NULL ;
          return oResult;
        }

        
        try{  
             System.out.println("processing file [" + file + "]");
             while ((strTmp = br.readLine()) != null) {
                   sb.append(strTmp);
                   sb.append(" "); //1.6.9 separate entries, else get some invalid strings.
             }
             oResult.m_oPayLoad = sb.toString() ;
             oResult.m_eRetType = hResult.CONTENT ;
        } catch (java.io.IOException ex){
                oResult.m_eRetType = hResult.NULL ;
                System.out.println("skipping file [" + ex.getLocalizedMessage().toString() + "]");
        }
       br.close();
       fis.close();
       return oResult ;
     }
  
  public static hResult processDocument(hcc_ExtractorArgsInputStream args)
    throws IOException {      
      hResult oResult = new hResult() ;
      
      byte b[] = new byte[1000];
        StringBuilder sb = new StringBuilder () ;
        int numRead = 0 ;
        String content = null ;
        String newContent = null ;
        
        if( null == args.m_is ){ //no input stream          
            return null ;
        }
        
        try{
            numRead = args.m_is.read(b);
            content = new String(b, 0, numRead);
            sb.append(content) ;
            while (numRead != -1) {
                //NOT CLEAR IF we really need the thread checking...
                //if (Thread.currentThread() != searchThread)
                  //  break;
                numRead = args.m_is.read(b);
                    if (numRead != -1) {
                        newContent = new String(b, 0, numRead);
                        sb.append(newContent) ;
                        content += newContent;
                    }
                }   
        }catch(IOException ex){
            return null ;
        }
        
       oResult.m_oPayLoad = sb.toString() ;
       oResult.m_eRetType = hResult.CONTENT ;
       return oResult ;
     }
  //put contents of a file into a hash table
  //file has the full path
  public static hResult processDocument(java.io.File file, Hashtable ht)
    throws IOException {
      FileInputStream fis;
      BufferedReader br = null ;
      StringBuilder sb = new StringBuilder() ;
      String strTmp = new String() ;
      hResult oResult = new hResult() ;
      
      
      
      try {
          fis = new FileInputStream(file);
          br = new BufferedReader(new InputStreamReader(fis, "UTF-8")) ;
        } catch (FileNotFoundException ex) {
          // at least on windows, some temporary files raise this exception with an "access denied" message
          // checking if the file can be read doesn't help         
          oResult.m_eRetType = hResult.NULL ;
          return oResult;
        }

        
        try{  
             System.out.println("processing file [" + file + "]");
             while ((strTmp = br.readLine()) != null) {
                   ht.put(strTmp, "0") ; //do not need a value use "0" for every file
             }
             oResult.m_oPayLoad = sb.toString() ;
             oResult.m_eRetType = hResult.CONTENT ;
        } catch (java.io.IOException ex){
                oResult.m_eRetType = hResult.NULL ;
                System.out.println("skipping file [" + ex.getLocalizedMessage().toString() + "]");
        }
       br.close();
       fis.close();
       return oResult ;
     }
}
