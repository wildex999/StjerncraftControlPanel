package com.stjerncraft.controlpanel.agent;

public interface IRemoteClient {
	String getUuid();
	
	/**
	 * Send a message to the remote client
	 * @param msg
	 */
	void sendMessage(String msg);
	
	/**
	 * Disconnect the client, ending any existing sessions.
	 */
	void disconnect(String reason);
}
