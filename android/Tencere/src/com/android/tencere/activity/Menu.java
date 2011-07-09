package com.android.tencere.activity;

import java.util.ArrayList;
import java.util.List;


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


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.tencere.activity.User;
import com.android.tencere.activity.Server;
import com.android.tencere.activity.Conversation;

/**
* Main class
*/ 
public class Menu extends Activity {

	//
	// CLASS VARIABLES
	//
    
    //-- XMPP Connection
    private XMPPConnection connection;    
    //--
    
    //-- Creating a conversation
    public User me = new User();
    public User partner = new User("Stranger",null,null,null,null,null);
    public Conversation conversation = new Conversation(me,partner);
    //--

	//-- UI elements
    private ArrayList<String> messages = new ArrayList(); 
    private Handler mHandler = new Handler();
    private EditText mSendText;
    private ListView mList;
    public ProgressDialog dialog;
    public Button end;
    public Button newConversation;
	//--
    
    //-- Location Manager 
    public LocationManager locmgr = null;
    LocationListener onLocationChange=new LocationListener() {
        public void onLocationChanged(Location loc) {
            //sets and displays the lat/long when a location is provided
            String latlong = loc.getLatitude() + ":" + loc.getLongitude();   
            conversation.me.location = latlong;
        }
        public void onProviderDisabled(String provider) {
        // required for interface, not used
        }
        public void onProviderEnabled(String provider) {
        // required for interface, not used
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
        // required for interface, not used
        }
    };
    // --
    
    
    //
    // END OF CLASS VARIABLES
    //
    
    
    //
    // CLASS METHODS START HERE
    //

    /**
 	* Sends a message to a xmpp jid            
	@param  to  Jisd to send message                 
	@param  text Message to send                        
 	*/
    public void sendMessage(String to, String text){
        Log.e("XMPPClient", "Sending text [" + text + "] to [" + to +"]");
        Message msg = new Message(to, Message.Type.chat);
        msg.setBody(text);
        connection.sendPacket(msg);
    }
    
      
    /**
    * Function to request a conversation
    @return void
    */
    public void requestConversation(){
    	//Get location
    	double[] location = getGPS();
    	
    	//Serialize the location	
    	conversation.me.location = Double.toString(location[0]) + ":" + Double.toString(location[1]);
        
    	//Send a PENDING_CONVERSATION
        sendMessage(Server.agent, custom_messages.PENDING_CONVERSATION + ":" + conversation.me.location);
        
        //Show a dialog box indicating the pending status
        dialog = ProgressDialog.show(Menu.this, "", "Waiting for a match...", true);
    }
    
    
    /**
    * Function to handle server messages
    * <p>
    * If command is START_CONVERSATION, set partner's address and set conversation.is_started to true. Also remove the pending dialog box.
    * <p>
    * If command is DELETE_CONVERSATION, set conversation.is_started to false
    * <p>
    * If command is TRADE_*, set partner's info and print it on the screen
    *  
    @param msg Received xmpp message
    @return void
    */
    public void handleCustomMessage(String msg){
    	String command = msg.split(":")[0];	

    	Log.i("Tencere",msg);
        // START_CONVERSATION
    	if (command.equals(custom_messages.START_CONVERSATION)){
    		
    		conversation.partner.setAdress(msg.split(":")[1]);
    		conversation.is_started = true;
    		dialog.dismiss();
    		
            messages.add("---Conversation Started---");
      
           // end.setVisibility(View.VISIBLE); //end is visible
           // newConversation.setVisibility(View.GONE); //new is invisible
            
            updateMessages();
    	}
    	//

        // DELETE_CONVERSATION
    	if (command.equals(custom_messages.DELETE_CONVERSATION)){
    		conversation.is_started = false;
            messages.add("---Disconnected---");
        //  end.setVisibility(View.INVISIBLE); //end is invisible
        //  newConversation.setVisibility(View.VISIBLE); //new is visible
            
            updateMessages();

    	}
    	//

    	 // TRADE_NAME
    	if (command.equals(custom_messages.TRADE_NAME)){
    		//messages.add("MESSAGE THAT CAME TO ME: " + msg); updateMessages(); //DEBUG
    		//messages.add("my name was: " + conversation.me.name); updateMessages(); //DEBUG
    		String name = infoPicker(msg, conversation.me.name); //find whichever one belongs to the partner   		
            messages.add("Your Partner's name is: " + name);
            conversation.partner.name = name; //update conversation.partner.name
            
            updateMessages();
            

    	}
    	//

        // TRADE_SEX
    	if (command.equals(custom_messages.TRADE_SEX)){
    		  		
    		String sex = infoPicker(msg, conversation.me.sex); //find whichever one belongs to the partner
    		
    		if (sex.equals("1")){
    			messages.add("Your Partner is a man");
    			conversation.partner.sex = "M"; //update conversation.partner.sex
    		}
    		if (sex.equals("2")){
    			messages.add("Your Partner is a woman");
    			conversation.partner.sex = "F"; //update conversation.partner.sex
    		}
    		
            updateMessages();
        
    	}
    	//

        // TRADE_AGE
    	if (command.equals(custom_messages.TRADE_AGE)){
    		
    		String age = infoPicker(msg, conversation.me.age); //find whichever one belongs to the partner
    		messages.add("Your Partner is " + age + " years old.");
    		conversation.partner.age = age; //update conversation.partner.age
            updateMessages();
            
    	}
    	//

        // TRADE_LOCATION
    	if (command.equals(custom_messages.TRADE_LOCATION)){
    		
    		String location = infoPicker(msg, conversation.me.location); //find whichever one belongs to the partner
    		messages.add("Your Partner is from " + location);
    		conversation.partner.location = location; //update partnerLocation
            updateMessages();
            
    	}
    	//
    	

    	
    }
    
    
    
    
    
