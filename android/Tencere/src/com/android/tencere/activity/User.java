package com.android.tencere.activity;

public class User {
	public String address;
	public String name;
	public String age;
	public String sex;
	public String location;
	public String number;
	public String mail;
	
	public User(){
		super();
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
	
	public void setAdress(String address){
		this.address = address;
	}
}
