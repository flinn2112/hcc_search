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



public class XLSExtractor {
  public static hResult processDocument(java.io.File file)
    throws IOException {
      FileInputStream fis;
      BufferedReader br = null ;
      StringBuilder sb = new StringBuilder() ;
      String strTmp = new String() ;
      hResult oResult = new hResult() ;
      ExcelFileWrapper xls = null ;
      
      oResult.m_eRetType = hResult.EMPTY ;
      
      try {
          fis = new FileInputStream(file);
          
        } catch (FileNotFoundException ex) {
          // at least on windows, some temporary files raise this exception with an "access denied" message
          // checking if the file can be read doesn't help         
          oResult.m_eRetType = hResult.NULL ;
          return oResult;
        }

        
        try{
            xls = new ExcelFileWrapper(fis) ;
            oResult.m_eRetType = hResult.CONTENT ;
            oResult.m_oPayLoad = xls.readContents() ;
        }
       catch( IOException ex2){
    
        }
        
        
       fis.close();
       return oResult ;
     }
  
  public static hResult processDocument(hcc_ExtractorArgsInputStream args)
    throws IOException {
      
      StringBuilder sb = new StringBuilder() ;
      String strTmp = new String() ;
      hResult oResult = new hResult() ;
      ExcelFileWrapper xls = null ;
      if( null == args.m_is ){           
            return null ;
        }
      oResult.m_eRetType = hResult.EMPTY ;
      
       
        try{
            xls = new ExcelFileWrapper(args.m_is) ;
            oResult.m_eRetType = hResult.CONTENT ;
            oResult.m_oPayLoad = xls.readContents() ;
        }
       catch( IOException ex2){
    
        }
        
        
       
       return oResult ;
     }
}
