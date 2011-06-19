import JServer;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.packet.Message;


public class Conversation{
    public Boolean isStarted;
    public String dateStarted;
    public String dateJoined;
    public LocalUser localUser;
    public RemoteUser remoteUser;
    public ConnConfiguration connConfig;
    public Jserver jserver;

    public Conversation(LocalUser localUser, RemoteUser remoteUser){
	this.connConfig = new ConnectionConfiguration(Jserver.host, Integer.parseInt(Jserver.port), Jserver.service);
	this.jServer = Jserver(this.connConfig);
	this.localUser = localUser;
	this.remoteUser = remoteUser;
	
    }

    public void SendMessage(String to, String text){
	Message msg = new Message(to, Message.Type.chat);
	msg.setBody(text);
	this.jserver.sendPacket(msg);
    }

    public void sendToPartner(String text){
	Message msg = new Message(this.remoteUser, Message.Type.chat);
	msg.setBody(text);
	this.jserver.sendPacket(msg);
    }

}
