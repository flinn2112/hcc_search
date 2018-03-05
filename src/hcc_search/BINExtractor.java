/*
   ____  ____ _____ ____  __    ____________________
  / __ \/ __ ) ___// __ \/ /   / ____/_  __/ ____/ /
 / / / / __  \__ \/ / / / /   / __/   / / / __/ / / 
/ /_/ / /_/ /__/ / /_/ / /___/ /___  / / / /___/_/  
\____/_____/____/\____/_____/_____/ /_/ /_____(_)   
                                                    

    ____  __________  __    ___   ________________     ______  __
   / __ \/ ____/ __ \/ /   /   | / ____/ ____/ __ \   / __ ) \/ /
  / /_/ / __/ / /_/ / /   / /| |/ /   / __/ / / / /  / __  |\  / 
 / _, _/ /___/ ____/ /___/ ___ / /___/ /___/ /_/ /  / /_/ / / /  
/_/ |_/_____/_/   /_____/_/  |_\____/_____/_____/  /_____/ /_/   
                                                                 
  ____________ __ ___ 
 /_  __/  _/ //_//   |
  / /  / // ,<  / /| |
 / / _/ // /| |/ ___ |
/_/ /___/_/ |_/_/  |_|
                      
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
import java.nio.* ;
import java.nio.channels.FileChannel ;
import java.nio.channels.FileChannel.MapMode; 


public class BINExtractor {
  public static hResult processDocument(java.io.File file)
    throws IOException {
      FileInputStream fis;
      BufferedReader br = null ;
      char[] cBuf  ;
      hResult oResult = new hResult() ;
      FileInputStream fin = null;
    FileChannel ch = null;
    try {
        System.out.println("BINExtractor processDocument - Opening file [" + file.getName() + "]");
        fin = new FileInputStream(file);
        ch = fin.getChannel();
        int size = (int) ch.size();
        MappedByteBuffer buf = ch.map(MapMode.READ_ONLY, 0, size);
        byte[] bytes = new byte[size];
        buf.get(bytes);
        oResult.m_oPayLoad = new String(bytes); 
        oResult.m_eRetType = hResult.UNKNOWN ;
    } 
    
    catch (java.nio.channels.ClosedByInterruptException exC) {
        // TODO Auto-generated catch block
        //e.printStackTrace();
        System.err.println("binExtractor - ClosedByInterruptException " + exC.getMessage()) ;
    }
    catch (IOException e) {
        // TODO Auto-generated catch block
        //e.printStackTrace();
        System.err.println("binExtractor - IOException " + e.getMessage()) ;
    }
      finally {
        try {
            if (fin != null) {
                fin.close();
            }
            if (ch != null) {
                ch.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
      
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
        
        if( null == args.m_is ){           
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
  
  
  
  
  
}
