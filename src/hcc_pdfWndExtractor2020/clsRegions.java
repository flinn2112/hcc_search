/*
 Define Regions of a Document that are later extracted by pdfBox
 
 */
package hcc_pdfWndExtractor2020;

import java.awt.geom.Rectangle2D;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import java.util.ArrayList;

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
class InvoiceRegionsDE{
    
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
    
    public float getSubjectX(float fDocHeight){
        return 0 ;
    }
    public float getSubjectWidth(float fDocHeight){
       return (int)fDocHeight ;
    }
    public float getSubjectHeight(float fDocHeight){
       return (int)fDocHeight / 10 ;  //just an approximation, would be 30 mm on DINA4
    }
    
    public float getSubjectY(float fDocHeight){
        return this.getAddressLeftHeight(fDocHeight) + this.getSubjectHeight(fDocHeight) ; 
    }
    
    
    
    public static int getRegions(PDFTextStripperByArea pdfArea, float fDocWidth, float fDocHeight){
        InvoiceRegionsDE ir = new InvoiceRegionsDE() ;
        //ArrayList<Rectangle2D> rRect = new ArrayList<Rectangle2D>();
        //Rect rect = null ;
        int iWidth = 0 ;
        int iHeight = 0 ;
        
        //Address Left
       
        pdfArea.addRegion("AddrLeft", new java.awt.geom.Rectangle2D.Float(
                           ir.getAddressLeftX(fDocWidth), 
                           ir.getAddressLeftY(fDocHeight),
                           ir.getAddressLeftWidth(fDocWidth),
                           ir.getAddressLeftHeight(fDocHeight)
                        ));
        //Addr Right
        
         pdfArea.addRegion("AddrRight", new java.awt.geom.Rectangle2D.Float(ir.getAddressRightX(fDocWidth), 
                           ir.getAddressRightY(fDocWidth),
                           ir.getAddressRightWidth(fDocWidth),
                           ir.getAddressRightHeight(fDocHeight)
                        ));
        //Subject
       
         pdfArea.addRegion("Subject", new java.awt.geom.Rectangle2D.Float(ir.getSubjectX(fDocWidth), 
                           ir.getSubjectY(fDocHeight),
                           ir.getSubjectWidth(fDocWidth),
                           ir.getSubjectHeight(fDocHeight)
                        ));
         
        return 0 ;        
    } 

}

public class clsRegions {
    private float m_fDocWidth ;
    private float m_fDocHeight ;
    
    public clsRegions(float fDocWidth, float fDocHeight){
        this.m_fDocWidth  = fDocWidth ;
        this.m_fDocHeight = fDocHeight ;
    }
    
    public int StdInvoiceRegions(PDFTextStripperByArea pdfArea){
        return InvoiceRegionsDE.getRegions(pdfArea, this.m_fDocWidth, this.m_fDocHeight) ;
    }    
}
