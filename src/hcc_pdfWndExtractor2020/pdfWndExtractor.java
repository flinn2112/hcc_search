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
 */
public class pdfWndExtractor {
    public pdfWndExtractor(){
        System.out.println("cstor::pdfWndExtractor") ;
    }
    public boolean extractTest(String strFilename){
        int page = 0;
        int x = 0;
        int y = 0;
        int width = 250 ;
        int height = 250 ;

        PDDocument document = null ;
        PDFTextStripperByArea textStripper = null ;
        try{
            document = PDDocument.load(new File(strFilename));
            textStripper = new PDFTextStripperByArea();
            Rectangle2D rect = new java.awt.geom.Rectangle2D.Float(x, y, width, height);
            textStripper.addRegion("region", rect);
            PDPage docPage = document.getPage(page);
            textStripper.extractRegions(docPage);
        }
        catch(Exception ex){
            return false ;
        }
        String textForRegion = textStripper.getTextForRegion("region");
        System.out.println(textForRegion);
        return true;
    }
}
