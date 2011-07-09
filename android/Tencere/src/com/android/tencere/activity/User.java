package com.android.tencere.activity;

import java.util.List;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class User {
	public String address;
	public String name;
	public String age;
	public String sex;
	public String location;
	public String number;
	public String mail;
	
	
	public LocationManager lm;
	public LocationListener onLocationChange = new LocationListener() {
		
		/**
		 * Sets and displays the lat/long when a location is provided
		 */
		public void onLocationChanged(Location loc) {
            
            String latlong = loc.getLatitude() + ":" + loc.getLongitude();   
            location = latlong;
        }
        
        // Methods below are required for interface, not used
        public void onProviderDisabled(String provider) {
        }
        public void onProviderEnabled(String provider) {
        }
        public void onStatusChanged(String provider, int status, Bundle extras) { 
        }
        
    };
    
    
	public User(){
		super();
	}
	
	public User(String name,String age,String sex,String location, String number, String mail, LocationManager lm){
		this.name = name;
		this.age = age;
		this.sex = sex;
		this.location = location;
		this.number = number;
		this.mail = mail;
		this.address = null;
		this.lm = lm;
	}
	
	
	/**
	 * Method to set JID of user
	 @param address JID of user
	 */
	public void setAdress(String address){
		this.address = address;
	}
    
	
	/**
	 * Function to get last known location of user
	 @return Double array of latitude and longitude
	 */
    public double[] getLocation() {
    	Location l = null;
    	double[] location = new double[2];
   	 	
    	//Get a list of location providers
    	List<String> providers = lm.getProviders(true);
    	
    	//Check for any location provider
    	for (int i=providers.size()-1; i>=0; i--) {
    		l = lm.getLastKnownLocation(providers.get(i));
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
