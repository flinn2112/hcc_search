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





import java.io.IOException;
import java.util.*;
import java.text.*;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.* ;
import org.jsoup.Jsoup ;



public class HTMLExtractor {
    
      
  public static hResult processDocument(StringBuilder sbHTMLContent)
  {
     return HTMLExtractor.processDocument(sbHTMLContent.toString())  ;
  }
  
  public static hResult processDocument(java.io.File file)
  {
      BINExtractor bExt = new BINExtractor() ;
          hResult oResult = null ;
          try{
              oResult = bExt.processDocument(file) ;
          }
          catch(IOException ex){
            //TODO - output message
          }
          
         return oResult ;
      
    }
  
  public static hResult processDocument(String strHTMLContent)
  {        
      hResult oResult = new hResult() ;            
      oResult.m_oPayLoad = strHTMLContent ;
      oResult.m_strText = strHTMLContent ; //need original to pass back with TAGS
      oResult.m_eRetType = hResult.CONTENT ;
      return oResult ;
   }
  
  
  
  public static hResult processDocument(hcc_ExtractorArgsInputStream args)
  {  
        byte b[] = new byte[8192];
        StringBuilder sb = new StringBuilder () ;
        int numRead = 0 ;
        String content = null ;
        String newContent = null ;
        hResult oResult = null ;
        
        if( null == args.m_is ){   
            System.err.println("HTMLExtractor: Inputstream is null");
            return null ;
        }
        
        try{
            numRead = args.m_is.read(b);
            //content = new String(b, 0, numRead);
            content = new String(b, args.m_strEncoding);
            sb.append(content) ;
            while (numRead != -1) {
                //NOT CLEAR IF we really need the thread checking...
                //if (Thread.currentThread() != searchThread)
                  //  break;
                b = new byte[8192] ;
                
                numRead = args.m_is.read(b);
                    if (numRead != -1) {
                        newContent = new String(b, args.m_strEncoding);
                        //newContent = new String(b, 0, numRead);
                        sb.append(newContent) ;
                        content += newContent;
                    }
                }   
        }catch(IOException ex){
            System.err.println("HTMLExtractor: " + ex.toString());
            return null ;
        }
       
      //System.out.println("HTMLExtractor processing: " + sb.toString());
        
        if( args.m_ppEx != null  ){ //is there a postprocessor?
            oResult = HTMLExtractor.processDocument(sb.toString()) ; 
            if( null != args.m_ppEx)
            oResult.m_strText =  args.m_ppEx.postProcess(sb.toString())  ;
            return oResult ;
        }
        else{
            return HTMLExtractor.processDocument(sb.toString()) ; 
            
        }
        
        
        
   }
  
}



