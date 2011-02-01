package com.android.tencere.model.server;

public abstract class Server {
	
    protected String host;
    protected String port; 
    protected String service;
    protected String username;
    protected String password;


	protected void setHost(String Host) {
		this.host = Host;
		
	}

	protected void setPort(String Port) {
		this.port = Port;
		
	}


	protected void setService(String Service) {
		this.service = Service;
		
	}

	protected void setUserName(String Username) {
		this.username = Username;
		
	}

	protected void setPassword(String Password) {
		this.password = Password;
		
	}


	public String getHost() {
		return this.host;
	}


	public String getPort() {
		return this.port;
	}


	public String getService() {
		return this.service;
	}


	public String getUserName() {
		return this.username;
	}


	public String getPassword() {
		return this.password;
	}


}
