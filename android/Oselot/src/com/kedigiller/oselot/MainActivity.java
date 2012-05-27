package com.kedigiller.oselot;



import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends Activity {

    //Android *Manager Internals
    public LocationManager locmgr = null; //-- Location Manager 
    public TelephonyManager mTelephonyMgr;      //-- Telephony Manager  
    public Phone myPhone;  //-- This Phone
	public ConnectivityManager conMgr;    //-- Connectivity Manager
    
	//Conversation variables
	public Xmpp xmpp; //-- Xmpp Manager  
	public User me; //-- Me as a conversation partner
    public User opponent; //-- My opponent
    public User nullPartner; //-- Null partner
	public Conversation conversation; //-- Xmpp Conversation
    
	//UI Views
	public static ProgressDialog dialog; //-- Info Box
    public EditText txtMessage; //-- Message box
    public ListView lstMessages; //-- Message list
    public Button btnEndConversation;// -- End conversation button
    public Button btnNewConversation; // -- New conversation button
    public ImageButton btnAddContact; // -- Add contact button
    public ArrayList<String> messages = new ArrayList<String>(); 
    public Handler mHandler = new Handler();
	
	//Networking on main thread
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    //Application to hold application-wide variables.
	public Oselot app;

	//Handlers for different types of xmpp messages
	public static Handler messageHandler;
	public static Handler conversationHandler;
	public static Handler tradeHandler;
	
	public void initialise(){
		//Create a new xmpp object
		this.xmpp = new Xmpp();
		
		//Android internals for location & telephony & connection & phone state
		this.locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //Initialise the location manager    	
        this.mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); //Initialise the telephony manager
		this.conMgr = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE); //Initialise the Connectivity Manager
    	this.myPhone = new Phone(mTelephonyMgr,conMgr); //Get phone state


		//Setting app variables
    	app = (Oselot)this.getApplication();
    	app.xmpp = this.xmpp;
		app.locmgr = this.locmgr;
		app.mTelephonyMgr = this.mTelephonyMgr;
		app.conMgr = this.conMgr;
		app.myPhone = this.myPhone;
		app.mainActivity = this;
		
		//Initialise UI Buttons
        txtMessage = (EditText) this.findViewById(R.id.txtMessage);
        lstMessages = (ListView) this.findViewById(R.id.lstMessages);
        btnEndConversation = (Button) this.findViewById(R.id.btnEndConversation);
    	btnNewConversation = (Button) this.findViewById(R.id.btnEndConversation);
    	 
    	
    	//Initialise the location manager
    	locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	
    	//Initialise the telephony manager
        mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); 
        
        //Initialise the Connectivity Manager
		conMgr = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
    	
    	//Get stored user variables
    	SharedPreferences settings = getPreferences(0);
    	String storedName = settings.getString("name", "myNotSetDefaultName");
    	String storedAge = settings.getString("age","myNotSetDefaultAge");
    	String storedMail = settings.getString("mail","myNotSetDefaultMail");
    	String storedSex = settings.getString("sex","myNotSetDefaultSex");
    	
        //Create a conversation
        me = new User(storedName, storedAge ,storedSex,null,myPhone.number,storedMail);
        nullPartner = new User("Stranger",null,null,null,null,null);
        conversation = new Conversation(me,nullPartner);
        setListAdapter(); 
        xmpp.me = me;
        xmpp.partner = nullPartner;		
    	
    	
    	//Get phone number from device
    	myPhone = new Phone(mTelephonyMgr,conMgr);
    	
	}
	
	//Function to initialise handlers for conversation status&trade&text xmpp messages
	public void initialise_handlers(){
    	
    	//Conversation start & end Handler
		conversationHandler = new Handler(){
			@Override
			public void handleMessage(Message conversation_status){				
				if (conversation_status.arg1 == 1){
					conversation.is_started = true;
					Bundle conversationBundle = conversation_status.getData();
					conversation.partner = new User();
					conversation.partner.address = conversationBundle.getString("partner");
				}
				else if (conversation_status.arg1 == 0){
					conversation.is_started = false;
					conversation.partner = nullPartner;
				}
			}
		};
		
		//Message handler
		messageHandler = new Handler() {
			  @Override
			  public void handleMessage(Message textReceived) {

				Bundle mesajBundle = textReceived.getData();
				String msg = mesajBundle.getString("mesaj");
			
				messages.add("Stranger: " + msg);
	            setListAdapter();

			  }
		};
		
		//Trade handler
		tradeHandler = new Handler() {
			  @Override
			  public void handleMessage(Message textReceived) {

				Bundle mesajBundle = textReceived.getData();
				String msg = mesajBundle.getString("mesaj");
				String type = mesajBundle.getString("type");
								
				if (type.equals("name")){
					String result = infoPicker(msg,conversation.me.name);
					messages.add("Your partner's name is: " + result);
					conversation.partner.name = result;
				}
				if (type.equals("age")){
					String result = infoPicker(msg,conversation.me.age);
					messages.add("Your partner's age is: " + result);
					conversation.partner.age = result;
				}
				if (type.equals("sex")){
					String result = infoPicker(msg,conversation.me.sex);
					if (result.equals("M")){
						messages.add("Your partner is a man.");
						conversation.partner.sex = "Man";
					}
					else if(result.equals("F")){
						messages.add("Your partner is a woman.");
						conversation.partner.sex = "Woman";
					}
					
				}
				if (type.equals("location")){
					String result = infoPicker(msg,conversation.me.location);
					messages.add("Your partner's location is: " + result);
					conversation.partner.location = result;
				}
				if (type.equals("mail")){
					String result = infoPicker(msg,conversation.me.mail);
					messages.add("Your partner's mail is: " + result);
					conversation.partner.mail = result;
				}
				if (type.equals("number")){
					String result = infoPicker(msg,conversation.me.number);
					messages.add("Your partner's number is: " + result);
					conversation.partner.number = result;
				}

				setListAdapter();

			  }
		};
	}
	//End of initialise_handlers

	//** Function for telling XMPP instance to start a connection & create a conversation */
    public void initialiseConnection(){
    	app.xmpp.connectServer();
    	app.conversation.requestConversation(me,xmpp);
    }
    //End of initialiseConnection

    
	//** XMPP connection & new conversation procedure */
	public void start_connection_procedure(){
		
    	//Check Internet connection
    	if (myPhone.checkInternetConnection() == true){
    		Log.i("Oselot","Internet connection OK");
    		
            dialog = ProgressDialog.show(this, "", "Waiting for a match...", true);
            this.app.dialog = dialog;
            
        	conversation = new Conversation(me,opponent);
        	app.conversation = this.conversation;
            this.initialiseConnection();            
            
    	}else{
    		Log.e("Oselot","Internet connection Failed");
    		Toast.makeText(getApplicationContext(), "Unable to connect to internet", Toast.LENGTH_SHORT).show();
         }
	}
	//end of start_connection_procedure
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainold);
        
        //Networking on main thread
    	StrictMode.setThreadPolicy(policy); 
    		
    	//Initialise app variables
    	this.initialise();

    	//Setup handlers for various xmpp messages
    	this.initialise_handlers();

		//Get my Location
		double[] myLocation = app.getLocation();
		//Serialise the location 
		this.me.location = Double.toString(myLocation[0]) + ":" + Double.toString(myLocation[1]);  
	
		//Start a new connection & conversation
		this.start_connection_procedure();

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
    //End of btnAddContactClick

    /**
     * Defines end button behaviour
     */
    public void btnEndConversationClick(View view) {

			xmpp.sendMessage(Server.agent,custom_messages.DELETE_CONVERSATION);        
	        conversation = new Conversation(me,nullPartner);
            messages.add("---Disconnected---");
            updateMessages();
            
	}

    /**
     * Defines new button behaviour
     */
    public void btnNewConversationClick(View view) {
    	
    	//Check Internet connection
    	if (myPhone.checkInternetConnection() == true){
    		Log.i("Oselot","Internet connection OK");
    		
            dialog = ProgressDialog.show(this, "", "Waiting for a match...", true);
            this.app.dialog = dialog;
            
        	conversation = new Conversation(me,opponent);
        	app.conversation = this.conversation;
            this.initialiseConnection();            
            
    	}else{
    		Log.e("Oselot","Internet connection Failed");
    		Toast.makeText(getApplicationContext(), "Unable to connect to internet", Toast.LENGTH_SHORT).show();
         }

    }
    
    /**
     * Defines send button behaviour
     */
    public void btnSendClick(View view) {
    	
    	//Get user input
        String text = txtMessage.getText().toString();

        if (conversation.is_started && !text.equals("")) {
        	xmpp.sendMessage(conversation.partner.address,text);
        	messages.add("You: " + text);
            setListAdapter();
            
        }
        
        //Empty user input button
    	txtMessage.setText("");
    	
    }
    //
    

    
    /**
     * Defines name button behaviour
     */
    public void nameClick(View view) {
    	if (conversation.nameClicked == false){
    		
	    	if (conversation.me.name == "myNotSetDefaultName"){
	    	
	    		AlertDialog.Builder alert = new AlertDialog.Builder(this);
	
	    		alert.setTitle("Enter your name");
	    		alert.setMessage("Enter your name to succeed.");
	
	    		// Set an EditText view to get user input 
	    		final EditText input = new EditText(this);
	    		alert.setView(input);
	
	    		alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				Editable value = input.getText();
	    				// Do something with value!
	    				if (value.toString().length() > 0){
	    					conversation.me.name = value.toString();
	    					SharedPreferences settings = getPreferences(0);
	    					SharedPreferences.Editor editor = settings.edit();
	    					editor.putString("name", conversation.me.name);
	    					editor.commit();
	    					xmpp.sendMessage(Server.agent,custom_messages.TRADE_NAME + ":" + conversation.me.name);
	    					conversation.nameClicked = true;
	
	    				}
	    				else conversation.me.name = "myNotSetDefaultName";
	
	    					
	    			}
	    		});
	    		alert.show();
	    	}
	    	if (conversation.me.name != "myNotSetDefaultName" && conversation.me.name != ""){
	    		xmpp.sendMessage(Server.agent,custom_messages.TRADE_NAME + ":" + conversation.me.name);
	    		conversation.nameClicked = true;
	    	}
    	}
    }
    //End of name click
    
    /**
     * Defines age button behaviour
     */
    public void ageClick(View view) {
    	if (conversation.ageClicked == false){
    		
	    	if (conversation.me.age == "myNotSetDefaultAge"){
	        	
	    		AlertDialog.Builder alert = new AlertDialog.Builder(this);
	
	    		alert.setTitle("Enter your age");
	    		alert.setMessage("Enter your age to succeed.");
	
	    		// Set an EditText view to get user input 
	    		final EditText input = new EditText(this);
	    		alert.setView(input);
	
	    		alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int whichButton) {
	    				Editable value = input.getText();
	    				// Do something with value!
	    				if (value.toString().length() > 0){
	    					conversation.me.age = value.toString();
	    					SharedPreferences settings = getPreferences(0);
	    					SharedPreferences.Editor editor = settings.edit();
	    					editor.putString("age", conversation.me.age);
	    					editor.commit();
	    					xmpp.sendMessage(Server.agent,custom_messages.TRADE_AGE + ":" + conversation.me.age);
	    					conversation.ageClicked = true;
	
	    				}
	    				else conversation.me.age = "myNotSetDefaultName";
	
	    					
	    			}
	    		});
	    		alert.show();
	    	}
	    	if (conversation.me.age != "myNotSetDefaultAge" && conversation.me.age != ""){
	    		xmpp.sendMessage(Server.agent,custom_messages.TRADE_AGE + ":" + conversation.me.age);
	    		conversation.ageClicked = true;
	    	}
    	}
    }
    //End of age click
    
    /**
     * Defines sex button behaviour
     */
	public void sexClick(View view) {
    	if (conversation.sexClicked == false){
    		conversation.sexClicked = true;
			int sexForServer = 2; //Defaults to Female
			if (conversation.me.sex == "M") 
				sexForServer = 1;
	    	xmpp.sendMessage(Server.agent,custom_messages.TRADE_SEX + ":" + sexForServer);           
    	}
	}
	//
	
	
    /**
     * Defines location button behaviour
     */
	public void locationClick(View view) {
    	if (conversation.locationClicked == false){
    		conversation.locationClicked = true;
    		xmpp.sendMessage(Server.agent,custom_messages.TRADE_LOCATION + ":" + conversation.me.location);        
    	}
	}
	//
	
	/**
     * Defines number button behaviour
     */
	public void numberClick(View view) {
    	if (conversation.numberClicked == false){
    		conversation.numberClicked = true;
    		xmpp.sendMessage(Server.agent,custom_messages.TRADE_NUMBER + ":" + myPhone.number);     
    	}
	}
	//
	
	/**
     * Defines mail button behaviour
     */
	public void mailClick(View view) {
    	if (conversation.mailClicked == false){
    		conversation.mailClicked = true;
    		xmpp.sendMessage(Server.agent,custom_messages.TRADE_MAIL + ":" + conversation.me.mail);     
    	}
	}
	//
    
	
    /**
     * Function to pick your own info from the trade message coming from the server
    @param msg Received message
    @param myinfo My Information
    @return Info that's not mine
    */
    public String infoPicker (String msg, String myinfo) {
    	Log.i("oselot.infoPicker",msg + ":" + myinfo);
		String info1 = msg.split(":")[1]; //get the the info1
		String info2 = msg.split(":")[2]; //get the the info2

		Log.i("oselot.infoPicker",info1 + ":" + info2 + ":" + myinfo);
		if (info1.equals(myinfo)) //info1 is mine
			return info2;
		else // info2 is mine
			return info1;   
    }
    //End of infoPicker

	
    /**
     * Function to add & update messages to list
     * */
    private void setListAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.multi_line_list_item, messages);
        lstMessages.setAdapter(adapter);
    }
    //End of setListAdapter
 
   
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
    //End of updateMessages
    
    //** Function to clear progress dialog on this activity */
    public static void clearDialog(){
    	dialog.dismiss();
    }
    //End of clearDialog
}
//End of MainActivity
