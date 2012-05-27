package com.kedigiller.oselot;

import java.util.List;

import android.app.Application;
import android.app.ProgressDialog;
import android.location.Location;

import android.location.LocationManager;
import android.net.ConnectivityManager;

import android.os.Handler;

import android.telephony.TelephonyManager;




public class Oselot extends Application {
	public Xmpp xmpp; //-- Xmpp Manager  
	public User me; //-- Me as a conversation partner
    public User opponent; //-- My opponent
	public Conversation conversation; //-- Xmpp Conversation
    public LocationManager locmgr = null; //-- Location Manager 
    public TelephonyManager mTelephonyMgr;      //-- Telephony Manager  
    public Phone myPhone;  //-- This Phone
	public ConnectivityManager conMgr;    //-- Connectivity Manager
	
	public ProgressDialog dialog;
	public MainActivity mainActivity;
	public Handler updateHandler;
	
	/**
	 * Function to get last known location of user
	 @return Double array of latitude and longitude
	 */
    public double[] getLocation() {
    	Location l = null;
    	double[] location = new double[2];
   	 	
    	//Get a list of location providers
    	List<String> providers = locmgr.getProviders(true);
    	
    	//Check for any location provider
    	for (int i=providers.size()-1; i>=0; i--) {
    		l = locmgr.getLastKnownLocation(providers.get(i));
    		if (l != null) break;
   	 	}
   	 
    	//If we got a location
   	 	if (l != null) {
   	 		location[0] = l.getLatitude();
   	 		location[1] = l.getLongitude();
   	 	}

   	 	return location;
   	}
    
}



