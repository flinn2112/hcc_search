/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.indexer;

import hcc_search.hcc_exception;
import hcc_search.hcc_search_ExtendedAttributes;
import hcc_search.hcc_utils;
import java.beans.*;
import java.io.Serializable;
import java.util.Date ;
import java.text.DateFormat ;
/**
 *
 * @author frankkempf
 */
public class hcc_index_bean implements Serializable {
    
    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";
    private String sampleProperty;
    private PropertyChangeSupport propertySupport;
    
    public hcc_index_bean() {
        propertySupport = new PropertyChangeSupport(this);
    }
    
    public String getSampleProperty() {
        return sampleProperty;
    }
    
    public void setSampleProperty(String value) {
        String oldValue = sampleProperty;
        sampleProperty = value;
        propertySupport.firePropertyChange(PROP_SAMPLE_PROPERTY, oldValue, sampleProperty);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    public void index(String strIndexPath,
                      String strDomain,                      
                      String strTitle,
                      String strShortTxt,
                      String strMemoPath, 
                      String strURN, 
                      String strDate,
                      String strDocType,
                      String strCategory,
                      String strSrc,
                      String strURL,                           
                      Date   dtExpireDate,
                      String strContent,
                      hcc_search_ExtendedAttributes oEA ){
            String strExpireDate = null ; 
            String strFullPath = null ;
            Indexer idx = new Indexer(strIndexPath, false, 0); //0=no trace
            IndexData idxD = new IndexData(strURN, strContent, strDocType) ;
            
            if( null != strMemoPath ){
                try{
                    idxD.setStoragePath(strMemoPath);
                    idxD.setStorageIndicator();  //set indicator that the content should be saved to file
                    strFullPath = idxD.getFullPath() ; //something like 
                    strURN = strFullPath ; //reset the path to point to the memo
                    hcc_utils.writeFile(strFullPath, strContent) ;
                }catch( hcc_exception ex){
                    System.out.println(ex.toString());
                    return ;
                }
                
            }
            
            if( null != strDomain){
                idxD.addField("domain", strDomain);
            }
            
            if( null != strTitle){
                idxD.addField("title", strTitle);
            }
            
            if( null != strShortTxt){
                idxD.addField("shorttext", strShortTxt);
            }
            if( null != oEA){
                //idxD.addField("shorttext", strShortTxt);
            }
            
            idxD.addField("URN", strURN);
            
            idxD.addField("date", strDate);
            
            
            
            idxD.addField("doctype", strDocType);
            if( null != strCategory){
                idxD.addField("category", strCategory);
            }
            if( null != strSrc )
                idxD.addField("source", strSrc);
            if( null != strURL)
                idxD.addField("url", strURL);
            /*
            //strExpireDate = hcc_utils.getDateString(dtExpireDate) ;
            idxD.addField("expire", strExpireDate);
            */
            try{
                idx.indexData(idxD);  
                
                idx.closeIndex();
            }catch(hcc_exception ex){}
            
    }
    
    public String getDateString(){
        long ts = (new Date()).getTime();
        DateFormat dataformat =  DateFormat.getDateInstance(DateFormat.LONG);
        return dataformat.format(ts); 
        
    }
}
