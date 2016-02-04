/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.indexer;
import hcc_search.hcc_exception;
import java.io.File     ;
import java.util.Vector ;
/**
 *
 * @author frankkempf
 */
public class IndexData {
    public String  m_strURN   = null ;
    public String  m_strText  = null ;
    public String  m_strDocType = null ;
    public String  m_strPath  = null ;
    public String  m_strID = null  ;
    //public boolean m_bStoreText = false ;
    public Vector<IndexField> m_VFields = null ;
    public IndexData(String strURN, String strText, String strDocType){
        String pattern = "[[^a-z0-9A-Z]]";
        m_strURN   = strURN   ;
        m_strText  = strText  ;
        m_strDocType = strDocType ;
        m_strID      = m_strURN.replaceAll(pattern, "_") ; 
        this.m_VFields = new Vector() ;
        this.addField("ID", m_strID);
    }
    
    public String makeFilename(){
        return m_strURN + "." + m_strDocType ;        
    }
   
    public String getFullPath(){
        return m_strPath + "/" + m_strURN  + "." + m_strDocType;
    }
     
    /*
     * Set path to store the text(i.e. for memos
     */
    public void setStoragePath(String strPath) throws hcc_exception{     
       
       boolean exists = (new File(strPath)).exists();
        if (exists) {
            // File or directory exists
        } else {
            // File or directory does not exist
            throw new hcc_exception(strPath + "not accessible") ;
        }
       this.m_strPath = strPath ;    
    }
    
    public void setStorageIndicator() throws hcc_exception{
      if( null == this.m_strPath ){
          throw new hcc_exception("Path is not set") ;
      }
      //this.m_bStoreText = true ;
    }
    
    public void addField(String strName, String strValue){
        this.m_VFields.add(new IndexField(strName, strValue));
    }
    
    public IndexField getField(int iIndex){
        return this.m_VFields.get(iIndex) ;
    }
    
}

class IndexField{    
    
    public String m_strName  = null ;
    public String m_strValue = null ;
    
    
    public IndexField(String strName, String strValue){        
        m_strName  = strName  ;
        m_strValue = strValue ;
    }
}