package com.stjerncraft.controlpanel.common.messages;

/**
 * Sent by the Core to the client after StartSession, indicating whether the StartSession was accepted,
 * and containing a SessionId for further communication.
 * Note: At this point the session might or might not have started(Started by the remote Agent).
 */
public class MessageSessionAccepted extends Message {
	//The ID the client sent with the StartSession message, this is only set on the first SessionState!
	public int requestId;
	
	//The Session ID assigned to this session.
	public int sessionId;
	
	//Whether the session was accepted by the core. If this is false this session is no longer valid.
	public boolean accepted;
	
	public MessageSessionAccepted() {}
	
	public MessageSessionAccepted(int requestId, int sessionId, boolean accepted) {
		this.requestId = requestId;
		this.sessionId = sessionId;
		this.accepted = accepted;
	}
}
