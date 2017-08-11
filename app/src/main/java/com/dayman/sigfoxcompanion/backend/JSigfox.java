package com.dayman.sigfoxcompanion.backend;

/**
 *
 * @author Benjamin Young
 * 
 * Email: b.p.young1234@gmail.com
 */


import android.util.Base64;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class JSigfox {
    private String authEncodedString;
    
    public JSigfox(String login, String password){
        String auth = login + ":" + password;

        authEncodedString = new String(Base64.encode(auth.getBytes(), Base64.DEFAULT));
    }
    
    public String GET(String urlEx) throws Exception {
        String newUrl = "https://backend.sigfox.com/api/" + urlEx;

        URL Url = new URL(newUrl);
        URLConnection conn = Url.openConnection();
        conn.setRequestProperty("Authorization", "Basic " + authEncodedString);

        InputStreamReader isr = new InputStreamReader(conn.getInputStream());

        int numCharsRead;
        char[] charArray = new char[1024];

        StringBuilder sb = new StringBuilder();

        while ((numCharsRead = isr.read(charArray)) > 0) {
            sb.append(charArray, 0, numCharsRead);
        }

        return sb.toString();
    }
}
