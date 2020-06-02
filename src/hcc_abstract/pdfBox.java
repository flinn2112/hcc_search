/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_abstract;

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.text.PDFTextStripperByArea;

/**
 * Dieses Paket dient dazu die Referenzen auf andere Komponenten(z.B. pdfBox, Tika) nicht über alle Source Files zu verteilen.
 * Ändern sich diese oder werden obsolet, kann die jeweilige Abstraktionsklasse das auffangen.
 * @author frank
 * 2020 pdfBox Abstraktion, damit z.B. clsRegions nicht direkt pdfBox verwenden muss. 
 * Hier: eine Besonderheit ist die Verwendung mehrerer TextStripper.
 *       Das wird nötig, wenn es überlappende Bereiche gibt, denn die Bereiche sind greedy und nehmen sich gegenseitig den Text weg.
 */
public class pdfBox {
    
    public PDFTextStripperByArea[] m_rTxtStrippers = null ;
    
    /*
        noch schöner, aber viel mehr Aufwand wäre es, wenn die Klasse selber erkennen würde,
        dass mehrere Stripper gebraucht werden - dazu müssten aber alle Areas auf Kollisionen getestet werden - 
        hoher Aufwand - deshalb hier als Parameter.
    */
    public pdfBox(int iNumStrippers){
        
    }
    
    public void addRegion(int iStripper, String strName, java.awt.geom.Rectangle2D rRect ){
        this.m_rTxtStrippers[iStripper].addRegion(strName, rRect);
    }
    
    public String getTextForRegion(String strRegionName){
        String strRet = null ;
        for(int i = 0; i < this.m_rTxtStrippers.length;i++){
            strRet = this.m_rTxtStrippers[i].getTextForRegion(strRegionName) ;
            if( null != strRet ){
                break ;
            }
        }
        return strRet ;
    }
    /*
        Alle Regionen zurückliefern.
        
    */
    public List getRegions(){
        ArrayList arRegions = new ArrayList() ;
        List  rRegions = new ArrayList() ;
        String strRegion = null ;
        for(int i = 0; i < this.m_rTxtStrippers.length;i++){
            rRegions = this.m_rTxtStrippers[i].getRegions() ; //das gibt eine Liste mit Strings
            for (Object element : rRegions) {
                strRegion = (String)element;
                arRegions.add(element) ;
             }
        }
        return arRegions ;
    } ;
}
