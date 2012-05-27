package com.kedigiller.oselot;


import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;


import android.os.Bundle;
import android.util.Log;

public class Xmpp{
    //-- XMPP Connection
    public XMPPConnection connection = null;    
    
    //-- Conversation
    public Conversation conversation;

    //-- This Phone
    public Phone myPhone;
      
    public User me;
	public User partner;
	 //Null Partner
    public User nullPartner;

    
    public Xmpp(){
    	super();
    	this.me = new User();
    	this.partner = new User();
    	this.conversation = new Conversation(me,partner);
    }

   
    
    /**
 	* Sends a message to a xmpp jid            
	@param  to  Jid to send message                 
	@param  text Message to send                        
 	*/
    public void sendMessage(String to, String text){
        Log.i("Tencere", "Sending text [" + text + "] to [" + to +"]");
        Message msg = new Message(to, Message.Type.chat);
        msg.setBody(text);
        connection.sendPacket(msg);
    }
    

    
    /**
     * Connects to Jabber server and logs in anonymously.
     * <br>
     * Server must accept SASL Anonymous logins. 
     * Return values:
     * 0: Success
     * 1: Unable to connect
     * 2: Unable to login
     */
    public int connectServer(){
    	 
    	
    	// Connection Settings
        String host = Server.host;
        String port = Server.port;
        String service = Server.service;
  
        // Create a connection
        ConnectionConfiguration connConfig = new ConnectionConfiguration(host, Integer.parseInt(port), service);
        final XMPPConnection connection = new XMPPConnection(connConfig);

        //Try connecting the xmpp server
        try {
            connection.connect();
            Log.i("Tencere", "[SettingsDialog] Connected to " + connection.getHost());
        }
        //If connection was not established, log it and show an error to user
        catch (XMPPException ex) {
            Log.e("Tencere", "[SettingsDialog] Failed to connect to " + connection.getHost());
            Log.e("Tencere", ex.toString());
            setConnection(null);
            //Show error
            return 1;
        }
        
        //Try logging in as anonymous
        try {
        	connection.loginAnonymously();
        	Log.i("Tencere", "Logged in");
            // Set the status to available
            Presence presence = new Presence(Presence.Type.available);        	
            connection.sendPacket(presence);
            setConnection(connection); 
            //Clear the connection pending dialog
            return 0;     
        }
        //If logging in was not possible, log it and show an error to user
        catch (XMPPException ex) {
        	Log.e("Tencere", "[SettingsDialog] Failed to log in as anonymous" );
        	Log.e("Tencere", ex.toString());
            setConnection(null);
            //Show error
            return 2;
        }
        

    }
    
    public void addMessage(String msg){
    	
    	Bundle mesajBundle = new Bundle();
    	mesajBundle.putString("mesaj", msg);
    	
    	android.os.Message textReceived = new android.os.Message();
    	textReceived.setData(mesajBundle);
    	
    	MainActivity.messageHandler.sendMessage(textReceived);
    	
    	
    	
    }
    
    
    
    /**
    * Function to handle server messages<br>
    * If command is START_CONVERSATION, set partner's address and set conversation.is_started to true. Also remove the pending dialog box.<br>
    * If command is DELETE_CONVERSATION, set conversation.is_started to false<br>
    @param msg Received xmpp message
    @return void
    */
    public void handleCustomMessage(String msg){
    	String command = msg.split(":")[0];	
    	
    	//Trade Message
    	Bundle tradeBundle = new Bundle();
    	tradeBundle.putString("mesaj",msg);
    	
    	android.os.Message tradeMessage = new android.os.Message();
    	
    	
        // START_CONVERSATION
    	if (command.equals(custom_messages.START_CONVERSATION)){

    		String address = msg.split(":")[1];  		
    		try{
    			this.partner.address = address;
    			Partner.address = address;
    		}
    		catch(Exception ex){
    			Log.i("Isabet", ex.toString());
    		}
    		
    		android.os.Message conversation_update = new android.os.Message();
    		conversation_update.arg1 = 1;
    		
    		Bundle conversationBundle = new Bundle();
    		conversationBundle.putString("partner", this.partner.address);
    		conversation_update.setData(conversationBundle);
    		
    		MainActivity.conversationHandler.sendMessage(conversation_update);
    		
    		conversation.is_started = true;
    		
    		MainActivity.clearDialog(); //Dismiss "waiting for a match dialog"

    	}
    	//

        // DELETE_CONVERSATION
    	if (command.equals(custom_messages.DELETE_CONVERSATION)){
    		this.partner = nullPartner;
    		android.os.Message conversation_update = new android.os.Message();
    		conversation_update.arg1 = 0;

    		
    		MainActivity.conversationHandler.sendMessage(conversation_update);

    	}
    	//


		// TRADE_NAME
		if (command.equals(custom_messages.TRADE_NAME)) {

			tradeBundle.putString("type", "name");
			tradeMessage.setData(tradeBundle);
			
			MainActivity.tradeHandler.sendMessage(tradeMessage);

		}
		//

		// TRADE_SEX
		if (command.equals(custom_messages.TRADE_SEX)) {

			tradeBundle.putString("mesaj", msg);
			tradeBundle.putString("type", "sex");
			tradeMessage.setData(tradeBundle);
			
			MainActivity.tradeHandler.sendMessage(tradeMessage);

		}
		//

		// TRADE_AGE
		if (command.equals(custom_messages.TRADE_AGE)) {

			tradeBundle.putString("type", "age");
			tradeMessage.setData(tradeBundle);
			
			MainActivity.tradeHandler.sendMessage(tradeMessage);

		}
		//

		// TRADE_LOCATION
		if (command.equals(custom_messages.TRADE_LOCATION)) {

			tradeBundle.putString("type", "location");
			tradeMessage.setData(tradeBundle);
			
			MainActivity.tradeHandler.sendMessage(tradeMessage);


		}
		//

		// TRADE_NUMBER
		if (command.equals(custom_messages.TRADE_NUMBER)) {

			tradeBundle.putString("type", "number");
			tradeMessage.setData(tradeBundle);
			
			MainActivity.tradeHandler.sendMessage(tradeMessage);
			
			


		}
		//

		// TRADE_MAIL
		if (command.equals(custom_messages.TRADE_MAIL)) {
			tradeBundle.putString("type", "mail");
			tradeMessage.setData(tradeBundle);
			
			MainActivity.tradeHandler.sendMessage(tradeMessage);


		}
		//
    	

    }
    
    /**
     * Called when a connection is established with the XMPP server
     * @param connection
     */
    public void setConnection (final XMPPConnection connection) {
        this.connection = connection;
        Log.e("tencere","packetgeldi");
        if (connection != null) {
            // Add a packet listener to get messages sent to us
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            //Filter only chat messages
            connection.addPacketListener(new PacketListener() {
            	
            	//On a chat message received
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    
                    //If the message is not empty
                    if (message.getBody() != null) {
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        String msg = message.getBody();
                        Log.i("Oselot Partner",msg);
                        //If this message is from conversation server
                        if (fromName.equals(Server.agent)){ 
                        	handleCustomMessage(msg); //Handle it with this function
                        	Log.i("Oselot", "Got text [" + msg + "] from agent");
                        }
                        // If this is a regular message
                        else{ 
                        	Log.i("Oselot", "Got text [" + msg + "] from partner");
                        	addMessage(msg);
    
                        	Log.i("Oselot", "Got text [" + msg + "] from partner");
                        }
                    }
                    
                    
                }
            }, filter);
        }
    }


    
}
