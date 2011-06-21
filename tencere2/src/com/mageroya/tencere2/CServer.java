package com.mageroya.tencere2;

import com.mageroya.tencere2.JServer;
import com.mageroya.tencere2.Conversation;
import com.mageroya.tencere2.LocalUser;
import com.mageroya.tencere2.RemoteUser;


public class CServer{
    public static final String address = "buluruzbirsey@appspot.com";
    public static final String PENDING_CONVERSATION = "|PENCON";
    public static final String START_CONVERSATION = "|STACON";
    public static final String DELETE_CONVERSATION = "|DELETE_CONVERSATION";
    public static final String TRADE_LOCATION = "|TRALOC";
    public static final String TRADE_NAME = "|TRANAM";
    public static final String TRADE_SEX = "|TRASEX";
    public static final String TRADE_PHONENUMBER = "|TRAPHO";
    public static final String TRADE_EMAIL = "TRAMAI";

    public void requestConversation(LocalUser localUser, JServer jServer){
	
	    RemoteUser remoteUser = new RemoteUser(CServer.address);
		Conversation conversation = new Conversation(localUser, remoteUser,jServer);
	
		String location = localUser.getLocation();
	 	String messageToSend = CServer.PENDING_CONVERSATION + ":" + location;
	
		conversation.sendMessage(remoteUser, messageToSend);
	
		//TODO: Show "Waiting for a match..." dialog
    }


}
