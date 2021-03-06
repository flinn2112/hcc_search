/*
 Define Regions of a Document that are later extracted by pdfBox
 
 */
package hcc_pdfWndExtractor2020;

import hcc_abstract.pdfBox;
import hcc_search.hcc_utils;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import java.util.Hashtable;
/**
 *
 * @author frank
 * Returns 
 */
class Rect{
    public String id = null ;
    public int X = 0 ;
    public int Y = 0 ;
    public int Width = 0 ;
    public int Height = 0 ;
    public Rect(String strID, int x, int y, int Width, int Height){
        this.id = strID ;
        this.X = x ;
        this.Y = y ;
        this.Width = Width ;
        this.Height = Height ;
    }
}

interface IDocRegions{
    public int createRegions(String strFilename, int iPageNum) throws Exception;
    public boolean serialize(String strPath, String strFilename,  String strContent) throws IOException;
    public boolean process();
     public Hashtable getTexts() ;
}

class clsDocRegions implements IDocRegions{
    public hcc_abstract.pdfBox m_pdf = null ;
    
    
    public clsDocRegions(){
        
    }
    public Hashtable getTexts(){
        return null ;
    }
    public boolean process(){
        return this.m_pdf.extractRegions() ;
    }
  
    public static int getRegions(PDFTextStripperByArea pdfArea, float fDocWidth, float fDocHeight){ 
         pdfArea.addRegion("ALL", new java.awt.geom.Rectangle2D.Float(
                           0, 
                           0,
                           fDocWidth - 1,
                           fDocHeight - 1
                        ));
        return 0;
    } ;
    
    public int createRegions(String strFilename, int iPageNum) throws Exception{
        return 0 ;
    }
    
    public boolean serialize(String strPath, String strFilename,  String strContent) throws IOException {
        if( false == hcc_utils.checkPath(strPath)){
            throw new IOException("Directory ["  + strPath + "] is not accessible") ;
        }
        return hcc_utils.writeFile(strPath + File.separator + strFilename + ".json", strContent) ;
    }
}

class InvoiceRegionsDE extends clsDocRegions  implements IDocRegions{
    
    public InvoiceRegionsDE(){        
        
    }
    
    public int getAddressLeftX(float fDocWidth){
        return 0 ;
    }
    
    public int getAddressLeftY(float fDocHeight){
        return 0 ;
    }    
    
    public float getAddressLeftWidth(float fDocWidth){
        return fDocWidth / 2 ;
    }
    
    public float getAddressLeftHeight(float fDocHeight){
        return (fDocHeight / 3) ;
    }
    
    public float getAddressRightX(float fDocWidth){
        return (int)(fDocWidth / 2) ;
    }
    
    public float getAddressRightY(float fDocWidth){
        return 0; //(int)(fDocHeight / 3) ;
    }    
    
    /*
      Which is same, but semantically separate
    */
    public float getAddressRightWidth(float fDocWidth){
        return this.getAddressLeftWidth(fDocWidth) ; //same
    }
    
    
    public float getAddressRightHeight(float fDocHeight){
        return (int)(fDocHeight / 3) ;
    }
    
    public float getSubjectX(float fDocWidth){
        return 0 ;
    }
    //Subject kann sich mit Adresse überlagern
    //Ein DINA4 ist üblicherweise in drei Teile gefaltet. Subject sollte am Ende des ersten Drittels stehen
    //Höhe ist etwa 842 Einheiten, also sollte man etwa bei 300 - 100 = 200 rauskommen.
    public float getSubjectY(float fDocHeight){
        return ( fDocHeight * 3 / 11 ) ; //ein Zehntel wäre bisschen zu wenig. 1/4 ist zu hoch 1/3 zu tief
    }
    public float getSubjectWidth(float fDocWidth){
       return (int)fDocWidth ;
    }
    public float getSubjectHeight(float fDocHeight){
       return (int)fDocHeight / 5 ;  //just an approximation, would be 30 mm on DINA4
    }
    
    
    public float getFooterX(float fDocWidth){
        return 0; 
    }
    
    //ein DINA4 Blatt ist 30 cm oder etwa 12 Inch also, nehmen wir das letzte Zehntel - etwa 3 cm
    public float getFooterY(float fDocHeight){
        return (int)(fDocHeight / 10 * 9) ;
    }  
    
    public float getFooterHeight(float fDocHeight){
        return (int)(fDocHeight - this.getFooterY(fDocHeight)) ;
    } 
    public float getFooterWidth(float fDocWidth){
        return (int)(fDocWidth) ;
    } 
    
    public Hashtable getTexts(){
        return this.m_pdf.getTexts() ;
    }
    
