package com.mageroya.tencere2;

public abstract class User {
	
	public String name;
    public String age;
    public String sex;
    public String location;
    public String phoneNumber;
    public String email;
    
    abstract String getName();
    abstract String getAge();
    abstract String getSex();
    abstract double[] getLocation();
    abstract String getPhoneNumber();
    abstract String getEmail();
    
}
    