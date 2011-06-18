import JServer;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.packet.Message;


public class Conversation{
    public Boolean isStarted;
    public String dateStarted;
    public String dateJoined;
    public LocalUser localUser;
    public RemoteUser remoteUser;
    
    public Conversation(){
	public ConnConfiguration connConfig = new ConnectionConfiguration(Jserver.host, Integer.parseInt(Jserver.port), Jserver.service);
	public JServer jServer = Jserver(connConfig);
	
    }

    public void SendMessage(String to, String text){
	Message msg = new Message(to, Message.Type.chat);
	msg.setBody(text);
	jserver.sendPacket(msg);
    }
