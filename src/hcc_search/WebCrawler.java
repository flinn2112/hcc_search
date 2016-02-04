/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hcc_search;

import hcc_search.indexer.hcc_index_bean;
import hcc_search.config.IConfigProcessor;
import java.applet.Applet;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.Hashtable ;
import java.nio.ByteBuffer ;

import java.nio.CharBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import searchRT.utils.mime;

public class WebCrawler extends Applet implements ActionListener, Runnable, IConfigProcessor {
    public static final String SEARCH = "Search";
    public static final String STOP = "Stop";
    public static final String DISALLOW = "Disallow:";
    public static final int    SEARCH_LIMIT = 250;
    
    public String m_strIdxDir = "D:\\SharedDownloads\\Projekte\\hcc\\hcc_search\\idxWeb" ;

    Panel          panelMain;
    java.awt.List  listMatches;
    Label          labelStatus;

    // URLs to be searched
    Vector vectorToSearch;
    // URLs already searched
    Vector vectorSearched;
    // URLs which match
    Vector vectorMatches;

    Thread searchThread;

    TextField textURL;
    Choice    choiceType;
 //2112   
    Hashtable ht_mimes ;
    Hashtable ht_sites ;
    
    String m_strConfigPath = null ;
    
    boolean   m_bRemoveWWW = true ; //TODO: read this from config
    
    
    
//2112
    public void init() {
       
        this.ht_mimes = new Hashtable() ;
        this.ht_sites = new Hashtable() ;
        vectorToSearch = new Vector();
	vectorSearched = new Vector();
	vectorMatches = new Vector();
        getConfig() ;
        
	// set up the main UI panel
	panelMain = new Panel();
	panelMain.setLayout(new BorderLayout(5, 5));

	// text entry components
	Panel panelEntry = new Panel();
	panelEntry.setLayout(new BorderLayout(5, 5));

	Panel panelURL = new Panel();
	panelURL.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
	Label labelURL = new Label("Starting URL: ", Label.RIGHT);
	panelURL.add(labelURL);
	textURL = new TextField("", 40);
	panelURL.add(textURL);
	panelEntry.add("North", panelURL);

	Panel panelType = new Panel();
	panelType.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
	Label labelType = new Label("Content type: ", Label.RIGHT);
	panelType.add(labelType);
	choiceType = new Choice();
	choiceType.addItem("text/html");
	choiceType.addItem("audio/basic");
	choiceType.addItem("audio/au");
	choiceType.addItem("audio/aiff");
	choiceType.addItem("audio/wav");
	choiceType.addItem("video/mpeg");
	choiceType.addItem("video/x-avi");
	panelType.add(choiceType);
	panelEntry.add("South", panelType);

	panelMain.add("North", panelEntry);

	// list of result URLs
	Panel panelListButtons = new Panel();
	panelListButtons.setLayout(new BorderLayout(5, 5));

	Panel panelList = new Panel();
	panelList.setLayout(new BorderLayout(5, 5));
	Label labelResults = new Label("Search results");
	panelList.add("North", labelResults);
	Panel panelListCurrent = new Panel();
	panelListCurrent.setLayout(new BorderLayout(5, 5));
	listMatches = new java.awt.List(10);
	panelListCurrent.add("North", listMatches);
	labelStatus = new Label("");
	panelListCurrent.add("South", labelStatus);
	panelList.add("South", panelListCurrent);

	panelListButtons.add("North", panelList);

	// control buttons
	Panel panelButtons = new Panel();
	Button buttonSearch = new Button(SEARCH);
	buttonSearch.addActionListener(this);
	panelButtons.add(buttonSearch);
	Button buttonStop = new Button(STOP);
	buttonStop.addActionListener(this);
	panelButtons.add(buttonStop);

	panelListButtons.add("South", panelButtons);

	panelMain.add("South", panelListButtons);

	add(panelMain);
	setVisible(true);
        textURL.setText("http://hcc-medical.com/");
	repaint(); 

	// set default for URL access
	URLConnection.setDefaultAllowUserInteraction(false);
    }

