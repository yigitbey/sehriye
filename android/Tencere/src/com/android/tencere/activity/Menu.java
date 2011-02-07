package com.android.tencere.activity;

import java.util.ArrayList;

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

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.tencere.activity.custom_messages;

public class Menu extends Activity {
    private ArrayList<String> messages = new ArrayList();
    private Handler mHandler = new Handler();
    private EditText mRecipient;
    private EditText mSendText;
    private ListView mList;
    private XMPPConnection connection;
    public static String server = "buluruzbirsey@appspot.com";
    public String partner;
    public Boolean is_started = false;
    
    
    // Function to send a message
    public void sendMessage(String to, String text){
        Log.i("XMPPClient", "Sending text [" + text + "] to [" + to +"]");
        Message msg = new Message(to, Message.Type.chat);
        msg.setBody(text);
        connection.sendPacket(msg);
    }
    
    
    // Called with the activity is first created.
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.i("XMPPClient", "onCreate called");
        setContentView(R.layout.main);

        
        mSendText = (EditText) this.findViewById(R.id.sendText);
        Log.i("XMPPClient", "mSendText = " + mSendText);
        mList = (ListView) this.findViewById(R.id.listMessages);
        Log.i("XMPPClient", "mList = " + mList);
        setListAdapter();        
        
        
        
        String host = "mageroya.com";
        String port = "5222";
        String service = "mageroya.com";
  
        // Create a connection
        ConnectionConfiguration connConfig = new ConnectionConfiguration(host, Integer.parseInt(port), service);
        final XMPPConnection connection = new XMPPConnection(connConfig);

        try {
            connection.connect();
            Log.i("XMPPClient", "[SettingsDialog] Connected to " + connection.getHost());
        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to connect to " + connection.getHost());
            Log.e("XMPPClient", ex.toString());
            setConnection(null);
        }
        try {
        	connection.loginAnonymously();
        	Log.i("XMPPClient", "Logged in as " + connection.getUser());

            // Set the status to available
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
            setConnection(connection);
            
            //Send a PENDING_CONVERSATION
            sendMessage(server, custom_messages.PENDING_CONVERSATION + ":32:12");
            
        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to log in as anonymous" );
            Log.e("XMPPClient", ex.toString());
            setConnection(null);
        }
        
        

        // Set a listener to send a chat text message
        Button send = (Button) this.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                String text = mSendText.getText().toString();
                
                if (is_started){
                	sendMessage(partner,text);
                	messages.add("You: " + text);
                    setListAdapter();
                }
                else{
                	sendMessage(server,text);
                }
                
            }
            
        });
        //
    }
    
    
    /**
     * Called by Settings dialog when a connection is established with the XMPP server
     * @param connection
     */
    public void setConnection (final XMPPConnection connection) {
        this.connection = connection;
        if (connection != null) {
            // Add a packet listener to get messages sent to us
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        Log.i("XMPPClient", "Got text [" + message.getBody() + "] from [" + fromName +"]");
                        
                        if (fromName.equals(server)){ //If this message is from conversation server
                        	String msg = message.getBody();
                        	String command = msg.split(":")[0];	
                            Log.i("XMPPClient", "Got " + command + msg.split(":")[1]);

                            // START_CONVERSATION
                        	if (command.equals(custom_messages.START_CONVERSATION)){

                                Log.i("XMPPClient", "Gotb " + command + msg.split(":")[1]);
                        		partner = msg.split(":")[1];
                        		is_started = true;
                                messages.add("---Conversation Started---");
    	                        // Add the incoming message to the list view
    	                        mHandler.post(new Runnable() {
    	                            public void run() {
    	                                setListAdapter();
    	                            }
    	                        });
                        	}
                        	                    	
                        	
                        }
                        else{
                            messages.add("Stranger: " + message.getBody());
	                        // Add the incoming message to the list view
	                        mHandler.post(new Runnable() {
	                            public void run() {
	                                setListAdapter();
	                            }
	                        });
                        }

                        Log.i("XMPPClient", "partner [" + partner + "] is_started [" + is_started + "]");
                    }
                }
            }, filter);
        }
    }

    
    
    private void setListAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.multi_line_list_item, messages);
        mList.setAdapter(adapter);
    }


    
}