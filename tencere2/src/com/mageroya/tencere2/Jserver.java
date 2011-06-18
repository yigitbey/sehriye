package com.mageroya.tencere2;


import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;

public class JServer extends XMPPConnection{
    public static String host = "mageroya.com";
    public static String port = "5222";
    public static String service = "mageroya.com";

    public Jserver(ConnectionConfiguration connConfig){
	super(connConfig);
    }
}