    //End button
    public void endClick(View view) {

			sendMessage(Server.agent,custom_messages.DELETE_CONVERSATION);            
			conversation.is_started = false; //make us note of it
            messages.add("---Disconnected---");
            updateMessages();
           
         // end.setVisibility(View.INVISIBLE); //end is invisible
         // newConversation.setVisibility(View.VISIBLE); //new is visible
            
	}
    
    
    
    //NewConversation button
    public void newconversationClick(View view) {
    	
       // end.setVisibility(View.VISIBLE); //end is visible
       // newConversation.setVisibility(View.INVISIBLE); //new is invisible
    	
		requestConversation();
		
    }
    //
    //Send button
    public void sendClick(View view) {
    	
        String text = mSendText.getText().toString();

        if (conversation.is_started && !text.equals("")) {
        	sendMessage(conversation.partner.address,text);
        	messages.add("You: " + text);
            setListAdapter();
            
        }
        else{
        	//sendMessage(Server.agent,text);
        }
        
    	mSendText.setText("");
    	
    }
    //
    
    //Name button
    public void nameClick(View view) {
    	if (conversation.me.name == "myNotSetDefaultName"){
    	
    		AlertDialog.Builder alert = new AlertDialog.Builder(this);

    		alert.setTitle("Enter your name");
    		alert.setMessage("Enter your name to succeed.");

    		// Set an EditText view to get user input 
    		final EditText input = new EditText(this);
    		alert.setView(input);

    		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				Editable value = input.getText();
    				// Do something with value!
    				if (value.toString().length() > 0){
    					conversation.me.name = value.toString();
    					SharedPreferences settings = getPreferences(0);
    					SharedPreferences.Editor editor = settings.edit();
    					editor.putString("name", conversation.me.name);
    					editor.commit();

    				}
    				else conversation.me.name = "myNotSetDefaultName";

    					
    			}
    		});

    		

    		alert.show();
    	}
    	if (conversation.me.name != "myNotSetDefaultName" && conversation.me.name != ""){
    		sendMessage(Server.agent,custom_messages.TRADE_NAME + ":" + conversation.me.name);
    	}
    }
    //
    
    //Age button
    public void ageClick(View view) {
    	if (conversation.me.age == "myNotSetDefaultAge"){
        	
    		AlertDialog.Builder alert = new AlertDialog.Builder(this);

    		alert.setTitle("Enter your age");
    		alert.setMessage("Enter your age to succeed.");

    		// Set an EditText view to get user input 
    		final EditText input = new EditText(this);
    		alert.setView(input);

    		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				Editable value = input.getText();
    				// Do something with value!
    				if (value.toString().length() > 0){
    					conversation.me.age = value.toString();
    					SharedPreferences settings = getPreferences(0);
    					SharedPreferences.Editor editor = settings.edit();
    					editor.putString("age", conversation.me.age);
    					editor.commit();

    				}
    				else conversation.me.age = "myNotSetDefaultName";

    					
    			}
    		});

    		

    		alert.show();
    	}
    	if (conversation.me.age != "myNotSetDefaultAge" && conversation.me.age != ""){
    		sendMessage(Server.agent,custom_messages.TRADE_AGE + ":" + conversation.me.age);
    	}
    }
    //
    
    //Sex button
	public void sexClick(View view) {
		int sexForServer;
		if (conversation.me.sex == "M") sexForServer = 1;
		else sexForServer = 2;
    	sendMessage(Server.agent,custom_messages.TRADE_SEX + ":" + sexForServer);                
	}
	//
	
	//Location button
	public void locationClick(View view) {
    	sendMessage(Server.agent,custom_messages.TRADE_LOCATION + ":" + conversation.me.location);                
	}
	//
	 
    //For connecting to server
    public void connectServer(){
    	dialog = ProgressDialog.show(Menu.this, "", "Connecting to server...", true);
    	// Connection Settings
        String host = Server.host;
        String port = Server.port;
        String service = Server.service;
  
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
            
        } catch (XMPPException ex) {
        	Log.e("XMPPClient", "[SettingsDialog] Failed to log in as anonymous" );
        	Log.e("XMPPClient", ex.toString());
            setConnection(null);
        }
        
        dialog.dismiss(); //Clear the connection pending dialog
    }
    // End of connectServer function
   
    
    /**
     * Function to pick your own info from the trade message coming from the server
    @param msg Received message
    @param myinfo My Information
    @return Info that's not mine
    */
    
    public String infoPicker (String msg, String myinfo) {
		String info1 = msg.split(":")[1]; //get the the info1
		String info2 = msg.split(":")[2]; //get the the info2

		if (info1.equals(myinfo)) //info1 is mine
			return info2;
		else // info2 is mine
			return info1;   
    }
    
    /**
     * Called when a connection is established with the XMPP server
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
                    	String msg = message.getBody();
                        
                        if (fromName.equals(Server.agent)){ //If this message is from conversation server
                        	handleCustomMessage(msg); //Handle it with this function
                        }
                        else{ // If this is a regular message
                        	if (conversation.partner.sex==null && conversation.partner.age==null) { //nothing known besides name
                                messages.add(conversation.partner.name + ": " + msg); //display only the name (with the message)
                                updateMessages();		
                        	}
                        	else { //at least one extra thing is known
                        		if (conversation.partner.sex==null) { //only conversation.partner.age known
                        		     messages.add(conversation.partner.name + " (" + conversation.partner.age +")" + ": " + msg);
                                     updateMessages();		
                        		}
                        		else if (conversation.partner.age==null) { //only conversation.partner.sex known
                        			 messages.add(conversation.partner.name + " (" + conversation.partner.sex +")" + ": " + msg);
                                     updateMessages();		
                        		}
                        			 else {//both known
                            			 messages.add(conversation.partner.name + " (" + conversation.partner.sex + ", " + conversation.partner.age + ")" + ": " + msg);
                                         updateMessages();                    				 
                        		     }
                        		
                        	}

                        }
                    }
                }
            }, filter);
        }
    }
    // End of setConnection function
    
    
    private double[] getGPS() {
    	LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	List<String> providers = lm.getProviders(true);

    	Location l = null;
   	 
    	for (int i=providers.size()-1; i>=0; i--) {
    		l = lm.getLastKnownLocation(providers.get(i));
    		if (l != null) break;
   	 	}
   	 
   	 	double[] gps = new double[2];
   	 	if (l != null) {
   	 		gps[0] = l.getLatitude();
   	 		gps[1] = l.getLongitude();
   	 	}

   	 	return gps;
   	}
    
    //Function to add messages to list
    private void setListAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.multi_line_list_item, messages);
        mList.setAdapter(adapter);
    }
    // End of setListAdapter function
   
    /**
    * Updates messages seen on the screen
    @return void
    */
    public void updateMessages(){
    	mHandler.post(new Runnable() {
            public void run() {
                setListAdapter(); 
            }
        });
    }
    
    // Called on the activity creation.
    @Override
    public void onCreate(Bundle icicle) {
    	
    	//check for internet connection?

    	
        super.onCreate(icicle);
        setContentView(R.layout.main);
        
        mSendText = (EditText) this.findViewById(R.id.sendText);
        mList = (ListView) this.findViewById(R.id.listMessages);
        end = (Button) this.findViewById(R.id.end);

    	newConversation = (Button) this.findViewById(R.id.newconversation);
    	locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	  
    	SharedPreferences settings = getPreferences(0);
    	conversation.me.name = settings.getString("name", "myNotSetDefaultName");
    	conversation.me.age = settings.getString("age","myNotSetDefaultAge");
        
        setListAdapter();        
        
        connectServer();
        requestConversation();

    }
    // End of onCreate function
    
    // Called on the activity destroy.
    @Override
    public void onDestroy() {
    	sendMessage(Server.agent,custom_messages.DELETE_CONVERSATION);                
    }
    // End of destroy function
    
    //pauses listener while app is inactive
    @Override
    public void onPause() {
        super.onPause();
        locmgr.removeUpdates(onLocationChange);
    }
    
    //reactivates listener when app is resumed
    @Override
    public void onResume() {
        super.onResume();
        locmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,10000.0f,onLocationChange);
    }

    

}
// End of class Menu