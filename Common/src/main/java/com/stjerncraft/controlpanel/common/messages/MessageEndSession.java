package com.stjerncraft.controlpanel.common.messages;

/**
 * Sent by either the Client, Agent or Core to end an existing session.
 */
public class MessageEndSession extends Message {

	public int sessionId; //ID received from SessionState
	public String reason; //Optional reason for the session ending.

	public MessageEndSession() {}
	
	public MessageEndSession(int sessionId, String reason) {
		this.sessionId = sessionId;
		this.reason = reason;
	}
}
