/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.logger;

import hcc_search.indexer.IndexFilesApp;
import hcc_search.TXTExtractor;
import hcc_search.hcc_utils;
import java.io.*;
import java.util.Hashtable;


/**
 *
 * @author hcc
 * This class logs to the /logs directory on a file base.
 * It can be used to log the files and directory list that are indexed.
 */
public class simpleLogger implements ILogger {
     private File m_fLog ;
     private BufferedWriter m_bwLog = null ;
     private String m_strName ;
     private String m_strPath ;
     private String m_strStatus ;
     private boolean m_bIsOpen ;
     private boolean m_bAppend ;
     private String m_strDelimiter = ";";
     public simpleLogger( String strName, String strFullpath, boolean bAppend){
         m_strName = strName ;
         m_strPath = strFullpath ;
         m_bIsOpen = this.open(strName, bAppend);
         
         if( true == m_bIsOpen  ) this.close() ;
         
     }
     
     public void setDelimiter(String strDelimiter){
         m_strDelimiter = strDelimiter ;
     }
      public boolean open(String strName){ 
          return this.open(strName, true) ;
      }
    public boolean open(String strName, boolean bAppend){        
        try{
            m_fLog = new File(m_strPath + File.separator + strName);
            m_bwLog = new BufferedWriter(new FileWriter(m_fLog, bAppend));
            
        }
        catch( IOException ex){
            System.err.println("ObjectLogger failed to open:" + m_strPath + File.separator + strName) ;
            m_strStatus = "open failed" ;
            return false ;
        }
       // System.out.println("ObjectLogger open" + strName) ;
        m_strStatus = "open" ;
        return true ;
    } ;
    
    public void close(){    
        if( false == m_bIsOpen) return ;
        try{
            m_bwLog.close();
        }
        catch( IOException ex){
           
        }
    } ;
    
    public String getStatus(){
        return m_strPath + "/" + m_strStatus ;
    }
    public void log(String strWho, String strWhat, int iSeverity){
        
    }
    public void log(File f){
        boolean bRet = false ;
        StringBuilder sb = new StringBuilder() ;  
        sb.append(f.getName()) ;        
        //sb.append("\r\n") ; //need to have new Line        
        try{
            bRet = this.open(m_strName, true);
            if(false == bRet) return ;
            m_bwLog.write(sb.toString());
            m_bwLog.newLine() ;
            this.close();
        } catch( IOException ex){
           ;
        }
    } 
    
    
     public void log(String strName){
        boolean bRet = false ;
        
        //sb.append("\r\n") ; //need to have new Line        
        try{
            bRet = this.open(m_strName, true);
            if(false == bRet) return ;
            m_bwLog.write(strName);
            m_bwLog.newLine() ;
            this.close();
        } catch( IOException ex){
           ;
        }
    }
    
     /*
   für Delta Fileprocessing - > load filelist into hashtable
   */
  public Hashtable buildFileList(){      
      TXTExtractor te = new TXTExtractor() ;
      Hashtable ht = new Hashtable() ;
      try{
        ht  = new Hashtable() ;
        te.processDocument(m_fLog,  ht) ;
        
      }catch( IOException ex){
          System.err.println("[buildFileList] Error processing " + m_fLog.getName() );
      }
      return ht ;
  }
    
}
