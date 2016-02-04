/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.result;

/**
 *
 * @author frankkempf
 */


public class sResult{  
  public String m_strSearchResult   ;
  public Long   m_lNumDocs          ;  
  public StringBuilder m_sbLog ;
  public sResult(String strResult, Long lCount, boolean bDoLog){        
        m_strSearchResult   = strResult ;
        m_lNumDocs          = lCount ;
        if(bDoLog)
        m_sbLog = new StringBuilder() ;
    }
  public void setResult(String strResult){
      m_strSearchResult   = strResult ;
  }
  public void log(String strWho, String strWhat){
      if( null == m_sbLog )
          return ;
      m_sbLog.append( strWho);
      m_sbLog.append( " - ");
      m_sbLog.append( strWhat);
      m_sbLog.append( "\n");
  }
  public String toString(){
        m_sbLog.append( String.valueOf(m_lNumDocs) );
        return m_sbLog.toString() ;
    }
};
