package com.stjerncraft.controlpanel.api;

import java.time.LocalDateTime;

/**
 * Information about a Client
 */
public interface IClient {
	/**
	 * User authorized with this client.
	 * @return Null if not yet authorized(Anonymous).
	 */
	public IUser getUser();
	
	/**
	 * The Date this client connected to the server
	 * @return
	 */
	public LocalDateTime getDateConnected();
	
	/**
	 * The client defined agent name. For example "WebClient 1.2"
	 * @return
	 */
	public String getAgent();
}
