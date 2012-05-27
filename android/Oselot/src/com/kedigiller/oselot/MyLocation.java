package com.kedigiller.oselot;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class MyLocation extends Activity {
	/**
	 * Function to get last known location of user
	 @return Double array of latitude and longitude
	 */
	
	public LocationManager locmgr = null; //-- Location Manager 
	
	public MyLocation(){
		this.locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //Initialise the location manager   
	}
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