    public int createRegions(String strFilename, int iPageNum) throws Exception{
        try{
            this.m_pdf = new pdfBox(strFilename, 0, 2) ;        
        }catch(Exception ex){
            throw(ex) ;
        }
        
        
        int iWidth = 0 ;
        int iHeight = 0 ;
        
        //Address Left
       
        this.m_pdf.addRegion(0, "AddrLeft", new java.awt.geom.Rectangle2D.Float(
                           this.getAddressLeftX(this.m_pdf.m_fDocWidth), 
                           this.getAddressLeftY(this.m_pdf.m_fDocHeight),
                           this.getAddressLeftWidth(this.m_pdf.m_fDocWidth),
                           this.getAddressLeftHeight(this.m_pdf.m_fDocHeight)
                        ));
        //Addr Right
        
         this.m_pdf.addRegion(0, "AddrRight", new java.awt.geom.Rectangle2D.Float(this.getAddressRightX(this.m_pdf.m_fDocWidth), 
                           this.getAddressRightY(this.m_pdf.m_fDocWidth),
                           this.getAddressRightWidth(this.m_pdf.m_fDocWidth),
                           this.getAddressRightHeight(this.m_pdf.m_fDocHeight)
                        ));
        //Subject muss in einen anderen Stripper
       
         this.m_pdf.addRegion(1, "Subject", new java.awt.geom.Rectangle2D.Float(this.getSubjectX(this.m_pdf.m_fDocWidth), 
                           this.getSubjectY(this.m_pdf.m_fDocHeight),
                           this.getSubjectWidth(this.m_pdf.m_fDocWidth),
                           this.getSubjectHeight(this.m_pdf.m_fDocHeight)
                        ));
        this.m_pdf.addRegion(1, "Footer", new java.awt.geom.Rectangle2D.Float(this.getFooterX(this.m_pdf.m_fDocWidth), 
                           this.getFooterY(this.m_pdf.m_fDocHeight),
                           this.getFooterWidth(this.m_pdf.m_fDocWidth),
                           this.getFooterHeight(this.m_pdf.m_fDocHeight)
                        )); 
        return 0 ;        
    } 

}


/*
    Ein einfacher Versuch, die Soll/Haben Positionen zu extrahieren.
*/
class VBAuszugRegionsS{
     public static int getRegions(PDFTextStripperByArea pdfArea, float fDocWidth, float fDocHeight){
        float fX = 0f ;
        fX = pdfWndExtractor.MetrictoPT(1600) ;
         pdfArea.addRegion("SOLL", new java.awt.geom.Rectangle2D.Float(
                           fX,                 //X
                           0,                                               //Y
                           100, //pdfWndExtractor.MetrictoPT(350),          //WIDTH
                           fDocHeight                                       //HEIGHT
                        ));         
         return 0 ;
     }
}

class VBAuszugRegionsH{
     public static int getRegions(PDFTextStripperByArea pdfArea, float fDocWidth, float fDocHeight){
        float fX = 0f ;
        fX = pdfWndExtractor.MetrictoPT(1600) ;
   
         pdfArea.addRegion("HABEN", new java.awt.geom.Rectangle2D.Float(
                           pdfWndExtractor.MetrictoPT(1800), 
                           0,
                           90, //pdfWndExtractor.MetrictoPT(500),
                           fDocHeight
                        ));
         
         return 0 ;
     }
}

public class clsRegions {
    protected float m_fDocWidth ;
    protected float m_fDocHeight ;
    public static final int RTYPE_INVOICE = 1 ;
    
    public clsRegions(float fDocWidth, float fDocHeight){
        this.m_fDocWidth  = fDocWidth ;
        this.m_fDocHeight = fDocHeight ;
    }
    
    public int all(PDFTextStripperByArea pdfArea){
        return clsDocRegions.getRegions(pdfArea, this.m_fDocWidth, this.m_fDocHeight) ;
    } 
    
    public int StdInvoiceRegions(PDFTextStripperByArea pdfArea){
        return InvoiceRegionsDE.getRegions(pdfArea, this.m_fDocWidth, this.m_fDocHeight) ;
    }   
    
    public int VBAuszugRegionsS(PDFTextStripperByArea pdfArea) {
        return VBAuszugRegionsS.getRegions(pdfArea, this.m_fDocWidth, this.m_fDocHeight) ;
    } 
    public int VBAuszugRegionsH(PDFTextStripperByArea pdfArea) {
        return VBAuszugRegionsH.getRegions(pdfArea, this.m_fDocWidth, this.m_fDocHeight) ;
    } 
}


