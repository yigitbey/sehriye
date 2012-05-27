package com.kedigiller.oselot;


import android.util.Log;

import java.net.*;
import java.io.*;
public class Road {
    
	public static String getClosestRoad(double lat, double longi) throws Exception{
		String url = "http://izabetapp.appspot.com/?lat=" + lat + "&longi=" + longi;
		String result = readUrl(url);
		Log.i("resurl",url);
		Log.i("res",result);
		return result;
	}

        public static String readUrl(String url) throws Exception {

            URL urlStream = new URL(url);
            String result = "";
            BufferedReader in = new BufferedReader(
            new InputStreamReader(urlStream.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
               result = result + inputLine;
            in.close();
            return result;
        }
    
}