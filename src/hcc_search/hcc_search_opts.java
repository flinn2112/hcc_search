/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search;
import hcc_search.logger.* ;
/**
 *
 * @author flinn2112
 */
public class hcc_search_opts {
    

 public String   m_strIndexDir ;
 public String   m_strField ;
 public int      m_iOutputFormat ;
 public int      m_iHitsPerPage ;
 public boolean  m_bSearchWhiteSpace ;
 public boolean  m_bCreateShortTextFromContent = false; //spezielle option: content laden und passenden shorttext daraus generieren.
 public String   m_strSubstDriveLetter ;
 public String   m_strQuery ;
 public String   m_strHighlightClass = null ; //HTML Highlighting CSS 
 public boolean  m_bHighlightMarkup = false ;
 public boolean  m_bLogResult = false ; //put some extra information into the results
 public ILogger  m_Logger = null ;
 public Object   clientCtxt = null ;  //store caller arbitrary data.
 
 
 
 
 public hcc_search_opts(String strQuery,  String strField, String strIndexDir, boolean bSearchWhiteSpace, 
             int iOutputFormat, String strSubstDriveLetter, boolean bHighlightMarkup, String   strHighlightClass){
    this.init(strQuery, strField, strIndexDir, bSearchWhiteSpace, iOutputFormat, strSubstDriveLetter);
    m_strHighlightClass    = strHighlightClass ;
    m_bHighlightMarkup     = bHighlightMarkup ;
 }
 
 public hcc_search_opts(String strQuery,  String strField, String strIndexDir, boolean bSearchWhiteSpace, 
             int iOutputFormat, String strSubstDriveLetter){
    this.init(strQuery, strField, strIndexDir, bSearchWhiteSpace, iOutputFormat, strSubstDriveLetter);
 }
 
 private void init(String strQuery,  String strField, String strIndexDir, boolean bSearchWhiteSpace, 
             int iOutputFormat, String strSubstDriveLetter){
    m_strIndexDir          = strIndexDir ;
    m_strField             = strField    ;
    m_iOutputFormat        = iOutputFormat ;
    m_strSubstDriveLetter  = strSubstDriveLetter ;
    m_iHitsPerPage         = 50 ;  //default
    m_bSearchWhiteSpace    = bSearchWhiteSpace ;
    m_strQuery             = strQuery ;    
 }
 
 public void setLogger(ILogger oLogger){
     m_Logger = oLogger ;
 }
 
 public String toString(){
     StringBuilder sb = new StringBuilder() ;
     sb.append(m_strIndexDir) ;
     sb.append(" --- ") ;
     sb.append(m_strField);
     sb.append(" --- ") ;
     sb.append(m_strQuery);
     return sb.toString() ;
 }
 
  public static final int _HTML_     = 0;  
  public static final int _JSON_     = 1;  
  public static final int _RAW_      = 2;  
  public static final int _XML_      = 3;
  public static final int _PATHS_      = 4;
}