    public void start() {
    }

    public void stop() {
	if (searchThread != null) {
	    setStatus("stopping...");
	    searchThread = null;
	}
    }

    public void destroy() {
    }

    boolean robotSafe(URL url) {
	String strHost = url.getHost();

	// form URL of the robots.txt file
	String strRobot = "http://" + strHost + "/robots.txt";
        String strURL = null ;
	URL urlRobot;
	try { 
	    urlRobot = new URL(strRobot);
	} catch (MalformedURLException e) {
	    // something weird is happening, so don't trust it
	    return false;
	}

	String strCommands;
	try {
	    InputStream urlRobotStream = urlRobot.openStream();

	    // read in entire file
	    byte b[] = new byte[1000];
	    int numRead = urlRobotStream.read(b);
	    strCommands = new String(b, 0, numRead);
	    while (numRead != -1) {
		if (Thread.currentThread() != searchThread)
		    break;
		numRead = urlRobotStream.read(b);
		if (numRead != -1) {
		    String newCommands = new String(b, 0, numRead);
		    strCommands += newCommands;
		}
	    }
	    urlRobotStream.close();
	} catch (IOException e) {
	    // if there is no robots.txt file, it is OK to search
	    return true;
	}

	// assume that this robots.txt refers to us and 
	// search for "Disallow:" commands.
	strURL = url.getFile();
	int index = 0;
	while ((index = strCommands.indexOf(DISALLOW, index)) != -1) {
	    index += DISALLOW.length();
	    String strPath = strCommands.substring(index);
	    StringTokenizer st = new StringTokenizer(strPath);

	    if (!st.hasMoreTokens())
		break;
	    
	    String strBadPath = st.nextToken();

	    // if the URL starts with a disallowed path, it is not safe
	    if (strURL.indexOf(strBadPath) == 0)
		return false;
	}

	return true;
    }

    public void paint(Graphics g) {
      	//Draw a Rectangle around the applet's display area.
      	g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);

