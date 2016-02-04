/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search.logger;

/**
 *
 * @author hcc
 */ 
public interface ILogger {
    public boolean open(String strName) ;
    public boolean open(String strName, boolean bAppend) ;
    public void close() ;
    public void log(String strWho, String strWhat, int iSeverity) ;
    
}
