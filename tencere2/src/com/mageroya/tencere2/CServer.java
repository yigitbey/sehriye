import Conversation;
import LocalUser;
import RemoteUser;


public class CServer{
    public String address = "buluruzbirsey@appspot.com";
    public final String PENDING_CONVERSATION = "|PENCON";
    public final String START_CONVERSATION = "|STACON";
    public final String DELETE_CONVERSATION = "|DELETE_CONVERSATION";
    public final String TRADE_LOCATION = "|TRALOC";
    public final String TRADE_NAME = "|TRANAM";
    public final String TRADE_SEX = "|TRASEX";
    public final String TRADE_PHONENUMBER = "|TRAPHO";
    public final String TRADE_EMAIL = "TRAMAI";

    public Conversation requestConversation(LocalUser localUser){
	
	public double[] location = localUser.getLocation();
	public String message_to_send = Double.toString(location[0]) + ":" + Double.toString(location[1]);
	
	//TODO:
	//Send a Pending Conversation to server

    }


}
