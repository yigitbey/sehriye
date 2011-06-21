package com.mageroya.tencere2;

import com.mageroya.tencere2.JServer;
import com.mageroya.tencere2.CServer;
import com.mageroya.tencere2.Conversation;
import com.mageroya.tencere2.User;
import com.mageroya.tencere2.LocalUser;
import com.mageroya.tencere2.RemoteUser;



import java.util.ArrayList;
import java.util.List;

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


public class Tencere extends Activity {
	
    public LocationManager locmgr = null;
    public JServer jServer = null;
    public CServer cServer = null;
	public Conversation conversation = null;
	public LocalUser localUser = null;
	public RemoteUser remoteUser = null;
	
    // Called on the activity creation.
    @Override
    public void onCreate(Bundle icicle) {
    	
    	//check for internet connection?

    	
        super.onCreate(icicle);
        setContentView(R.layout.main);
        
    	locmgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	  
    	SharedPreferences settings = getPreferences(0);
    	localUser = new LocalUser("a@aa.aaa",settings);
    	jServer = new JServer(localUser);
    	cServer = new CServer();
    	
    	cServer.requestConversation(localUser,jServer);
    	
    	

    }
    // End of onCreate function
	

}
