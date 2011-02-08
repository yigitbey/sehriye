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
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class Menu extends Activity {
    private ArrayList<String> messages = new ArrayList();
    private Handler mHandler = new Handler();

    private EditText mSendText;
    private ListView mList;
    private XMPPConnection connection;
    public static String server = "buluruzbirsey@appspot.com";
    public String partner;
    public Boolean is_started = false;
    public ProgressDialog dialog;
    public String partnerName;
    public Integer partnerAge;
    public Integer partnerSex;
    public String partnerLocation;
    public String partnerID = "Stranger";
    
    
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

        // Get buttons from Layout
        Button send = (Button) this.findViewById(R.id.send);
        Button end = (Button) this.findViewById(R.id.end);
        Button trade_name = (Button) this.findViewById(R.id.trade_name);
        Button trade_sex = (Button) this.findViewById(R.id.trade_sex);
        Button trade_age = (Button) this.findViewById(R.id.trade_age);
        Button trade_location = (Button) this.findViewById(R.id.trade_location);

        
        
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
        	dialog = ProgressDialog.show(Menu.this, "", "Connecting to server...", true);
        	connection.loginAnonymously();
        	Log.i("XMPPClient", "Logged in as " + connection.getUser());
            // Set the status to available
            Presence presence = new Presence(Presence.Type.available);        	
            connection.sendPacket(presence);

            setConnection(connection);
            
        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to log in as anonymous" );
            Log.e("XMPPClient", ex.toString());
            setConnection(null);
        }
        
        dialog.dismiss();
        //Send a PENDING_CONVERSATION
        sendMessage(server, custom_messages.PENDING_CONVERSATION + ":32:12");
        dialog = ProgressDialog.show(Menu.this, "", "Waiting for a match...", true);
        

        //Buttons

        // Set a listener to send button
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
        
        // Set a listener to end button
        end.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                	sendMessage(server,custom_messages.DELETE_CONVERSATION);                
            }
        });
        //

        // Set a listener to name button
        trade_name.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                	sendMessage(server,custom_messages.TRADE_NAME + ":Ahmet");                
            }
        });
        //
        // Set a listener to age button
        trade_age.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                	sendMessage(server,custom_messages.TRADE_AGE + ":22");                
            }
        });
        //
        // Set a listener to sex button
        trade_sex.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                	sendMessage(server,custom_messages.TRADE_SEX + ":1");                
            }
        });
        //
        // Set a listener to location button
        trade_location.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                	sendMessage(server,custom_messages.TRADE_LOCATION + ":32:12");                
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

                            //Handle Server Messages
                            
                            // START_CONVERSATION
                        	if (command.equals(custom_messages.START_CONVERSATION)){

                                Log.i("XMPPClient", "Gotb " + command + msg.split(":")[1]);
                        		partner = msg.split(":")[1];
                        		is_started = true;
                        		dialog.dismiss();
                                messages.add("---Conversation Started---");
    	                        // Add the incoming message to the list view
    	                        mHandler.post(new Runnable() {
    	                            public void run() {
    	                                setListAdapter();
    	                            }
    	                        });
                        	}
                        	//

                            // TRADE_NAME
                        	if (command.equals(custom_messages.TRADE_NAME)){
                        		String name = msg.split(":")[1];
                                messages.add("Your Partner's name is: " + name);
    	                        // Add the incoming message to the list view
    	                        mHandler.post(new Runnable() {
    	                            public void run() {
    	                                setListAdapter();
    	                            }
    	                        });
                        	}
                        	//
                        	
                            // TRADE_SEX
                        	if (command.equals(custom_messages.TRADE_SEX)){
                        		Integer sex =  Integer.parseInt(msg.split(":")[1]);
                        		if (sex == 1){
                        			messages.add("Your Partner is a man");
                        		}
                        		if (sex == 2){
                        			messages.add("Your Partner is a woman");
                        		}
                        			// Add the incoming message to the list view
    	                        mHandler.post(new Runnable() {
    	                            public void run() {
    	                                setListAdapter();
    	                            }
    	                        });
                        	}
                        	//

                            // TRADE_AGE
                        	if (command.equals(custom_messages.TRADE_AGE)){
                        		Integer age = Integer.parseInt(msg.split(":")[1]);
                        		messages.add("Your Partner is " + age + " years old.");
                        		// Add the incoming message to the list view
    	                        mHandler.post(new Runnable() {
    	                            public void run() {
    	                                setListAdapter();
    	                            }
    	                        });
                        	}
                        	//

                            // TRADE_LOCATION
                        	if (command.equals(custom_messages.TRADE_LOCATION)){
                        		String location = msg.split(":")[1];
                        		messages.add("Your Partner is from" + location);
                        		// Add the incoming message to the list view
    	                        mHandler.post(new Runnable() {
    	                            public void run() {
    	                                setListAdapter();
    	                            }
    	                        });
                        	}
                        	//

                        	
                        	
                        	
                        	
                        }
                        else{ // If this is a regular message
                            messages.add("Stranger: " + message.getBody());
	                        // Add the incoming message to the list view
	                        mHandler.post(new Runnable() {
	                            public void run() {
	                                setListAdapter();
	                            }
	                        });
                        }

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