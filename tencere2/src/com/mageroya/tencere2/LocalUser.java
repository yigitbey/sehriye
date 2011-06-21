package com.mageroya.tencere2;

import android.content.SharedPreferences;

public class LocalUser extends User {
	
	public LocalUser(String JID, SharedPreferences settings) {
		super(JID);
        this.name = settings.getString("name", "myNotSetDefaultName");
    	this.age = settings.getString("age", "myNotSetDefaultAge");
    	
	}
        
	
	public String getName() {
		
	}
	
	public String getAge() {
		

}
