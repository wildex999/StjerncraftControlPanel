package com.stjerncraft.controlpanel.api;

import java.time.LocalDateTime;

/**
 * Information about a Client
 */
public interface IClient {
	public IUser getUser();
	
	//The Date this client connected to the server
	public LocalDateTime getDateConnected();
	
	//The client defined agent name. For example "WebClient 1.2"
	public String getAgent();
}
