package com.servlet.utilities;

public enum UrlAddress {
	ROUTER1("192.168.1.101", "8888"),
	ROUTER2("192.168.1.102", "8888"),
	ROUTER3("192.168.1.103", "8888");
	
	public String url;
	public String port;
	
	UrlAddress(String u, String p){
		this.url = u;
		this.port = p;
	}
	
}
