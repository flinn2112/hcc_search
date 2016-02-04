/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.result;

import hcc_search.hcc_search_ExtendedAttributes;
import hcc_search.indexer.IndexData;

/**
 *
 * @author frankkempf
 */
public interface searchResultOut {
  public String out(String strTitle, String strFilename, String strLastModified, hcc_search_ExtendedAttributes o) ;
  public String out(String strTitle, String strFilename, String strLastModified) ;
  public String out(IndexData idxD[]) ;
  public String outURL(String strTitle, String strShortText, String strFilename) ;
}
