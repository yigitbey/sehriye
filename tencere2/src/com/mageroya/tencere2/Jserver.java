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

public class JServer{
    public static String host = "mageroya.com";
    public static int port = "5222";
    public static String service = "mageroya.com";
    public XMPPConnection connection;


    public Jserver(){
	ConnectionConfiguration connConfig = new ConnectionConfiguration(this.host, this.port, this.service);
	final XMPPConnection connection = new XMPPConnection(connConfig);
	
	//Try connecting to server
	//On exception log it, and set connection as null.
	try{ 
	    connection.connect();
	    
	    //Log the connection
	    Log.i("Tencere","Connected to" + connection.getHost());
	
	}catch(XMppException ex){
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

    //TODO: Implement setConnection function
    

}
