/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_miners;

/**
 * Rechnung erkennen
 * @author frank
 */
public class clsInvoiceSensor extends clsSensor{
    
    //ein
    public int hasDate( String strText ){        
        return 0 ;
    }
    /*
        Rechnung, Invoice, Kostennote
    */
    public int hasInvoiceToken( String strText ){        
        return 0 ;
    }
    public int hasIBAN( String strText ){        
        return 0 ;
    }
}
