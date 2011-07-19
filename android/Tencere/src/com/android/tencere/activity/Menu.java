package com.android.tencere.activity;

import com.android.tencere.activity.User;
import com.android.tencere.activity.Server;
import com.android.tencere.activity.Conversation;

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

import android.location.LocationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

/**
* Main class
*/ 
public class Menu extends Activity {
    //-- XMPP Connection
    private XMPPConnection connection = null;    
    //-- Conversation
    public Conversation conversation = null;
    //-- Location Manager 
    public LocationManager locmgr = null;
    //-- Telephony Manager
    TelephonyManager mTelephonyMgr; 
    //-- Connectivity Manager
	ConnectivityManager conMgr;
    //-- This Phone
    public Phone myPhone;
	//-- UI elements
    public ArrayList<String> messages = new ArrayList<String>(); 
    public Handler mHandler = new Handler();
    public EditText txtMessage;
    public ListView lstMessages;
    public ProgressDialog dialog;
    public ImageButton btnEndConversation;
    public ImageButton btnNewConversation; 
    public ImageButton btnAddContact;

    
    //Me
    public User me;
    //Null Partner
    public User nullPartner;

    
    /**
 	* Sends a message to a xmpp jid            
	@param  to  Jisd to send message                 
	@param  text Message to send                        
 	*/
    public void sendMessage(String to, String text){
        Log.i("Tencere", "Sending text [" + text + "] to [" + to +"]");
        Message msg = new Message(to, Message.Type.chat);
        msg.setBody(text);
        connection.sendPacket(msg);
    }
    
      
    /**
    * Function to request a conversation
    @return void
    */
    public void requestConversation(){
    	//Show a dialog box indicating the pending status
        dialog = ProgressDialog.show(Menu.this, "", "Waiting for a match...", true);
    	//Get location
    	double[] location = conversation.me.getLocation(); 	
    	//Serialize the location	
    	conversation.me.location = Double.toString(location[0]) + ":" + Double.toString(location[1]);  
    	//Send a PENDING_CONVERSATION
        sendMessage(Server.agent, custom_messages.PENDING_CONVERSATION + ":" + conversation.me.location);     
    }
    
    
    /**
    * Function to handle server messages<br>
    * If command is START_CONVERSATION, set partner's address and set conversation.is_started to true. Also remove the pending dialog box.<br>
    * If command is DELETE_CONVERSATION, set conversation.is_started to false<br>
    * If command is TRADE_*, set partner's info and print it on the screen 
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
    		conversation = new Conversation(me,nullPartner); //Reset it
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
    	
        // TRADE_NUMBER
    	if (command.equals(custom_messages.TRADE_NUMBER)){
    		
    		String number = infoPicker(msg, conversation.me.number); //find whichever one belongs to the partner
    		messages.add("Your Partner's number: " + number);
    		conversation.partner.number = number; //update partner's number
            updateMessages();
            
    	}
    	//
    	
        // TRADE_MAIL
    	if (command.equals(custom_messages.TRADE_MAIL)){
    		String mail = infoPicker(msg, conversation.me.mail); //find whichever one belongs to the partner
    		messages.add("Your Partner's mail: " + mail);
    		conversation.partner.mail = mail; //update partner's mail
            updateMessages();
            
    	}
    	//

    }
    
    
    /**
     * Defines add contact button behaviour
     */
    public void btnAddContactClick(View view) {
    	
    	Intent addContactIntent = new Intent(ContactsContract.Intents.Insert.ACTION, ContactsContract.Contacts.CONTENT_URI);
    	addContactIntent.putExtra(ContactsContract.Intents.Insert.NAME, conversation.partner.name); // the name
    	addContactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, conversation.partner.number); // number
    	addContactIntent.putExtra(ContactsContract.Intents.Insert.EMAIL, conversation.partner.mail); // mail
    	
