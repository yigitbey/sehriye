package com.android.tencere.activity;


import com.android.tencere.activity.User;

public class Conversation {
	public Boolean is_started;
	public User me;
	public User partner;

	public Boolean nameClicked = false;
    public Boolean ageClicked = false;
    public Boolean sexClicked = false;
    public Boolean locationClicked = false;
    public Boolean numberClicked = false;
    public Boolean mailClicked = false;
	
	public Conversation(){
		this.is_started = false;
		this.me = null;
		this.partner = null;
	}
	
	public Conversation(User me, User partner){
		this.is_started = false;
		this.me = me;
		this.partner = partner;
	}
	
	public void start(){
		this.is_started = true;
	}
	
	public void end(){
		this.is_started = false;
	}
	
}
