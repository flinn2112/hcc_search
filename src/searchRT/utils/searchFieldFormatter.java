/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package searchRT.utils ;

import java.math.* ;
     



/**
 *
 * @author Frank
 */
public class searchFieldFormatter {
     public static String fileSize(long lFilesize){
         Long lKB = 0L ;
         String strUnit = "KB" ;
         StringBuilder sb = new StringBuilder() ;
         lKB = (long)Math.ceil(Math.round( lFilesize / 1024 )) ;
         if( 0 == lKB){
             strUnit = "Bytes" ;
             lKB = lFilesize ;
         }
         sb.append("[") ;
         sb.append(lKB.toString()) ;
         sb.append("] ") ;
         sb.append(strUnit) ;
         return sb.toString() ;
     }
}
