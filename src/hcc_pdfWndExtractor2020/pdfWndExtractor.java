/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_pdfWndExtractor2020;

import java.awt.geom.Rectangle2D;
import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripperByArea;

/**
 *
 * @author frank
 * Measurement units inside a PDF are in points, a traditional graphic industry measurement unit.
 * 1 pt = 1/72 inch
 */
public class pdfWndExtractor {
    public pdfWndExtractor(){
        System.out.println("cstor::pdfWndExtractor") ;
    }
    
    //PT to mm
    public static float PTtoMetric(float f72){
        return (float)(f72 / 72 * 25.4) ;
    }
    //mm to PT
    public static float MetrictoPT(float f10){
        return (float)(f10 / 10 / 25.4 * 72 ) ;
    }
    protected float getAddressWidthLeft(float fDocWidth){
        return (float)(fDocWidth / 2) ;
    }
    /*
      Which is same, but semantically separate
    */
     protected float getAddressWidthRight(float fDocWidth){
        return this.getAddressWidthLeft(fDocWidth) ;
    }
     protected float getAddressHeight(float fDocHeight){
        return (float)(fDocHeight / 3) ;
    }
   
     
    
    public boolean extractTest(String strFilename){
        int page = 0;
        int x = 0;
        int y = 0;
        int width = 250 ;
        int height = 250 ;
        float fDocWidth  = 0f ;
        float fDocHeight = 0f ;
        PDPage docPage = null ;
        PDDocument document = null ;
        PDFTextStripperByArea textStripper1 = null ;
        PDFTextStripperByArea textStripper2 = null ;
        try{
            document = PDDocument.load(new File(strFilename));
            textStripper1 = new PDFTextStripperByArea();
            textStripper2 = new PDFTextStripperByArea();
            docPage = document.getPage(page);
            fDocWidth = docPage.getMediaBox().getWidth() ;
            //umrechnen in mm fDocWidth = fDocWidth
            fDocHeight = docPage.getMediaBox().getHeight();
            float fWidth = this.getAddressWidthLeft(fDocWidth) ;  
            float fHeight = this.getAddressHeight(fDocWidth) ;  
            Rectangle2D rect = new java.awt.geom.Rectangle2D.Float(x, y, 
                                                                    fWidth, 
                                                                    fHeight);
            
            clsRegions r = new clsRegions(fDocWidth, fDocHeight) ;
            //r.StdInvoiceRegions(textStripper) ;
            
            r.VBAuszugRegionsS(textStripper1) ;
            r.VBAuszugRegionsH(textStripper2) ;
            //textStripper.addRegion("region", rect);
            
            System.out.println(fDocWidth);
            System.out.println(fDocHeight);
            textStripper1.extractRegions(docPage);
            textStripper2.extractRegions(docPage);
        }
        catch(Exception ex){
            return false ;
        }
        String textForRegion = textStripper1.getTextForRegion("SOLL");
        textForRegion = textStripper2.getTextForRegion("HABEN");
        //textForRegion = textStripper.getTextForRegion("Subject");
        System.out.println(textForRegion);
        return true;
    }
}