    	startActivity(addContactIntent);

    }
    //
    
    
    
    /**
     * Defines end button behaviour
     */
    public void btnEndConversationClick(View view) {

			sendMessage(Server.agent,custom_messages.DELETE_CONVERSATION);        
	        conversation = new Conversation(me,nullPartner);
            messages.add("---Disconnected---");
            updateMessages();
           
         // end.setVisibility(View.INVISIBLE); //end is invisible
         // newConversation.setVisibility(View.VISIBLE); //new is visible
            
	}

    /**
     * Defines new button behaviour
     */
    public void btnNewConversationClick(View view) {
    	
       // end.setVisibility(View.VISIBLE); //end is visible
       // newConversation.setVisibility(View.INVISIBLE); //new is invisible
    	if (connection == null){
    		connectServer();
    	}
    	else if(conversation.is_started == true){
    		btnEndConversationClick(view); //Act as if pressed on end button
    	}
		requestConversation(); // request a new conversation

    }
    
    /**
     * Defines send button behaviour
     */
    public void btnSendClick(View view) {
    	
        String text = txtMessage.getText().toString();

        if (conversation.is_started && !text.equals("")) {
        	sendMessage(conversation.partner.address,text);
        	messages.add("You: " + text);
            setListAdapter();
            
        }
        else{
        	//sendMessage(Server.agent,text);
        }
        
    	txtMessage.setText("");
    	
    }
    //
    
    /**
     * Defines name button behaviour
     */
    public void nameClick(View view) {
    	if (conversation.nameClicked == false){
    		conversation.nameClicked = true;
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
	    					sendMessage(Server.agent,custom_messages.TRADE_NAME + ":" + conversation.me.name);
	
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
    }
    //
    
    /**
     * Defines age button behaviour
     */
    public void ageClick(View view) {
    	if (conversation.ageClicked == false){
    		conversation.ageClicked = true;
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
	    					sendMessage(Server.agent,custom_messages.TRADE_AGE + ":" + conversation.me.age);
	
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
    }
    //
    
    /**
     * Defines sex button behaviour
     */
	public void sexClick(View view) {
    	if (conversation.sexClicked == false){
    		conversation.sexClicked = true;
			int sexForServer = 2; //Defaults to Female
			if (conversation.me.sex == "M") 
				sexForServer = 1;
	    	sendMessage(Server.agent,custom_messages.TRADE_SEX + ":" + sexForServer);           
    	}
	}
	//
	
    /**
     * Defines location button behaviour
     */
	public void locationClick(View view) {
    	if (conversation.locationClicked == false){
    		conversation.locationClicked = true;
    		sendMessage(Server.agent,custom_messages.TRADE_LOCATION + ":" + conversation.me.location);        
    	}
	}
	//
	
	/**
     * Defines number button behaviour
     */
	public void numberClick(View view) {
    	if (conversation.numberClicked == false){
    		conversation.numberClicked = true;
    		sendMessage(Server.agent,custom_messages.TRADE_NUMBER + ":" + myPhone.number);     
    	}
	}
	//
	
	
    /**
     * Defines name button behaviour
     */
    public void mailClick(View view) {
    	if (conversation.mailClicked == false){
    		conversation.mailClicked = true;
	    	if (conversation.me.mail == "myNotSetDefaultMail"){
	    	
	    		AlertDialog.Builder alert = new AlertDialog.Builder(this);
	
	    		alert.setTitle("Enter your mail address");
	    		alert.setMessage("Enter your mail address to succeed.");
	
	    		// Set an EditText view to get user input 
	    		final EditText input = new EditText(this);
	    		alert.setView(input);
	
	    		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				Editable value = input.getText();
	    				// Do something with value!
	    				if (value.toString().length() > 0){
	    					conversation.me.mail = value.toString();
	    					SharedPreferences settings = getPreferences(0);
	    					SharedPreferences.Editor editor = settings.edit();
	    					editor.putString("mail", conversation.me.mail);
	    					editor.commit();
	    					
	
	    				}
	    				else conversation.me.mail = "myNotSetDefaultMail";
	
	    					
	    			}
	    		});
	    		alert.show();
	    	}
	    	if (conversation.me.mail != "myNotSetDefaultMail" && conversation.me.mail != ""){
	    		sendMessage(Server.agent,custom_messages.TRADE_MAIL + ":" + conversation.me.mail);
	    	}
    	}
    }
    //
	 
    /**
     * Connects to Jabber server and logs in anonymously.
     * <br>
     * Server must accept SASL Anonymous logins. 
     */
    public void connectServer(){
    	dialog = ProgressDialog.show(Menu.this, "", "Connecting to server...", true);
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
            dialog = ProgressDialog.show(Menu.this, "", "Unable to connect", true);
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
            dialog.dismiss();             
        }
        //If logging in was not possible, log it and show an error to user
        catch (XMPPException ex) {
        	Log.e("Tencere", "[SettingsDialog] Failed to log in as anonymous" );
        	Log.e("Tencere", ex.toString());
            setConnection(null);
            //Show error
            dialog = ProgressDialog.show(Menu.this, "", "Unable to login", true);
        }
        

    }
   
    
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
            
            //Filter only chat messages
            connection.addPacketListener(new PacketListener() {
            	
            	//On a chat message received
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    
                    //If the message is not empty
                    if (message.getBody() != null) {
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        String msg = message.getBody();
                        
                        //If this message is from conversation server
                        if (fromName.equals(Server.agent)){ 
                        	handleCustomMessage(msg); //Handle it with this function
                        	Log.i("Tencere", "Got text [" + msg + "] from agent");
                        }
                        // If this is a regular message
                        else{ 
                        	messages.add(conversation.partner.name + ": " + msg); //display only the name (with the message)
                        	updateMessages();          
                        	Log.i("Tencere", "Got text [" + msg + "] from partner");
                        }
                    }
                    
                    
                }
            }, filter);
        }
    }
    
    
    /**
     * Function to add messages to list
     * */
    private void setListAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.multi_line_list_item, messages);
        lstMessages.setAdapter(adapter);
    }

   
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
    
    /** Called on the activity creation.

     @param icicle Bundle
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        
        
        
        //Initialize the main UI
        setContentView(R.layout.main);
        
        
        //Initialize UI Buttons
        txtMessage = (EditText) this.findViewById(R.id.txtMessage);
        lstMessages = (ListView) this.findViewById(R.id.lstMessages);
        btnEndConversation = (ImageButton) this.findViewById(R.id.btnEndConversation);
    	btnNewConversation = (ImageButton) this.findViewById(R.id.btnEndConversation);
    	 
    	
    	//Initialize the location manager
    	locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	
    	//Initialize the telephony manager
        mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); 
        
        //initialize the Connectivity Manager
		conMgr = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    	
    	//Get stored user variables
    	SharedPreferences settings = getPreferences(0);
    	String storedName = settings.getString("name", "myNotSetDefaultName");
    	String storedAge = settings.getString("age","myNotSetDefaultAge");
    	String storedMail = settings.getString("mail","myNotSetDefaultMail");
    	
    	
    	//Get phone number
    	myPhone = new Phone(mTelephonyMgr,conMgr);
    	
        //Create a conversation
        me = new User(storedName, storedAge ,null,null,myPhone.number,storedMail,locmgr);
        nullPartner = new User("Stranger",null,null,null,null,null,locmgr);
        conversation = new Conversation(me,nullPartner);
        setListAdapter(); 
        
        if (myPhone.checkInternetConnection() == true){
	        //Connect to jabber server
	        connectServer(); 
	        //Request a conversation from the agent
	        requestConversation();
        }
        else{
        	Toast.makeText(getApplicationContext(), "Unable to connect to internet", Toast.LENGTH_SHORT).show();
        }

    }
    // End of onCreate function
    
    // Called on the activity destroy.
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	sendMessage(Server.agent,custom_messages.DELETE_CONVERSATION);                
    }
    // End of destroy function
    
    //pauses listener while app is inactive
    @Override
    public void onPause() {
        super.onPause();
        locmgr.removeUpdates(conversation.me.onLocationChange);
    }
    
    //reactivates listener when app is resumed
    @Override
    public void onResume() {
        super.onResume();
        locmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,10000.0f,conversation.me.onLocationChange);
    }

    
}
// End of class Menu