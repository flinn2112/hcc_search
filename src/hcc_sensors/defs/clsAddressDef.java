/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_sensors.defs;

/**
 * Liefert Definitionen zu Mustern
 * Ausbauen für:
 * -Mehrsprachigkeit.
 * -Weitere Datenquellen nutzen
 * @author frank
 */


/*
[a-zA-Z]*)*\s Name
(\s*[0-9])+ Zahl
([A-Za-z\s])+(\s*[0-9])+ primitiver Strassenname mit Hausnummer
*/
class clsName{
   public static String get(){
        return "([A-Za-z]\\s*)+"; 
    } 
}

class clsStreet{
   public static String get(){
        return "(([A-Za-z\\.-])+\\s*)+([0-9])+\\s+"; 
    } 
}

class clsCity{
   public static String get(){
        return "([A-Za-z]\\s*)+(\\s*[0-9])+"; 
    } 
}
public class clsAddressDef {
    
    public static String name(){
        return clsName.get();
    }
    public static String street(){
        return clsStreet.get();
    }
     public static String city(){
        return clsCity.get();
    }
}
