package com.android.tencere.model.protocol;

public class Message {

	public static final byte PROTOCOL_MESSAGE = 0;
	public static final byte CHAT_MESSAGE = 1;
	public static final byte ERROR_MESSAGE = -1;
	
	
	private String identifier;
	private String paramsSplitter;
	private String messageBody;
	private String[] params; 
	private byte messageType;
	
	public Message(byte type){
		this.setMessageType(type);
		this.setIdentifier("");
		this.setMessageBody("");
		this.setParams(null);
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getIdentifier() {
		return identifier;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	public String getMessageBody() {
		return this.messageBody;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	public String[] getParams() {
		return params;
	}

	public void setMessageType(byte messageType) {
		this.messageType = messageType;
	}

	public byte getMessageType() {
		return messageType;
	}
	
	public void setParamsSplitter(String paramsSplitter) {
		this.paramsSplitter = paramsSplitter;
	}

	public String getParamsSplitter() {
		return paramsSplitter;
	}
	
	public String getMessage(){
		 String message = this.identifier + this.messageBody;
		 if( this.params != null ){
			 for (int i = 0; i < this.params.length; i++)
				 message += this.getParamsSplitter() + this.params[i];
		 }
		 return message;
	}



	
}
