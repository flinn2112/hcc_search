/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

*/

    

package hccAI;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import static org.apache.http.HttpHeaders.USER_AGENT;


/**
 *
 * @author frank
 */
public class clsPostAI {
    private static final String POST_PARAMS = "userName=doodle&password=cockAdoodledoo";
    private static final String USER_AGENT = "hcc_search.jar";
    private String strProto; 
    private String strAddr; 
    private int iPort; 
    private String strURI;
    private String m_strResponse ;
    
    public clsPostAI(String strProto, String strAddr, int iPort, String strURI){
        this.strProto = strProto ;
        this.strAddr = strAddr ;
        this.iPort = iPort ;
        this.strURI = strURI;
    }
    
    public boolean post(String strURL, String strJSON){
        HttpURLConnection httpURLConnection = null ;
        int responseCode = 404 ;
        StringBuffer response ;
        try{
            URL obj = new URL(strURL);
            httpURLConnection = (HttpURLConnection) obj.openConnection();
            httpURLConnection.setRequestMethod("POST");
        }catch(Exception ex){
            return false ;
        }
        httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);

        // For POST only - START
        httpURLConnection.setDoOutput(true);
        try{
            OutputStream os = httpURLConnection.getOutputStream();
            //os.write(POST_PARAMS.getBytes());
            os.write(strJSON.getBytes());
            os.flush();
            os.close();
        // For POST only - END

            responseCode = httpURLConnection.getResponseCode();
        }catch(Exception ex){
            return false ;
        }
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            try{
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inputLine;
                response = new StringBuffer();

                while ((inputLine = in .readLine()) != null) {
                    response.append(inputLine);
                } in .close();
        }catch(Exception ex){
            return false ;
        }
            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("POST request not worked");
        }
        return true ;
    }
    
}
