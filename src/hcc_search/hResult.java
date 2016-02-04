/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search;

/**
 *
 * @author frankkempf
 */


public class hResult{  
  public Object  m_oPayLoad   ;
  public String  m_strDocType ;
  public String  m_strText ;
  public int     m_eRetType  ;
  public Integer m_iPayLoadLen ;
  public hResult(){        
        m_oPayLoad   = null ;
        m_strText    = null ;  //might be used by WebCrawlser to store original data
        m_strDocType = null ;
        m_eRetType   = hResult.NULL ;
    }
  public static final int NULL     = 0;  
  public static final int EMPTY    = 1;  
  public static final int CONTENT  = 2;  
  public static final int DOCUMENT = 3;
  public static final int UNKNOWN  = 4;  //binary something
  public enum retType {
      NULL, EMPTY, CONTENT, DOCUMENT
  };
}

