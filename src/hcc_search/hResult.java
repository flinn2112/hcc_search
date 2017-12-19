/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search;

/**
 *
 * @author frankkempf
 * Result of an Extraction/Index Operation
 */


public class hResult{  
  public Object  m_oPayLoad   ;
  public String  m_strDocType ;
  public String  m_strText ;
  public int     m_eRetType  ;
  public Integer m_iPayLoadLen ;
  public int     m_iStatusCode ;
  public String  m_strFilename ;
  public String  m_strPath ;
  public String  m_strLastModified ;
  public long    m_lLastModified ;
  public String  m_strYear ;
  public String  m_strExt ; //file Extension
  public long    m_lFileLength ;
  public String  m_strType ; //DIR/FILE
  public String  m_strHash ;
  public hResult(){        
        m_oPayLoad    = null ;
        m_strText     = null ;  //might be used by WebCrawlser to store original data
        m_strDocType  = null ;
        m_eRetType    = hResult.NULL ;
        m_iStatusCode = 0 ;     //status of processing
        m_strType     = "FILE" ; //default
    }
    public static final int NULL     = 0;  
    public static final int EMPTY    = 1;  
    public static final int CONTENT  = 2;  
    public static final int DOCUMENT = 3;
    public static final int UNKNOWN  = 4;  //binary something
  
    public static final int STATUS_UNKNOWN                  =      0 ;
    public static final int STATUS_OK                       =   1001 ;
    public static final int STATUS_ERR                      =   1002 ;
    public static final int ERR_IDX_ALL_GOOD                =      1 ;
    public static final int ERR_IDX_NOT_HANDLED             =  -1001 ;
    public static final int ERR_IDX_FILE_NOT_ACCESSIBLE     = -11001 ;
    public static final int ERR_IDX_FILE_NO_EXT             = -11002 ;
    public static final int ERR_IDX_FILE_EXTENSION_EXCLUDED = -11003 ;
  
  
  
  public enum retType {
      NULL, EMPTY, CONTENT, DOCUMENT
  };
}



