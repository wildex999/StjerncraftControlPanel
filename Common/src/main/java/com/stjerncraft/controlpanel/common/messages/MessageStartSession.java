package com.stjerncraft.controlpanel.common.messages;

/**
 * Sent to the Core to initialize a session with a Service Provider.
 * The Core will assign a unique session ID, and forward the request to the Agent.
 * 
 * When sent to the Agent, the requestId will contain the assigned Session ID.
 * The agent is expected to reply with a EndSession message if it does not accept the session,
 * otherwise it is implied that the session is always accepted.
 */
public class MessageStartSession extends Message {
	
	//The requestId is decided by the sender, and should be used when answering with a SessionState message.
	public int requestId;
	
	public String apiId;
	public String serviceProviderId;

}
