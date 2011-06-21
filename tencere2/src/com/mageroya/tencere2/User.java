package com.mageroya.tencere2;

public abstract class User {
	
    public String name;
    public String age;
    public String sex;
    public String location;
    public String phoneNumber;
    public String email;
    public String JID;

    abstract String getName();
    abstract String getAge();
    abstract String getSex();
    abstract String getLocation();
    abstract String getPhoneNumber();
    abstract String getEmail();
    

    public User(String JID) {	
    	this.JID = JID;
    }
   
    
}
    
