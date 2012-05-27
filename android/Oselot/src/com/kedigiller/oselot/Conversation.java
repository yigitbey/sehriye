package com.kedigiller.oselot;


import android.app.ProgressDialog;


import com.kedigiller.oselot.User;

public class Conversation {
	public Boolean is_started;
	public User me;
	public User partner;
	 //Null Partner
    public User nullPartner;
	public ProgressDialog dialog;
    
	public Boolean nameClicked = false;
    public Boolean ageClicked = false;
    public Boolean sexClicked = false;
    public Boolean locationClicked = false;
    public Boolean numberClicked = false;
    public Boolean mailClicked = false;
	
	public Conversation(){
		this.is_started = false;
		this.me = null;
		this.partner = nullPartner;
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
	
	/**
	* Function to request a conversation
	@return void
	*/
	public void requestConversation(User me, Xmpp xmpp){
	    	
	
		//Send a PENDING_CONVERSATION
	    xmpp.sendMessage(Server.agent, custom_messages.PENDING_CONVERSATION + ":" + this.me.location);
	}
	    
	    


}
