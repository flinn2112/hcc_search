/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search;

import hcc_search.search.customSearchCtrl;
import hcc_search.result.sResult;
import hcc_search.result.searchResultHTML;
import hcc_search.config.IConfigProcessor;
import java.beans.*;
import java.io.Serializable;
import java.io.UnsupportedEncodingException ;
import java.util.Hashtable ;
/**
 *
 * @author frankkempf
 */
public class hcc_search_Bean implements Serializable, IConfigProcessor {
    
    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";
    private String hitsPerPage = null ;
    public  String m_strIndexPath = null ;
    public  String m_strSubstPath = null ;
    private PropertyChangeSupport propertySupport;
    private Hashtable m_htConfig = null ;
    
    public hcc_search_Bean() {
        propertySupport = new PropertyChangeSupport(this);
        m_htConfig = new Hashtable() ;
    }
    
    public String getSampleProperty() {
        return hitsPerPage;
    }
    
    public void setSampleProperty(String value) {
        String oldValue = hitsPerPage;
        hitsPerPage = value;
        propertySupport.firePropertyChange(PROP_SAMPLE_PROPERTY, oldValue, hitsPerPage);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    public String getID(){
     return "2114" ; 
    }
    //Get from hashtable
    public String getIni(String strName){
        String strRet = "" ;
        if( null == m_htConfig || null == strName ){
            return strRet ;            //empty string - not null
        }
        strRet = (String)m_htConfig.get(strName) ;        
        if( strRet == null ) strRet = "" ;
        return strRet ;
    }
    
    //config reading- obsolete - use getIni
    public String processLine(String strLine, String strCfgType){
        String strRet = null ;
        String[] rSplit = null ; 
        if( strCfgType.equals("index")){
            strRet = strLine.replace("index:", "") ;
            m_strIndexPath = strRet ;
        }
        if( strCfgType.equals("substDriveLetter")){
            strRet = strLine.replace("substDriveLetter:", "") ;
            m_strSubstPath = strRet ;
        }       
        return strRet;
    }
    
    /*
     called when no results found - will display a language dependend message.
     */
    
    public String emptyResult(String strBasePath, String strMsgPath, 
                   String strFilename, String strLanguage){
        String strPath = null ; ;
        //paths should already contain trailing '/'.
        strPath = strBasePath + strMsgPath + strLanguage + "/" + strFilename;
        return hcc_utils.getFile(strPath) ;
    }
    
    
    
    public String strToUTF8(String strOrg){
        String strResult = null ;
        try {
                byte[] utf8Bytes = strOrg.getBytes("UTF8");
                //byte[] defaultBytes = strOrg.getBytes();
                strResult = new String(utf8Bytes, "UTF8");
                
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                strResult = strOrg ;
            }
            return strResult ;
    }
    
    //new 3/2012: ClientPrefs can control the searchresult
    public sResult search2(String m_strIndexPath, hcc_search_opts so, String strClientPref, boolean bConvertToUTF8){
       customSearchCtrl csc  = null  ;
       String strResult     = null ;
       sResult oResult      = null ;        
       csc = new customSearchCtrl() ;
       try{
           oResult = csc.search2(so, new searchResultHTML(strClientPref)) ;
       }catch(Exception ex){          
           strResult = ex.toString() ;
       }
       if( oResult != null ){
        oResult.m_strSearchResult = this.strToUTF8(oResult.m_strSearchResult) ;
       }
       return oResult ;
    }
    
    //Read settings.
    public  void readConfig(String strFullPath, String strType){
        int iRet = 0 ;
        iRet = hcc_utils.readIniFile(strFullPath, m_htConfig) ; //get config ini into hashtable
    }
}


