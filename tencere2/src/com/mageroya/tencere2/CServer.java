import com.mageroya.tencere2.Conversation;
import com.mageroya.tencere2.LocalUser;
import com.mageroya.tencere2.RemoteUser;


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

	String location = localUser.getLocation();
 	String messageToSend = this.PENDING_CONVERSATION + ":" + location;

	conversation.sendMessage(remoteUser, messageToSend);

	//TODO: Show "Waiting for a match..." dialog
    }


}
