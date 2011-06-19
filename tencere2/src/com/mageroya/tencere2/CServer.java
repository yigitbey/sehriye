import Conversation;
import LocalUser;
import RemoteUser;


public class CServer{
    public final String address = "buluruzbirsey@appspot.com";
    public final String PENDING_CONVERSATION = "|PENCON";
    public final String START_CONVERSATION = "|STACON";
    public final String DELETE_CONVERSATION = "|DELETE_CONVERSATION";
    public final String TRADE_LOCATION = "|TRALOC";
    public final String TRADE_NAME = "|TRANAM";
    public final String TRADE_SEX = "|TRASEX";
    public final String TRADE_PHONENUMBER = "|TRAPHO";
    public final String TRADE_EMAIL = "TRAMAI";

    public void requestConversation(LocalUser localUser){
	
       	RemoteUser remoteUser = new RemoteUser(this.address);
	Conversation conversation = new Conversation(localUser, remoteUser);

	public double[] location = localUser.getLocation();
	public String myLocation = Double.toString(location[0]) + ":" + Double.toString(location[1]);
	String message_to_send = this.PENDING_CONVERSATION + ":" + myLocation;

	conversation.sendMessage(remoteUser, message_to_send);

	//TODO: Show "Waiting for a match..." dialog
    }


}
