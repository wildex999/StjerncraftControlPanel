package com.stjerncraft.controlpanel.common.messages;

/**
 * Sent to the Client as a reply to a StartSession message.
 * This will contain whether the session was accepted, and the session ID.
 */
public class MessageSessionState extends Message {

	//The ID the client sent with the StartSession message
	public int requestId;
	
	//Unique session ID, assigned by the Core
	public int sessionId;
	public boolean accepted;
	
	public MessageSessionState() {}
	
	public MessageSessionState(int requestId, int sessionId, boolean accepted) {
		this.requestId = requestId;
		this.sessionId = sessionId;
		this.accepted = accepted;
	}
	
}
