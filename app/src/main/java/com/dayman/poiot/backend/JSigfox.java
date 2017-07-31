package com.dayman.poiot.backend;

/**
 *
 * @author Benjamin Young
 * 
 * Email: b.p.young1234@gmail.com
 */


import android.util.Base64;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class JSigfox {
    private String authEncodedString;
    
    public JSigfox(String login, String password){
        String auth = login + ":" + password;

        byte[] authBytes = Base64.encode(auth.getBytes(), Base64.DEFAULT);

        authEncodedString = new String(authBytes);       
    }
    
    public String GET(String urlEx) throws Exception {
        String url = "https://backend.sigfox.com/api/";
        String newUrl = url + urlEx;

        URL Url = new URL(newUrl);
        URLConnection conn = Url.openConnection();
        conn.setRequestProperty("Authorization", "Basic " + authEncodedString);

        InputStream is = conn.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);

        int numCharsRead;
        char[] charArray = new char[1024];

        StringBuilder sb = new StringBuilder();

        while ((numCharsRead = isr.read(charArray)) > 0) {
            sb.append(charArray, 0, numCharsRead);
        }

        return sb.toString();
    }
}
