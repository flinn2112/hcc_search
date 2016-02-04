/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.logger;

import hcc_search.hcc_utils;
import java.io.*;
import java.security.MessageDigest;

/**
 *
 * @author hcc
 * This class logs to the /logs directory on a file base.
 */
public class fileLogger implements ILogger {
     private File m_fLog ;
     private BufferedWriter m_bwLog = null ;
     private String m_strName ;
     private String m_strFullPath ;
     private String m_strStatus ;
     private boolean m_bIsOpen ;
     private String m_strDelimiter = ";";
     
     public fileLogger( String strName, String strFullpath){
         m_strName = strName ;
         m_strFullPath = strFullpath ;
         m_bIsOpen = this.open(m_strFullPath);
        
         if( true == m_bIsOpen  ) 
             this.close() ;
         
     }
     
     public fileLogger( String strName, String strFullpath, boolean bAppend){
         m_strName = strName ;
         m_strFullPath = strFullpath ;
         m_bIsOpen = this.open(m_strFullPath, bAppend);
        
         if( true == m_bIsOpen  ) 
                        this.close() ;
         
     }
     
     public void setDelimiter(String strDelimiter){
         m_strDelimiter = strDelimiter ;
     }
     
    public boolean open(String m_strPath){ 
          return this.open(m_strPath, true) ;
    }
    
    public boolean open(String m_strPath, boolean bAppend){    
        FileOutputStream fOut = null ;
        try{
            fOut = new FileOutputStream(m_strFullPath, true) ;
            //m_fLog = new File(m_strFullPath);
            m_bwLog = //new BufferedWriter(new FileWriter(m_fLog, bAppend));
                new BufferedWriter(new OutputStreamWriter(
                            fOut, "UTF-8")
            );            
        }
        catch( IOException ex){
            m_strStatus = "open failed" ;
            return false ;
        }
        m_strStatus = "open" ;
        return true ;
    } ;
    
    
    
    public void close(){    
        if( false == m_bIsOpen) return ;
        try{
            m_bwLog.flush() ;
            m_bwLog.close();
        }
        catch( IOException ex){
           
        }
    } ;
    
    public String getStatus(){
        return m_strFullPath + "/" + m_strStatus ;
    }
    
    public void log(String strText){
        this.logLine(strText);
    }
    
    
    public void logLine(String strText){
        boolean bRet = false ;        
        try{
            bRet = this.open(m_strFullPath);
            if(false == bRet) return ;
            m_bwLog.append(strText);
           // m_bwLog.newLine() ;
            this.close();
        } catch( IOException ex){
           ;
        }
    }
    
    public void log(String strWho, String strWhat, int iSeverity){
        boolean bRet = false ;
        StringBuilder sb = new StringBuilder() ;        
        sb.append(hcc_utils.timestamp_now()) ;
        sb.append(m_strDelimiter) ;
        sb.append(strWho) ;
        sb.append(m_strDelimiter) ;
        sb.append(strWhat) ;
        sb.append("\r\n") ;
        this.log(sb.toString());        
    } 
    
    public static String md5(String strContent){   
        MessageDigest m = null ;
        String strRet   = null ;
        try{
            m = MessageDigest.getInstance("MD5");
            m.update(strContent.getBytes(),0,strContent.length());
            strRet = new java.math.BigInteger(1,m.digest()).toString(16) ;
        }catch(java.security.NoSuchAlgorithmException ex){
           strRet = "" ;
        }
        
        return strRet ;
    }
  
}
