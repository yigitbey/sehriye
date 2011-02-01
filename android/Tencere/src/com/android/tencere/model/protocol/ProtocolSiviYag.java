package com.android.tencere.model.protocol;

public class ProtocolSiviYag extends Protocol {
 
	protected static String pendingConversation = "PENCON";
	protected static String startingConversation = "STACON";
	protected static String deletingConversation = "DELCON";
	
	protected static String tradePrefix = "TRA";
	protected static String tradeName = tradePrefix + "NAM";
	protected static String tradeSex = tradePrefix + "SEX";
	protected static String tradeAge = tradePrefix + "AGE";
	protected static String tradeLocation = tradePrefix + "LOC";
	
	protected static String chatIdentifier = "~";
	protected static String messageIdentifier = "|";
	protected static String parameterSplitter = ":";
	
	//TODO: locations will be got from Place model.
	protected static String currentLocx = "0.0";
	protected static String currentLocy = "0.0";
	
	private ProtocolSiviYag(){}
	
	
	protected static String constructMessage(byte msgType, String msgBody, String[] msgParams){
		Message penCon = new Message(msgType);
		
		if (msgType == Message.PROTOCOL_MESSAGE) penCon.setIdentifier(messageIdentifier);
		else if (msgType == Message.CHAT_MESSAGE) penCon.setIdentifier(chatIdentifier);
		
		penCon.setMessageBody(msgBody);
		penCon.setParams(msgParams);
		penCon.setParamsSplitter(parameterSplitter);
		
		return penCon.getMessage();
	}
	
	public static String createPendingConversation(){	
		return constructMessage( Message.PROTOCOL_MESSAGE, pendingConversation, new String[]{ currentLocx, currentLocy});
	}
	
	public static String createStartingConversation(){	
		return constructMessage( Message.PROTOCOL_MESSAGE, startingConversation, null);
	}
	
	public static String createDeletingConversation(){	
		return constructMessage( Message.PROTOCOL_MESSAGE, pendingConversation, null);
	}
	
	public static String createTradeName(String name){	
		return constructMessage( Message.PROTOCOL_MESSAGE, tradeName, new String[]{ name });
	}
	
	public static String createTradeSex(String sex){	
		return constructMessage( Message.PROTOCOL_MESSAGE, tradeSex, new String[]{ sex });
	}
	
	public static String createTradeAge(String age){	
		return constructMessage( Message.PROTOCOL_MESSAGE, tradeAge, new String[]{ age });
	}
	
	public static String createTradeLocation(String locx, String locy){	
		return constructMessage( Message.PROTOCOL_MESSAGE, tradeLocation, new String[]{ locx, locy });
	}
	
}
