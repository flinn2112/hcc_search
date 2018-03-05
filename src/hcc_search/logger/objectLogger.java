/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.logger;

import hcc_search.hResult;
import hcc_search.hcc_utils;
import java.io.*;


/**
 *
 * @author hcc
 * This class logs to the /logs directory on a file base.
 * It can be used to log the files and directory list that are indexed.
 */
public class objectLogger implements ILogger {
     private File m_fLog ;
     private BufferedWriter m_bwLog = null ;
     private String m_strName ;
     private String m_strPath ;
     private String m_strStatus ;
     private boolean m_bIsOpen ;
     private String m_strDelimiter = ";";
     public objectLogger( String strName, String strFullpath, boolean bAppend){
         m_strName = strName ;
         m_strPath = strFullpath ;
         m_bIsOpen = this.open(strName);
        
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
            m_fLog = new File(m_strPath + File.separator + strName) ;
            m_bwLog = new BufferedWriter(new FileWriter(m_fLog, bAppend));            
        }
        catch( IOException ex){
            System.err.println("ObjectLogger failed to open:" + m_strPath + File.separator + strName) ;
            m_strStatus = "open failed" ;
            return false ;
        }
        System.out.println("ObjectLogger open" + strName) ;
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
    
    public void log(hResult oResult){
        boolean bRet = false ;
        StringBuilder sb = new StringBuilder() ; 
        sb.append(oResult.m_strPath) ;
        
        sb.append(m_strDelimiter) ;
        sb.append(hcc_utils.timestamp_now()) ;
        sb.append(m_strDelimiter) ;
        
        
        sb.append(oResult.m_strType) ;
        
        sb.append(m_strDelimiter) ;        
        
        sb.append(oResult.m_lFileLength) ;
        sb.append(m_strDelimiter) ;        
         sb.append(oResult.m_iPayLoadLen) ;
        sb.append(m_strDelimiter) ;
        sb.append(oResult.m_strLastModified) ;
        sb.append("\r\n") ; //need to have new Line
        
        try{
            bRet = this.open(m_strName);
            if(false == bRet) return ;
            m_bwLog.write(sb.toString());
            this.close();
        } catch( IOException ex){
           ;
        }
    }
    
    public void log(File f){
        boolean bRet = false ;
        StringBuilder sb = new StringBuilder() ;  
        sb.append(f.getName()) ;
        sb.append(m_strDelimiter) ;
        sb.append(hcc_utils.timestamp_now()) ;
        sb.append(m_strDelimiter) ;
        
        if(f.isDirectory()){
            sb.append("DIR") ;
        }
        if(f.isFile()){
            sb.append("FILE") ;
        }
        
        sb.append(m_strDelimiter) ;        
        
        sb.append(f.length()) ;
        sb.append(m_strDelimiter) ;
        sb.append(hcc_utils.getLastModifiedString(f)) ;
        sb.append("\r\n") ; //need to have new Line
        
        try{
            bRet = this.open(m_strName);
            if(false == bRet) return ;
            m_bwLog.write(sb.toString());
            this.close();
        } catch( IOException ex){
           ;
        }
    } 
    
    public void log(File f, String strMsg){
        boolean bRet = false ;
        StringBuilder sb = new StringBuilder() ;  
        sb.append(f.getAbsolutePath()) ;
        sb.append(m_strDelimiter) ;
        sb.append(hcc_utils.timestamp_now()) ;
        sb.append(m_strDelimiter) ;
        
        if(f.isDirectory()){
            sb.append("DIR") ;
        }
        if(f.isFile()){
            sb.append("FILE") ;
        }
        
        sb.append(m_strDelimiter) ;       
        sb.append(f.length()) ;
        sb.append(m_strDelimiter) ;
        sb.append(hcc_utils.getLastModifiedString(f)) ;
        sb.append(m_strDelimiter) ;                
        sb.append(strMsg) ;
        sb.append(m_strDelimiter) ;
        
        try{
            bRet = this.open(m_strName);
            if(false == bRet) return ;
            m_bwLog.write(sb.toString());
            m_bwLog.newLine() ;
            this.close();
        } catch( IOException ex){
           ;
        }
    } ;
  
}
