/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_abstract;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
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
    protected PDDocument m_Document = null ; 
    protected PDPage m_DocPage      = null ;    
    public float m_fDocWidth     = 0 ; 
    public float m_fDocHeight    = 0 ; 
    public PDFTextStripperByArea[] m_rTxtStrippers = null ;
    
    /*
        noch schöner, aber viel mehr Aufwand wäre es, wenn die Klasse selber erkennen würde,
        dass mehrere Stripper gebraucht werden - dazu müssten aber alle Areas auf Kollisionen getestet werden - 
        hoher Aufwand - deshalb hier als Parameter.
    */
    public pdfBox(String strFilename, int iPageNum, int iNumStrippers){
        
        try{
            this.m_Document = PDDocument.load(new File(strFilename));
            if( null != this.m_Document ){
                this.m_DocPage = this.m_Document.getPage(iPageNum);
            }
            if( null != this.m_DocPage ){
                this.m_fDocWidth  = this.m_DocPage.getMediaBox().getWidth() ;            
                this.m_fDocHeight = this.m_DocPage.getMediaBox().getHeight();
            }
            this.m_rTxtStrippers = new PDFTextStripperByArea[iNumStrippers] ;
            for( int i = 0 ; i < iNumStrippers ; i++){
                this.m_rTxtStrippers[i] = new PDFTextStripperByArea() ;
            }
            
        }catch(Exception ex){
            
        }
        
    }
    public Hashtable getTexts(){
        Hashtable dict = new Hashtable();
        ArrayList rTexts = new ArrayList() ;
        List lRegions = this.getRegions() ; //IDs der Regionen
        String strRegionName = null ;
        String strText       = null ;
        for (Object element : lRegions) {
            strRegionName = (String)element;
            strText = this.getTextForRegion(strRegionName);
            dict.put(strRegionName, strText) ;
        }
        return dict ;
    }
    public void addRegion(int iStripper, String strName, java.awt.geom.Rectangle2D rRect ){
        this.m_rTxtStrippers[iStripper].addRegion(strName, rRect);
    }
    
    public String getTextForRegion(String strRegionName){
        String strRet = null ;
        for(int i = 0; i < this.m_rTxtStrippers.length;i++){
            try{
                strRet = this.m_rTxtStrippers[i].getTextForRegion(strRegionName) ;
            }catch(Exception ex){
                continue ;
            }            
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
    
    public boolean extractRegions(){
        
            for(int i = 0; i < this.m_rTxtStrippers.length;i++){
                try{
                    this.m_rTxtStrippers[i].extractRegions(this.m_DocPage);
                }catch(Exception ex){
                    return false ;
                }
            }
        return true ;        
    }
}
