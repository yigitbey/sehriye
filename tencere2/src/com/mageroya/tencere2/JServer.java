package com.mageroya.tencere2;


import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.util.Log;

public class JServer {
    public static String host = "mageroya.com";
    public static int port = 5222;
    public static String service = "mageroya.com";
    public XMPPConnection connection;


    public JServer() {
    	ConnectionConfiguration connConfig = new ConnectionConfiguration(JServer.host, JServer.port, JServer.service);
    	final XMPPConnection connection = new XMPPConnection(connConfig);
	
		//Try connecting to server
		//On exception log it, and set connection as null.
		try{ 
		    connection.connect();
		    
		    //Log the connection
		    Log.i("Tencere","Connected to" + connection.getHost());
		
		}catch(XMPPException ex){
		    //Log the exception
	        Log.e("XMPPClient", "[SettingsDialog] Failed to connect to " + connection.getHost());
	        Log.e("XMPPClient", ex.toString());
	
		    //Set connection as null
	        setConnection(null);
        } 
		//End of trying
	
		//Trying to login anonymously
		//On exception log it, and set connection as null.
        try{ 
		    connection.loginAnonymously();
		    
		    //Log login info
		    Log.i("XMPPClient", "Logged in as " + connection.getUser());
            // Set the status to available
            Presence presence = new Presence(Presence.Type.available);        
            connection.sendPacket(presence);

            //Set connection as this.connection
            setConnection(connection);

        }catch (XMPPException ex){
        	//Log the exception
        	Log.e("XMPPClient", "[SettingsDialog] Failed to log in as anonymous" );
        	Log.e("XMPPClient", ex.toString());

        	//Set connection as null
            setConnection(null);
        }

    }


    //setConnection
    public void setConnection (final XMPPConnection connection) {
        this.connection = connection;
        if (connection != null) {
            // Add a packet listener to get messages sent to us
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat); // Set a filter for only chat type XMPP packets
            connection.addPacketListener(new PacketListener() { //Set a new Packet Listener on this connection with this filter
		    public void processPacket(Packet packet) {
			Message message = (Message) packet; //Cast received package to a XMPP Message
			if (message.getBody() != null) {
			    conversation.handleIncomingMessage(message); //Handle the message
			}
		    }
		}, filter);
        }


    }// End of setConnection




    

}
