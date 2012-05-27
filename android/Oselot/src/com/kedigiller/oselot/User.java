package com.kedigiller.oselot;


public class User {
	public String address;
	public String name;
	public String age;
	public String sex;
	public String location;
	public String number;
	public String mail;

	public double latitude = 0;
	public double longitude = 0;
	
	public Oselot app;
	
	public User(){
		super();
	}

	public User(String address){
		super();
		this.address = address;

	}
	
	public User(String name,String location){
		super();
		this.location = location;
		this.address = null;
		this.name = name;
	}
	
	public User(String name,String age,String sex,String location, String number, String mail){
		this.name = name;
		this.age = age;
		this.sex = sex;
		this.location = location;
		this.number = number;
		this.mail = mail;
		this.address = null;

	}
	
	/**
	 * Method to set JID of user
	 @param address JID of user
	 */
	public void setAdress(String address){
		this.address = address;
	}
    
	


}
