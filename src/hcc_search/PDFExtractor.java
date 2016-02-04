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



import java.io.File;
import java.io.*;
import java.io.IOException;

import java.util.*;
import java.text.*;
import org.apache.pdfbox.searchengine.lucene.LucenePDFDocument ;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;


public class PDFExtractor {
    
    
  //This method would loop forever when PDF has an encoding error:
  //pdf +"Stop reading corrupt stream" +pdfbox
  public static hResult processDocumentBogus(java.io.File file)
    throws IOException {
        Document doc = null ;
        hResult oResult = new hResult() ;
       
      try{  
                 System.out.println("processing file [" + file + "]");
                 doc = LucenePDFDocument.getDocument( file );           
                 oResult.m_eRetType = hResult.DOCUMENT ;
                 oResult.m_oPayLoad = doc ;
      }
      catch (java.io.IOException ex){
                oResult.m_eRetType = hResult.NULL ;
                System.out.println("skipping file [" + file + "]");
      }
       return oResult ;
     }
  
  public static hResult processDocument(java.io.File file)
  {
      
      hResult oResult = new hResult() ;
      oResult.m_eRetType = hResult.NULL ;
      
        try
        {
            System.out.println("PDFExtractor processDocument - Opening file [" + file.getName() + "]");
            PDDocument pddDocument = PDDocument.load(file); 
      
            PDFTextStripper textStripper = new PDFTextStripper();
            
            System.out.println("stripping text ");
            oResult.m_oPayLoad = textStripper.getText(pddDocument);
            System.out.println("done ");
            pddDocument.close();
            oResult.m_eRetType = hResult.CONTENT ;
            
        }
        catch(Exception ex)
        {
        ex.printStackTrace();
        }
         return oResult ;
      
    }
  
  public static hResult processDocument(hcc_ExtractorArgsInputStream args)
  {
      
      hResult oResult = new hResult() ;
      oResult.m_eRetType = hResult.NULL ;
       if( null == args.m_is ){           
            return null ;
        }
      
        try
        {
            PDDocument pddDocument = PDDocument.load(args.m_is);            
            PDFTextStripper textStripper = new PDFTextStripper();
            
            System.out.println("stripping text ");
            oResult.m_oPayLoad = textStripper.getText(pddDocument);
            System.out.println("done ");
            pddDocument.close();
            oResult.m_eRetType = hResult.CONTENT ;
            
        }
        catch(Exception ex)
        {
        ex.printStackTrace();
        }
         return oResult ;
      
    }
  
  
  public static hResult processDocument(String strContentPDF)
  {
      
      hResult oResult = new hResult() ;
      oResult.m_eRetType = hResult.NULL ;
      
        try
        {
            PDDocument pddDocument = PDDocument.load(strContentPDF);            
            PDFTextStripper textStripper = new PDFTextStripper();
            
            System.out.println("stripping text ");
            oResult.m_oPayLoad = textStripper.getText(pddDocument);
            System.out.println("done ");
            pddDocument.close();
            oResult.m_eRetType = hResult.CONTENT ;
            
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
         return oResult ;
      
    }
  
  
  
  
  //Test using stripper
  public static String processDocument2(String strFilename)
     {
      String strText = null ;
        try
        {
            PDDocument pddDocument=PDDocument.load(new File(strFilename));
            PDFTextStripper textStripper=new PDFTextStripper();
            strText = textStripper.getText(pddDocument);
            pddDocument.close();
        }
        catch(Exception ex)
        {
        ex.printStackTrace();
        }
         return strText ;
    }
}
  





