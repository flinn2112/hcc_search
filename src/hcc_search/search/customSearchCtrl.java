/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.search;
import hcc_search.hcc_search_opts;
import hcc_search.hcc_utils;
import hcc_search.logger.fileLogger;
import hcc_search.result.sResult;
import hcc_search.result.searchResultOut;
import hcc_search.result.* ;
 
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.Date;




import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;



import org.apache.lucene.document.Document;

//import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.index.IndexReader ;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryparser.classic.QueryParser ;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import searchRT.utils.*;




/** Simple command-line based search demo. */
public class customSearchCtrl {

  public customSearchCtrl() {}

  
  
    public  sResult  search2( hcc_search_opts so, searchResultOut out ) throws Exception {
        String strResult = null ; 
        java.nio.file.Path nPath = Paths.get(so.m_strIndexDir) ;//FileSystems.getDefault().getPath(so.m_strIndexDir);
        FSDirectory fsDir = FSDirectory.open(nPath);
        org.apache.lucene.index.DirectoryReader  ireader = null ;
        fileLogger fLog = new fileLogger("customSearch.java", ".\\customSearchCtrl.log" );         
        fLog.log("cs.search2", "Starting", 0) ;
        fLog.log("cs.search2", "CheckPoint1", 0) ; 
        
        fLog.log("cs.search2", "Path is " + nPath.toString(), 0) ;
        if( null == fsDir){
            fLog.log("cs.search2", "fsDir is null." , 0) ;
        }
        else{
            fLog.log("cs.search2", "fsDir: " + fsDir.toString() , 0) ;
        }
        //org.apache.lucene.store.Directory dir = org.apache.lucene.store.Directory ;
        
        try{
                ireader = 
                        org.apache.lucene.index.DirectoryReader.open(fsDir);
        }
        catch( IOException ex){
            fLog.log("cs.search2", "CheckPoint1.1 " + ex.toString(), 0) ;     
            return null ;
        }
 fLog.log("cs.search2", "CheckPoint2", 0) ;       
        //FSDirectory.open(Paths.get(so.m_strIndexDir))
        org.apache.lucene.search.IndexSearcher searcher = new org.apache.lucene.search.IndexSearcher(ireader);
        Analyzer analyzer = null ;
      fLog.log("cs.search2", "CheckPoint3", 0) ;      
        
        
        org.apache.lucene.queryparser.classic.QueryParser parser = null ; //new org.apache.lucene.queryparser.classic(Version.LUCENE_5_0_0, so.m_strField, analyzer);
    fLog.log("cs.search2", "CheckPoint4", 0) ;      
        Query query = null ;
        sResult oResult = null ;
        boolean raw = false;
        int hitsPerPage = 10;
        
        fLog.log("cs.search2", "Searching [" + so.m_strField + "] for: " + so.m_strQuery, 0);
        
        System.out.println("Searching [" + so.m_strField + "] for: " + so.m_strQuery);

        if( true == so.m_bSearchWhiteSpace )
            analyzer = new org.apache.lucene.analysis.core.WhitespaceAnalyzer();
        else
            analyzer = new StandardAnalyzer();

        parser = new org.apache.lucene.queryparser.classic.QueryParser(so.m_strField, analyzer);     

        try{
            query = parser.parse(so.m_strQuery);
        }catch(Exception ex){             
             return new sResult("parser.parse" + ex.toString(), 0L, false) ;
        }

        Date start = new Date();
        try{
            searcher.search(query, null, 100);
            Date end = new Date();
            System.out.println("Time: "+(end.getTime()-start.getTime())+"ms");
            oResult = doPagingSearch2(searcher, query, so, hitsPerPage, out);
        }
        catch( Exception ex){
            //nothing - empty string            
            return new sResult("searcher.search" + ex.toString(), 0L, false) ; 
        }
        //??? 5.0.0 searcher.close();

        return oResult ;
  }

  /**
   * This demonstrates a typical paging search scenario, where the search engine presents 
   * pages of size n to the user. The user can then go to the next page if interested in
   * the next hits.
   * 
   * When the query is executed for the first time, then only enough results are collected
   * to fill 5 result pages. If the user wants to page beyond this limit, then the query
   * is executed another time and all hits are collected.
   * 
   */

  
  
