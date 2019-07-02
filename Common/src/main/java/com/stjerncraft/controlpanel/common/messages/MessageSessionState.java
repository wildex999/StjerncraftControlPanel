package com.stjerncraft.controlpanel.common.messages;

/**
 * Sent to the Client through the Core from the Agent.
 * This will contain whether the session has started.
 */
public class MessageSessionState extends Message {	
	//Unique session ID, assigned by the Core
	public int sessionId;
	
	//Set to true once the Agent has started the session.
	public boolean started;
	
	public MessageSessionState() {}
	
	public MessageSessionState(int sessionId, boolean started) {
		this.sessionId = sessionId;
		this.started = started;
	}
	
}
