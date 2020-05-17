/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_pdfWndExtractor2020;

import org.apache.pdfbox.text.PDFTextStripperByArea;

/**
 *
 * @author frank
 */
/*
XP: eine Klasse, die eine Seite in n Regionen Vertikal unterteilt.
    Genutzt, um Anfang und Ende des Textsbereichs auf einer Seite zu finden.
Beispiel: 
    Einkaufsbeleg, dieser hat nur einen schmalen Bereich, und kann an jeder beliebigen Stelle der Seite beginnen/enden.
    Ziel ist es die Breite des Textes zu bestimmen.
    Man könnte also die Seite in 10 vertikale Bereiche unterteilen und jeweils den Text extrahieren.
    Wenn dieser leer ist, dann nächste Spalte, bis ein Text kommt - das ist dann der Anfang.
    Das Ende ist dann wieder da, wo kein Text mehr extrahiert wird.
*/
 class clsColumRegions{ //seltsamerweise lässt sich die Klasse nicht von clsRegions ableiten.
     
     
     public static int getRegions(PDFTextStripperByArea pdfArea, float fDocWidth, float fDocHeight, int iNumColumns){
        float fX = 0f ;
        int   iColWidth = 0 ;
        fX = pdfWndExtractor.MetrictoPT(1600) ;
        
        if( 0 == fDocWidth ) return -1 ;
        iColWidth = Math.round(Math.round(fDocWidth) / iNumColumns) ;
        for(int i = 0; i < iNumColumns; i++){
            pdfArea.addRegion(String.valueOf(i) , new java.awt.geom.Rectangle2D.Float(
                              iColWidth * i, //X
                              0,  //Y
                              iColWidth, //pdfWndExtractor.MetrictoPT(500),
                              fDocHeight
                           ));
        } 
         return 0 ;
     }
}
