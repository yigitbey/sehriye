import com.mageroya.tencere2.JServer;
import com.mageroya.tencere2.CServer;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.packet.Message;


public class Conversation{
    public Boolean isStarted = false;
    public String dateStarted;
    public String dateJoined;
    public LocalUser localUser;
    public RemoteUser remoteUser;
    public ConnConfiguration connConfig;
    public JServer jserver;

    public Conversation(LocalUser localUser, RemoteUser remoteUser){
	this.connConfig = new ConnectionConfiguration(Jserver.host, Integer.parseInt(Jserver.port), Jserver.service);
	this.jServer = Jserver(this.connConfig);
	this.localUser = localUser;
	this.remoteUser = remoteUser;
	
    }

    public void sendMessage(RemoteUser to, String text){
	Message msg = new Message(to.JID, Message.Type.chat);
	msg.setBody(text);
	this.jserver.sendPacket(msg);
    }

    public void sendToPartner(String text){
	Message msg = new Message(this.remoteUser.getName(), Message.Type.chat);
	msg.setBody(text);
	this.jserver.sendPacket(msg);
    }


    //Function to handle server messages
    public void handleCustomMessage(String msg){

	String command = msg.split(":")[0]; // Parse Command

	Log.i("Tencere",command);
	
        // START_CONVERSATION
	if (command.equals(CServer.START_CONVERSATION)){
	    
	    this.remoteUser = new RemoteUser(msg.split(":")[1]); //Initialize a new remoteUser
	    this.isStarted = true;

	    //messages.add("---Conversation Started---");
	    //updateMessages();

	}
	//

        // DELETE_CONVERSATION
	if (command.equals(CServer.DELETE_CONVERSATION)){
            is_started = false;
            
	    //messages.add("---Disconnected---");
	    //updateMessages();

	}
	//

	// TRADE_NAME
	if (command.equals(CServer.TRADE_NAME)){
	    //messages.add("MESSAGE THAT CAME TO ME: " + msg); updateMessages(); //DEBUG
	    //messages.add("my name was: " + myName); updateMessages(); //DEBUG
	    String name = infoPicker(msg, localUser.getName()); //find whichever one belongs to the partner  
 
            this.remoteUser.name = name; //update remoteUser's name

            //messages.add("Your Partner's name is: " + name);            
            //updateMessages();
            

	}
	//

        // TRADE_SEX
	if (command.equals(CServer.TRADE_SEX)){
	    
	    String sex = infoPicker(msg, localUser.getSex()); //find whichever one belongs to the partner
	    
	    if (sex.equals("1")){
		//messages.add("Your Partner is a man");
		remoteUser.sex = "M"; //update remoteUser's sex
	    }
	    if (sex.equals("2")){
		//messages.add("Your Partner is a woman");
		remoteUser.sex = "F"; //update remoteUser's sex
	    }
	    
            //updateMessages();
            

	    //TODO: handle cases other than 1 & 2

        
	}
	//

        // TRADE_AGE
	if (command.equals(CServer.TRADE_AGE)){
	    
	    String age = infoPicker(msg, localUser.getAge()); //find whichever one belongs to the partner
	    remoteUser.age = age; //update remoteUser's age
            
	    
	    //messages.add("Your Partner is " + age + " years old.");
	    //updateMessages();
            
	}
	//

        // TRADE_LOCATION
	if (command.equals(CServer.TRADE_LOCATION)){
	    
	    String location = infoPicker(msg, localUser.getLocation()); //find whichever one belongs to the partner
	    remoteUser.location = location; //update remoteUser's location

	    //messages.add("Your Partner is from " + location);
            //updateMessages();
            
	}
	//
	

	
    }
    // End of handleCustomMessage function

    //Function to pick your own info from the trade message coming from the server
    public String infoPicker (String msg, String myinfo) {

	String info1 = msg.split(":")[1]; //get the the info1
	String info2 = msg.split(":")[2]; //get the the info2

	if (info1.equals(myinfo)) //info1 is mine
	    return info2;
	else // info2 is mine
	    return info1;

    
    }
    // End of infoPicker Function




}