  //New with object return
  public static sResult  doPagingSearch2( IndexSearcher searcher, Query query, hcc_search_opts so,
                                     int hitsPerPage,   searchResultOut rsOut) throws IOException { 
    // Collect enough docs to show 5 pages
    TopDocs results = searcher.search(query, 5 * hitsPerPage);
    ScoreDoc[] hits = results.scoreDocs;
    StringBuilder sb = new StringBuilder() ; 
    int iNumTotalHits = results.totalHits;
    String strTmp = null ;
    String strURL = null ;
    String strTitle = null ;
    
    String strShortText = null ;
    hcc_search.hcc_search_ExtendedAttributes ea = new hcc_search.hcc_search_ExtendedAttributes() ;
    sResult oResult =  new sResult(sb.toString(), new Long(iNumTotalHits), true) ;//so.m_bLogResult
    System.out.println(iNumTotalHits + " total matching documents");

    int start = 0;
    int end = Math.min(iNumTotalHits, hitsPerPage);
        
     if( 0 == iNumTotalHits ){
       return new sResult("", new Long(iNumTotalHits), so.m_bLogResult) ;
     } 

      
      hits = searcher.search(query, iNumTotalHits).scoreDocs;
      
      end = Math.min(hits.length, start + hitsPerPage);
      
      for (int i = start; i < iNumTotalHits; i++) {
      /*
          if (raw) {                              // output raw format
          System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
          sb.append("doc="+hits[i].doc+" score="+hits[i].score) ;
          continue;
        }
      */
        Document doc = searcher.doc(hits[i].doc);
        String path = doc.get("URN");
        if (path == null) {
            sb.append(rsOut.out( "invalid document", "No path to this document", "00000000")) ;
            System.out.println((i+1) + ". " + "No path for this document");
            continue ;
        }
        
         
            
          //System.out.println((i+1) + ". " + path);
          String lastMod = doc.get("date");
          if (lastMod != null) {
            //System.out.println("  last modified : " + doc.get("LASTMODIFIED"));            
          }
          strTitle = doc.get("title") ; //can be null
          if( null == strTitle ){  //no title
              strTitle = hcc_utils.getFilename(path) ;
          }
          
          ea.m_strFilesize = doc.get("filesize") ; //can be null
          if( null == ea.m_strFilesize ){  //no title
              ea.m_strFilesize = "unknown size" ;
          }else{
              ea.m_strFilesize = searchRT.utils.searchFieldFormatter.fileSize(Long.parseLong(ea.m_strFilesize, 10)) ;
          }
          
          //is it a URL?
          strURL = doc.get("url") ; //can be null
          
      
          if( null != strURL ){  //strURL bedeutet, dass es sich um eine WebSite Search handelt, bei files ist das nicht gesetzt.
              //strShortText = doc.get("shorttext");
              //nur experimentell: wenn WEB, dann Kurztext aus Content bilden.
              
    
              //query.extractTerms(lTerms) ;
              
              strTmp = doc.get("contents") ;
              try{
                strShortText = hcc_search.result.documentShorttext.shortText(strTmp, so.m_strQuery, 128, 
                      so.m_bHighlightMarkup, so.m_strHighlightClass, 384  //maxLen
                      );
              }
              catch(Exception ex){
                  strShortText = "No shorttext available. [" + ex.toString() + "]" ;
              }
              //sb.append("URL IS SET calling outURL");
              strURL = strURL.replaceFirst("url:/i", "") ;
              
              sb.append(rsOut.outURL(strTitle, strShortText, strURL)) ;       
              //sb.append(rsOut.outURL(strTitle, "das ist der URL Branch", "???")) ;
          }else{ //file
              //sb.append("URL NOT SET");
              //Server Path substitution active?
              //then replace <DRIVELETTER>: with subst
              if( null != so.m_strSubstDriveLetter ){
                  String pattern = "([A-Za-z]:)";
                  path = path.replaceAll(pattern, so.m_strSubstDriveLetter) ;
                  //strTitle = "Found SUBST " +  so.m_strSubstDriveLetter + " path: " + path;
              }else{ //1.8.2.16 - bugs bounty
                  //strTitle = "NO SUBST" ;
              }
              
              sb.append(rsOut.out( strTitle, path, lastMod, ea)) ;
              //sb.append(rsOut.outURL(strTitle, "das ist der FILE Branch", "!!!")) ;
          }
        
      } //for start end
        System.out.println(sb.toString()) ;
        oResult.setResult(sb.toString()) ;
        return oResult ;
      } //doPagingSearch2

    }




