package com.kedigiller.oselot;


import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.Log;


public class Phone{
	public String number;
	
	TelephonyManager mTelephonyMgr;
	ConnectivityManager conMgr;

	   
	public Phone(TelephonyManager mTelephonyMgr, ConnectivityManager conMgr){
		this.mTelephonyMgr = mTelephonyMgr;
		this.conMgr = conMgr;
		this.number = getMyPhoneNumber();
		
	}	
	public String getMyPhoneNumber(){
	    return this.mTelephonyMgr.getLine1Number();
	}

	public boolean checkInternetConnection() {

		if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			Log.i("Isabet", "Internet Connection Not Present");
			return false;
		}

	} 

}
	