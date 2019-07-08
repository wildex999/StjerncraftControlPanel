package com.stjerncraft.controlpanel.agent;

import com.stjerncraft.controlpanel.api.IClient;

public interface IRemoteClient extends IClient {	
	String getUuid();
	
	/**
	 * Send a message to the remote client
	 * @param msg
	 */
	void sendMessage(String msg);
	
	/**
	 * Disconnect the client, ending any existing sessions.
	 * This must be thread safe, as it can be called from a worker thread.
	 */
	void disconnect(String reason);
	
	/**
	 * Whether this Client is connected.
	 * If this is false, any message received and sent should be ignored.
	 * This must be thread safe.
	 * @return
	 */
	boolean isConnected();
}