	panelMain.paint(g);
	panelMain.paintComponents(g);
	// update(g);
	// panelMain.update(g);
    }
    
    public static hResult extractFromStream(hcc_ExtractorArgsInputStream args, String strContentType, String strEncoding){
        String strType = null ;
        String strClassName = null ;
        strType = strContentType.replaceFirst(".*/", "") ;  //need only part after slash
        strClassName = "hcc_search." + strType.toUpperCase() + "Extractor" ;
        hResult oResult = null ;
        myClassLoader ccl = new myClassLoader(0) ;
        //hcc_ExtractorArgsInputStream args = new hcc_ExtractorArgsInputStream(is, strEncoding) ;
        
        
        
        //do  we have such a class?
         if( null == ccl.findClass(strClassName) ){
          strClassName = "hcc_search.BINExtractor" ;
         }
         //args.m_is = is ;
         try{                 
                 oResult = (hResult)ccl.invokeClassMethod(strClassName, "processDocument", args) ;
                 if( null != oResult){
                     oResult.m_strDocType = strType ;
                 }
                 //to be implemented ...
                 switch (oResult.m_eRetType){
                     case hResult.DOCUMENT :                       
                         break ;
                     case hResult.CONTENT: 
                     case hResult.UNKNOWN : //a string
                          break ; 
                     default:                         
                          break ;
                 }
                 
            }
            catch(Exception ex){
                Throwable t = ex.getCause() ;
                System.out.println("Failed [" + ex.toString() + "]" ) ;
                return null ;
            }    
         
        return oResult ;
    }
    
    /*
     Implementing interface methode for config reading
     * 
     
     */
    public String processLine(String strLine, String strCfgType){
      
      if(strCfgType.equals("accepted_mimes")){ //the mimetypes that are accepted          
          this.ht_mimes.put(strLine.toLowerCase(), strLine) ;
      }
      if(strCfgType.equals("site")){
          //this.collectFiles(strLine);
          this.ht_sites.put(strLine.toLowerCase(), strLine) ;
      }
      
      if(strCfgType.equals("urls")){
          //this.collectFiles(strLine);
          this.vectorToSearch.add(strLine) ;
      }
     
      return "";      
     }
    
    
    
    private boolean getConfig(){
      
      
        m_strConfigPath = System.getProperty("user.dir") + "/config" ; //try this.
        if( null == m_strConfigPath ){
            return false ;
            
        }

       System.out.println("Config Path was set to [" + m_strConfigPath + "]") ;

       if( false == hcc_utils.checkPath(m_strConfigPath)){
        System.err.println("Path [ " + m_strConfigPath + " ] does not exist - check argument please.") ;
        return  false ;
        }
   
        //this will call processLine 0 to n times
        hcc_utils.processCfgFile(m_strConfigPath + "/mimetypes.cfg", this , "accepted_mimes");
        hcc_utils.processCfgFile(m_strConfigPath + "/sites.cfg", this , "site");
        hcc_utils.processCfgFile(m_strConfigPath + "/urls.cfg", this , "urls");
        return true ;
    }
    
    public String HTMLGetTitle(String strHTML, String strDefaultTitle){
        String strTitle = null ;//use Title to contain full text in the first place
        Pattern pattern = Pattern.compile("<title>.*</title>");
        boolean bFound = false ;
        
        if( null == strHTML){
            strTitle = "no title";
            return strTitle ;
        }
        strTitle = strHTML.toLowerCase() ; 
        
        // In case you would like to ignore case sensitivity you could use this
        // statement
        // Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(strHTML);
        
        bFound = matcher.find() ;
        if( true == bFound )
        {
                System.out.print("Start index: " + matcher.start());
                System.out.print(" End index: " + matcher.end() + " ");
                //!!! Take original here, since we want case sensitive title.
                strTitle = strHTML.substring(matcher.start(), matcher.end()) ;
                strTitle = strTitle.replaceAll("(<.*?>)", "") ;
                System.out.println(strTitle) ;
        }
        else{
            strTitle = strDefaultTitle ;
            
        }
        //strTitle = strHTML.indexOf(strTitle)
        
        return strTitle;
    }
    
    

    public void run() {
        String strTmp = null ;
	String strURL = textURL.getText().toLowerCase();
        StringBuilder sbResult = new StringBuilder() ;
        StringBuilder sbShortText = new StringBuilder() ;
	String strTargetType = choiceType.getSelectedItem();
	Integer numberSearched = 0;
        Integer numberIndexed = 0 ;
	int numberFound = 0;
        int iHttpResponse = 0 ;
        String strType = null ;
        String strFileExt = null ;
        hResult result = null ;
        String strSite = null ;        
        String lowerCaseContent = null ;
        String strLink = null ;
        String strTitle = null ;
        String strDocumentText = null ;
        
        String strEncoding = null ;
        StringTokenizer st = null ;
        HttpURLConnection urlConnection = null ;
        StringBuilder sbURN = null ;
        InputStream urlStream = null ;
        
        
        
        ht_container htc = new ht_container() ;         
               
        HTMLExtractorPostProcess oPPEx = new HTMLExtractorPostProcess() ;
         htc.m_ht = mime.ht_MapExt ;   
        hcc_index_bean idxB = new hcc_index_bean();
               
        //nur test   this.HTMLGetTitle("   <head>       <title>hcc::medical </title>     <meta name=GENERATOR ");
        
	if (strURL.length() == 0) {
	    setStatus("ERROR: must enter a starting URL");
	    return;
	}

	// initialize search data structures
	//reVolt! OS - allow preset of URLs, not removing these vectorToSearch.removeAllElements();
	vectorSearched.removeAllElements();
	vectorMatches.removeAllElements();
	listMatches.removeAll();

//	vectorToSearch.addElement(strURL);

	while ((vectorToSearch.size() > 0) 
	  && (Thread.currentThread() == searchThread)) {
	    // get the first element from the to be searched list
	    strURL = (String) vectorToSearch.elementAt(0);
            
            if( null == strURL ){
                System.err.println("URL is NULL - ...") ;
                continue ;
            }
            
if( numberSearched == 40 ){
    System.out.println("break") ; 
}            
            System.out.println("Processing [" + strURL + "] / Count [" + numberSearched.toString() + "]") ;
	    setStatus("searching " + strURL);
         
	    URL url = null ;
            
            // mark the URL as searched (we want this one way or the other)
	    vectorToSearch.removeElementAt(0);
	    vectorSearched.addElement(strURL);
            
            
            
            numberSearched++;
            
	    try { 
		url = new URL(strURL);
                System.out.println("HOST: " + url.getHost()) ;
                //errors in URL would break here
                strTmp = url.getHost() ;  //i.e. http:://healthcare-components.com/stthomas/stthomas.html
                if( null == strTmp || 0 == strTmp.length()  ){
                    System.err.println("This URL seems to be bogus[" + strURL + "]" ) ;
                    continue ;
                }
	    } catch (MalformedURLException e) {
		System.out.println("ERROR: invalid URL " + strURL);
		continue;
	    }
            
            strSite = url.getHost() ;
            if( null == this.ht_sites.get(strSite.toLowerCase()) ){
                    //do not process
                    System.out.println("Host [" + strSite + "] not included in sites.cfg") ;
                    continue ;
                }

	    

	    // can only search http: protocol URLs
	    if (url.getProtocol().compareTo("http") != 0) 
		continue;

	    // test to make sure it is before searching
	    
            //if (!robotSafe(url))
		//break;

	    try {
		// try opening the URL
		urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestProperty ( "User-agent", "hcc::medical site indexer");
		urlConnection.setAllowUserInteraction(false);
                
               
                
                try{
                    urlStream = url.openStream();
                } catch (java.lang.NullPointerException ex) {
                    System.out.println("openStream ERROR: " + ex.toString());
                    continue ;
                }
               
                
                
                 //strEncoding = hcc_encodings.guessEncoding(urlStream, true) ;
                
          //jsoupDoc = Jsoup.parse(url, 20000) ;
                
                
                
                //System.out.println("Content Type [" + urlConnection.getContentType() + "]") ;
                strType = urlConnection.getContentType() ;
                
                
       /*
          reVolt! OS: there might be no file ext 
       */
                strFileExt = searchRT.utils.mime.get(htc, m_strConfigPath + "/filetypes.cfg" , strType, sbResult);
                
                /*
                if( null == this.ht_mimes.get(strType.toLowerCase()) ){
                    //do not process
                    System.out.println("invalid Content Type [" + urlConnection.getContentType() + "]") ;
                    continue ;
                }
                */
               strType = strType.trim() ;
                strType = strType.replaceFirst(".*/", "") ;  //need only part after slash
                //there might also be some trailing stuff, text/html;charset UTF-8 - this needs to be replaced
       //!!!functioniert nicht!!! keine Ersetzung        
                
                Pattern p = Pattern.compile("[^a-zA-Z].*");
                // get a matcher object
                Matcher m = p.matcher(strType);
                strType = m.replaceAll("");
                //strType = strType.replace("[^a-zA-Z].*", "") ;
                
                
                //use jsoup postprocessing (oPPEx) ;
                hcc_search.hcc_ExtractorArgsInputStream args = new hcc_ExtractorArgsInputStream(urlStream, "ISO-8859-1", oPPEx) ;
                //HTMLExtractor.processDocument(args);
                result = WebCrawler.extractFromStream(args, strType, "ISO-8859-1") ; 
                
               //strEncoding = hcc_encodings.guessEncodingFromString(result.m_oPayLoad.toString(), true) ;
                
		//urlStream.close();
                
                
                System.out.println(urlConnection.getResponseCode()) ;
                iHttpResponse = urlConnection.getResponseCode() ;
                
                
                if(iHttpResponse  != 200){
                    System.err.print("Page [" + strURL + "] resulted in invalid code [" + iHttpResponse + "]") ;
                    continue ;
                }
                
         //http://2112portals.com/KLAU/MasterWorkprint_KLAU_DEV.pdf      
                
               if( null == result ){
                    continue ;
                }
            
                //strEncoding = hcc_encodings.guessEncodingFromString(result.m_oPayLoad.toString(), false) ;
                
                strTitle = HTMLGetTitle(result.m_oPayLoad.toString(), strURL) ;
               /* 
                
                
                if( true == this.m_bRemoveWWW ){
                   strURL = strURL.replace("www.", "") ;
                }
                
                
                if( null == strEncoding ){
                    strEncoding = "ISO-8859-1" ;
                }
                */
                strDocumentText = "" ;
                //
                
                //if( null != result.m_strText){
                    
 
   
                    //jsoupDoc = Jsoup.parse(result.m_strText) ;
                   if( result.m_strText != null){
                       strDocumentText = result.m_strText ;
                   }
                   else{
                       strDocumentText = result.m_oPayLoad.toString() ;
                   }
                   
                   
                   
                   sbShortText = new StringBuilder() ;
                   if( strType.compareTo("html") == 0){
                       strDocumentText = strDocumentText.replaceAll("&#0;", ".") ; //  strDocumentText.substring(0, 256) ;
                       
                       byte[] b = strDocumentText.getBytes() ;
                       byte[] b2 = hcc_utils.byteReplace(b) ;  //replace Schmutz(0x0)
                       ByteBuffer bf = ByteBuffer.wrap(b2);
                       strDocumentText = new String(b2, 0, b2.length) ;  //re-fill
                       sbShortText.append("...") ;
                       //this.showBufferData(bf, "TEST");
                       
                       if( b2.length > 512 ) {                           
                           //strShortText = strShortText.substring(256) ;
                           sbShortText.append(new String(b2, 256, 256));                          
                           //strShortText = new String(b, 0, 256); //strDocumentText.substring( 256) ;
                       }
                       else if( b2.length > 127 && b2.length <= 256 ){
                           sbShortText.append(new String(b2, 128, b2.length - 128 - 1));
                       }
                       else{
                           //strShortText = new String(b, 0, strDocumentText.length());
                           sbShortText.append(strDocumentText) ;//strShortText = new String(b2, 0, b2.length) ;
                       }
                       sbShortText.append("...") ;                       
                   }
                   
                //}       
      //strShortText = strShortText.replaceAll(sbPattern.toString(), "") ;
      String strPath = url.getPath() ;
      numberIndexed++ ;
      System.out.println("#" + numberIndexed.toString() + " indexing " + strURL ) ;     
      sbURN = new StringBuilder() ;
      strTmp = url.getHost().replaceFirst("www.", "") ;
      sbURN.append(strTmp) ;
   
        sbURN.append(url.getFile()) ;
      
                idxB.index( 
                           m_strIdxDir,
                           strSite,
                           strTitle,
                           sbShortText.toString(),  //for shorttext
                           null,   //Memopath
                           sbURN.toString(),  //URN
                           hcc_utils.now(),
                           result.m_strDocType,
                           "web",
                           null,
                           strURL,                           
                           null, //strExpires,
                           strDocumentText,
                           null  //no extended Attributes
                        );
                 
                
                if( strType.compareTo("html") != 0){ //is no html -> do not search for links
                 continue ;  
                }
                
                
                
                //System.out.println(result.m_oPayLoad) ;
                
                
                
                
		if (Thread.currentThread() != searchThread)
		    break;
                if( null == result.m_oPayLoad){ //text would be the raw html
                    continue ;
                    
                }
		lowerCaseContent = result.m_oPayLoad.toString().toLowerCase();

		int index = 0;
                //picking up all links from this page
		while ((index = lowerCaseContent.indexOf("<a", index)) != -1)
		{
		    if ((index = lowerCaseContent.indexOf("href", index)) == -1) 
			break;
		    if ((index = lowerCaseContent.indexOf("=", index)) == -1) 
			break;
		    
		    if (Thread.currentThread() != searchThread)
			break;

		    index++;
		    String remaining = result.m_oPayLoad.toString().substring(index);

		    st 
		      = new StringTokenizer(remaining, "\t\n\r\">#");
		    strLink = st.nextToken();

		    URL urlLink;
		    try {
			urlLink = new URL(url, strLink);
			strLink = urlLink.toString();
		    } catch (MalformedURLException e) {
			setStatus("ERROR: bad URL " + strLink);
			continue;
		    }

		    // only look at http links
		    if (urlLink.getProtocol().compareTo("http") != 0)
			continue;

		    if (Thread.currentThread() != searchThread)
			break;

		   
     
                        // check to see if this URL has already been 
			    // searched or is going to be searched
			    if ( strLink != null && (!vectorSearched.contains(strLink)) 
			      && (!vectorToSearch.contains(strLink))) {

				// test to make sure it is robot-safe!
				//if (robotSafe(urlLink))
				    vectorToSearch.addElement(strLink);
			    }
			
			    if (vectorMatches.contains(strLink) == false) {
				listMatches.add(strLink);
				vectorMatches.addElement(strLink);
				numberFound++;
				if (numberFound >= SEARCH_LIMIT)
				    break;
			    }
		} //while
	    } catch (IOException e) {
		setStatus("ERROR: couldn't open URL " + strURL);
		continue ;
	    }
             //outer try block

	    
	    if (numberSearched >= SEARCH_LIMIT)
		break;
	}//outer while

	if (numberSearched >= SEARCH_LIMIT || numberFound >= SEARCH_LIMIT)
	    setStatus("reached search limit of " + SEARCH_LIMIT);
	else
	    setStatus("done searched[" + numberSearched.toString() + "]");
	searchThread = null;
	// searchThread.stop();
    }

    void setStatus(String status) {
	labelStatus.setText(status);
    }

    public void actionPerformed(ActionEvent event) {
	String command = event.getActionCommand();

	if (command.compareTo(SEARCH) == 0) {
	    setStatus("searching...");

	    // launch a thread to do the search
	    if (searchThread == null) {
		searchThread = new Thread(this);
	    }
	    searchThread.start();
	}
	else if (command.compareTo(STOP) == 0) {
	    stop();
	}
    }
        public static void main (String argv[]){
                Frame f = new Frame("WebFrame");
                WebCrawler applet = new WebCrawler();
		f.add("Center", applet);
/*		Behind a firewall set your proxy and port here!
*/
                Properties props= new Properties(System.getProperties());
                props.put("http.proxySet", "true");
        	props.put("http.proxyHost", "webcache-cup");
        	props.put("http.proxyPort", "8080");

                Properties newprops = new Properties(props);
                System.setProperties(newprops);
/**/

		
                applet.init();
                applet.start();
                f.pack();
                f.show();
        }
        
        
 void showBufferData(
          ByteBuffer buf, String name){
    System.out.println(
            "Buffer data for " + name);
    int cnt = 0;
    while(buf.hasRemaining()){
      System.out.print(
                      buf.get() + " ");
      cnt++;
      if(cnt%12 == 0)
            System.out.println();//line
    }//end while loop
    System.out.println();//blank line
  }//end showBufferData

}


class html_utils{    
    public static String getShortText(String strHTML){
        int iStart = 0 ;
        String strPart = null ;
        iStart = strHTML.indexOf("<body") ;
        if( iStart < 0 ){
            return strHTML ;
        }
        
        strPart = strHTML.substring(iStart) ;
        
        strPart = strPart.replaceAll("\\n*", "");
        return strPart.replaceAll("<.*?>", "");
    }

   

}