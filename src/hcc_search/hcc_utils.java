/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search;

import hcc_search.config.IConfigProcessor; 
import java.text.*;
import java.io.File;
import java.io.*;
import java.util.* ;
import java.net.*;
import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import javax.management.* ;

import java.nio.* ;
import java.nio.channels.FileChannel ;
import java.nio.channels.FileChannel.MapMode; 
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

/**
 *
 * @author frankkempf
 */
public class hcc_utils {
  public static Hashtable sHT_IncludeExt = null ;  //static hash table for include extensions
  
  public static void reportMessage(String strCategory, String strWho, 
          String strWhat, String strMessage){
     System.out.println ( "[" + strCategory + "] ["  + strWho + "] ["  + strWhat + "] ["  + strMessage + "]" );
  }  
    
  /*get last modified of a file as a string*/
  public static String getLastModifiedString(File f) {
     DateFormat dataformat =  DateFormat.getDateInstance(DateFormat.LONG);
     return dataformat.format(f.lastModified()); 
  } 
  
  
  public static String getTimestampString(long lTime) {
     DateFormat dataformat =  DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM  );
     
     return dataformat.format(lTime); 
  }
  
  public static String getDateString(Date dt) {
     DateFormat dataformat =  DateFormat.getDateInstance(DateFormat.LONG);
     return dataformat.format(dt); 
  }
  
  public static String getYear(File f) {
     DateFormat dataformat =  DateFormat.getDateInstance(DateFormat.YEAR_FIELD);     
     return dataformat.format(f.lastModified()); 
  } 
  
  public static Long getCheckSum(String strContent){
      byte bBuf[] = null ;
      if( null == strContent ){
          return 0L;
      }
      bBuf = strContent.getBytes() ;
      Checksum checksumEngine = new Adler32();
      
      checksumEngine.update(bBuf, 0, bBuf.length);
      return (Long)checksumEngine.getValue();
  }
   /*
    * the Date as string
    * 
    */
   public static String now() {
    Calendar cal = Calendar.getInstance();
    return hcc_utils.getDateString(cal.getTime()) ;
  }
   
   /*
    * the Timestamp as string
    * 
    */
   public static String timestamp_now() {
    Calendar cal = Calendar.getInstance();
    return hcc_utils.getTimestampString(cal.getTimeInMillis()) ;
  }
   //for search terms - need to escape all '/'
   public static String escapeTerm(String strIn){
       return  strIn.replace("/", "\\/") ;
        
   }
   
   public static byte[] byteReplace( byte[] bIn){
       int i = 0 ;
       int k = 0 ;
       
       byte[] bOut = new byte[bIn.length];
       for( i = 0; i < bIn.length; i++){
           if( bIn[i] > 0 ){
                bOut[k++] =  bIn[i];
           } 
           
       }
       return bOut ;
   }
   
   
  
  public static boolean checkPath(String strPath){      
      if(null == strPath){
          System.err.println("hcc_utils.checkPath: a path was delivered that is NULL") ;
          return false ;
      }
      File file = new File(strPath);
      return  file.exists();
  }
  
  public static String getFilename(String strPath){
     String strRet = null ;
     int iPos = 0 ;
     if(null == strPath) return "" ;
     
     /* it is quite rudimentary here: get the string after the last '.'.
      * But the string may start with '.' - then it is not an extension
     */
     if( strPath.startsWith(".") ){
        return "" ;
     }
     iPos = strPath.lastIndexOf(File.separator);
     if(iPos > 0){
         strRet = strPath.substring( iPos + 1 ) ;
     }     
     return strRet ;
  }
  
  public static String getExtension(String strFilename){
     
     String strRet = null ;
     int iPos = 0 ;
     if(null == strFilename) return "" ;
     
     /* it is quite rudimentary here: get the string after the last '.'.
      * But the string may start with '.' - then it is not an extension
     */
     if( strFilename.startsWith(".") ){
        return "" ;
     }
     iPos = strFilename.lastIndexOf(".");
     if(iPos > 0){
         strRet = strFilename.substring( iPos + 1 ) ;
     }
     
     return strRet ;
  }
  
  public static String getExtension(File f){
     String strFilename = null ;
     if(null == f) return "" ;
     strFilename = f.getName() ;
     
     
     return hcc_utils.getExtension(strFilename) ;
  }
  
  public static String getExtensionUpper(File f){
      String strRet = null ;
      try{
          strRet = hcc_utils.getExtension(f) ;
          if( null !=  strRet ){
             strRet = strRet.toUpperCase() ;
          }  
      }catch(java.lang.NullPointerException ex){
        strRet = "" ;
      }
      return strRet ;
      
  }
  
  public static String getExtensionUpper(String strFilename){
      String strRet = null ;
      try{
          strRet = hcc_utils.getExtension(strFilename) ;
          if( null !=  strRet ){
             strRet = strRet.toUpperCase() ;
          }  
      }catch(java.lang.NullPointerException ex){
        strRet = "" ;
      }
      return strRet ;
      
  }
  /*
   *  Checking for extensions that should not be excluded.
   * 
   */
  
  
  public static boolean isContained( String  strExt){
      if( null == strExt ) return false ;
      return null != hcc_utils.sHT_IncludeExt.get(strExt.toLowerCase()) ;
      
  }
  
  public static void ht_rebuild( String strFilename)  throws IOException{
      hcc_utils.sHT_IncludeExt = null ;
      hcc_utils.ht_build(strFilename);
  }
  
  public static void ht_build(String strFilename) throws IOException{      
      // Open the file that is the first 
      if( null == hcc_utils.sHT_IncludeExt ){
          hcc_utils.sHT_IncludeExt = new Hashtable() ;
      }
      else{  //does exist -> clear
          hcc_utils.sHT_IncludeExt.clear();
      }
  try{
      FileInputStream fstream = new FileInputStream(strFilename);
      // Get the object of DataInputStream
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String strLine;
  //Read File Line By Line
      while ((strLine = br.readLine()) != null)   {
      // Print the content on the console
      System.out.println (strLine);
      hcc_utils.sHT_IncludeExt.put(strLine.toLowerCase(), strLine.toLowerCase());
      }
  //Close the input stream
     in.close();
     br.close();
    }catch (Exception ex){   //Catch exception if any
       System.err.println("Error: " + ex.getMessage());
    }
  }
      
      /*
      FileInputStream fis = null;
      BufferedReader br = null ;      
      String strTmp = new String() ;      
      File file = null ;    
      file = new File(strFilename);
      
      
      try {
          fis = new FileInputStream(file);
          br = new BufferedReader(new InputStreamReader(fis, "UTF-8")) ;
        } catch (FileNotFoundException ex) {
          // at least on windows, some temporary files raise this exception with an "access denied" message
          // checking if the file can be read doesn't help         
         
        }

        
        try{  
             System.out.println("loading exclude file [" + file + "]");
             while ((strTmp = br.readLine()) != null) {
                   ht.put(strTmp.toLowerCase(), strTmp.toLowerCase());
             }
             
        } catch (java.io.IOException ex){                
                System.out.println("skipping file [" + ex.getLocalizedMessage().toString() + "]");
        }
       br.close();
       fis.close();
  }
  */
  public static byte[] getDocument(String strPath)
    throws IOException {
      FileInputStream fis;
      BufferedReader br = null ;
      MappedByteBuffer buf = null ;
      char[] cBuf = null  ;
      byte[] bytes = null ;
      CharBuffer cb = null ;
      File file ;
      FileInputStream fin = null;
      FileChannel ch = null;
      
      cBuf = strPath.toCharArray() ;
      
    try {
        file = new File(strPath) ;
        fin = new FileInputStream(file);
        ch = fin.getChannel();
        int size = (int) ch.size();
        buf = ch.map(MapMode.READ_ONLY, 0, size);
        bytes = new byte[size];
        buf.get(bytes);
        //cBuf = bytes.toString(); //cb.get(cBuf) ; //.get(bytes) ; //.toString().toCharArray();      
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        cBuf = e.toString().toCharArray() ;
    } finally {
        try {
            if (fin != null) {
                fin.close();
            }
            if (ch != null) {
                ch.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            cBuf = e.toString().toCharArray() ;
        }
    }
      
       return bytes ;
     }
  /*getting contents of a usually HTML File as String*/
  public static String getFile(String strPath){
      FileInputStream fis;
      BufferedReader br = null ;
      StringBuilder sb = new StringBuilder() ;
      String strTmp = new String() ;
      
      
      try {
          fis = new FileInputStream(new File(strPath));
          br = new BufferedReader(new InputStreamReader(fis, "UTF-8")) ;
        } catch (Exception ex) {
          // at least on windows, some temporary files raise this exception with an "access denied" message
          // checking if the file can be read doesn't help         
     
          return null ;
        }

        
        try{  
             
             while ((strTmp = br.readLine()) != null) {
                   sb.append(strTmp);
             }
             br.close();
              fis.close();
        } catch (java.io.IOException ex){
           
        }finally{
           
           
        }
       
       
       return sb.toString() ;
     }
  
  //similar to processCfgFile - but somehow simpler.
  //puts all config in a name-value pair hashtable
  public static int readIniFile(String strPath, Hashtable htConfig){
       FileInputStream fis = null ;
      BufferedReader br = null ;
      StringBuilder sb = new StringBuilder() ;
      String strTmp = new String() ;
      String strNameValue[] = null ;
      
      try {
          fis = new FileInputStream(new File(strPath));
          br = new BufferedReader(new InputStreamReader(fis, "UTF-8")) ;
          System.out.println("readIniFile - opened: " + strPath );
        } catch (Exception ex) {
          // at least on windows, some temporary files raise this exception with an "access denied" message
          // checking if the file can be read doesn't help    
            System.err.println("readIniFile - error: " + ex.toString());
            return 0;
        }

        try{               
             while ((strTmp = br.readLine()) != null) {
                strNameValue = strTmp.split(":", 2) ;                
                htConfig.put(strNameValue[0], strNameValue[1]) ;   
             }
             br.close();
             fis.close();
        } catch (java.io.IOException ex){
           System.err.println("readIniFile - error: " + ex.toString());
        }finally{
           
           
        }
      return htConfig.size() ;
  }
  
  
  //used to process a config line by line and handle it by a Config Processor
  public static void processCfgFile(String strPath, IConfigProcessor cp, String strCfgType){
      FileInputStream fis = null ;
      BufferedReader br = null ;
      StringBuilder sb = new StringBuilder() ;
      String strTmp = new String() ;
      
      
      try {
          fis = new FileInputStream(new File(strPath));
          br = new BufferedReader(new InputStreamReader(fis, "UTF-8")) ;
          System.out.println("processCfgFile - opened: " + strPath );
        } catch (Exception ex) {
          // at least on windows, some temporary files raise this exception with an "access denied" message
          // checking if the file can be read doesn't help    
            System.err.println("processCfgFile - error: " + ex.toString());
            return ;
        }

        try{             
             while ((strTmp = br.readLine()) != null) {
                  if(strTmp.startsWith("#")){ //a comment line
                       
                    }else{
                      cp.processLine(strTmp, strCfgType);
                  }
                   
             }
             br.close();
             fis.close();
        } catch (java.io.IOException ex){
           System.err.println(ex.toString());
        }finally{
           
           
        }     
     }
  
  public static boolean writeFile(String strPath, String strContent){
      Writer output = null;
      boolean bRet = false ;
      
      //!CHECK PATH (later)
      File file = new File(strPath);
      try{
      output = new BufferedWriter(new FileWriter(file));
      output.write(strContent);
      output.close();
      bRet = true ;
      }catch( IOException ex){
          
      }
      return bRet ;
  }
  
  public static String loadURL(String strURL){
      
      HttpURLConnection urlConnection = null ;
      InputStream urlStream = null ;
      URL url = null ;
      String content = null ;
      byte b[] = new byte[4096];
      
      try { 
            url = new URL(strURL);
            System.out.println("HOST: " + url.getHost()) ;
        } catch (MalformedURLException e) {
            System.out.println("ERROR: invalid URL " + strURL);
            return e.toString() ;
        }
      
      
      try {
		// try opening the URL
		urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestProperty ( "User-agent", "hcc::medical site indexer");
		urlConnection.setAllowUserInteraction(false);

                
                try{
                    urlStream = url.openStream();
                } catch (java.lang.NullPointerException ex) {
                    System.out.println("openStream ERROR: " + ex.toString());
                    return null  ;
                }
            }catch(Exception ex){
                return ex.toString() ;
            }
      
            try{
                StringBuilder sb = new StringBuilder () ;
                int numRead = urlStream.read(b);
                content = new String(b, 0, numRead);
                sb.append(content) ;
                while (numRead != -1) {
                    
                    numRead = urlStream.read(b);
                        if (numRead != -1) {
                            String newContent = new String(b, 0, numRead);
                            sb.append(newContent) ;
                            content += newContent;
                        }
                    }
                urlStream.close();
            }catch(IOException iex){
                return iex.toString() ;
            }
                

         return content ;
  }
    public static String strToUTF8(String strOrg, String strEncodeFrom){
        String strResult = null ;
        try {
                byte[] utf8Bytes = strOrg.getBytes(strEncodeFrom);
                //byte[] defaultBytes = strOrg.getBytes();
                strResult = new String(utf8Bytes, "UTF8");
                
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                strResult = strOrg ;
            }
            return strResult ;
    }  
    
    /*
     * Get the HTML Context of a Substring.
     * Used by highlightHTML to determine the Environment of a given keyword
     * 1 ist OK
     * 0 - nicht highlighten
     */
    public static int getHTMLContext(String strHTML, int iStartPos, int iEndPos){
        int iPos0 = 0 ;
        int iPos1 = 0 ;
        
        String strTmp = null ;
        
        if( (iEndPos - iStartPos) < 3 ){ //kleine Bremse
            return 0 ;
        }
        
        //is it within a TAG? i.e. PMD -> <a href="#PMD">PMD</a> 
        //System.out.println(strHTML.substring(iStartPos, iEndPos)) ;
        iPos0 = strHTML.indexOf(">", iEndPos);
        
        if(iPos0 > 0){ //wenn gefunden, dann darf aber kein < vorher kommen
            iPos1 = strHTML.indexOf("<", iPos0);
            //zwischen > und < darf dann aber kein '<' kommen
            //System.out.println(strHTML.substring(iEndPos, iPos1)) ;
            if( iPos1 > iPos0 &&  strHTML.substring(iEndPos, iPos1).contains("<") == false ){ //wenn die Klammer auf erst nach der Klammer zu kommt - es ist innerhalb eines TAGS <a href="...keyword">                                
                return 0 ;
            }
             //kein else -> weiter   
        }
        //is it within a TAG?
        iPos0 = strHTML.indexOf("</", iEndPos);
        if(iPos0 > 0){
            iPos1 = strHTML.indexOf(">",  iPos0) ;
            if(iPos1 > 0){
                strTmp = strHTML.substring(  iPos0+2,  iPos1) ; 
                //das tag ist SPAN - aber nur, wenn nicht ein offenes span nach dem keyword kommt.
                //System.out.println(strHTML.substring(iStartPos, iPos1)) ;
                if( strTmp.toLowerCase().equals("span") &&  strHTML.substring(iEndPos, iPos0).contains("<span") == false ){ //no nesting span
                    return 0 ;                     
                }
                //do not replace within the file details paragraph (searchResultFileDetails)
                
            }
        }
        
        
        
        return 1 ;
    }
    
    /*
     * This is quite crude.
     * TODO: 
     *    1. do not replace within anchor tags(it would destroy the link)
     *    2. do not replace within span tags
     */
    public static String highlightHTML(String strIn, String strKeyword){
        StringBuilder sbPattern = new StringBuilder() ;
        StringBuilder sbResult = null ;
        int iStart = 0 ;
        int iEnd   = 0 ;
        int iOrigin = 0 ;
        int iCount =  0 ;
        
        sbPattern.append("(") ;
        sbPattern.append(strKeyword) ;
        sbPattern.append(")") ;
        
        Pattern p = Pattern.compile(sbPattern.toString(), Pattern.CASE_INSENSITIVE ) ;
        Matcher m = p.matcher(strIn) ;
        
        
        sbResult = new StringBuilder() ;
        while(m.find()){
            iStart = m.start() ;
            iCount++ ;
            sbResult.append(strIn.substring(iOrigin, iStart)) ; //Übernehmen bis zum Treffer
            
            iEnd   = m.end() ;
            iOrigin = iEnd ; //weiter
            if( 0 == getHTMLContext(strIn, iStart, iEnd) ){
                //sbResult.append("----- no replacement -----") ;
                sbResult.append( strIn.substring(iStart, iEnd)) ; //ohne Änderung übernehmen
            }
            else  //das Keyword zwischen Start und End highlighten...
                sbResult.append(p.matcher(strIn.substring(iStart, iEnd)).replaceAll("<span class=\"highlightText\">$0</span>"));
            //sbResult.append(" n e x t   h i t") ;
        }
        if( 0 == iCount )
            return strIn ; //komplett übernehmen
        
        //when there is content beyond the last hit
        if( iOrigin < strIn.length() ){
            sbResult.append( strIn.substring(iOrigin, strIn.length())) ; 
        }
        return sbResult.toString() ;
        
        //return  strIn.replaceAll(p, "<span class=\"highlightText\">$0</span>") ;
    }
    
    /*
     * highlightQuery should parse a query and make use of the keywords within to highlight each of it.
     */
    public static String highlightQuery(String strIn, String strQuery){
        String[] rStrings = null ;
        String strRet = null ;
        int i = 0 ;
        strQuery = strQuery.replaceAll("[\\*\\+]", "") ;
        rStrings = strQuery.split(" ") ;
        strRet = strIn ;
        for( i = 0; i< rStrings.length;i++){
            strRet = hcc_utils.highlightHTML( strRet, rStrings[i]) ;
        }
        return strRet ; //sb.toString() ;
    }
    
    
}  




