package com.stjerncraft.controlpanel.common.messages;

import com.stjerncraft.controlpanel.api.IServiceProvider;

/**
 * Sent to the Core to initialize a session with a Service Provider.
 * The Core will assign a unique session ID, and forward the request to the Agent.
 * 
 * The Agent is expected to reply with a SessionAccepted message indicating whether the Session was accepted.
 */
public class MessageStartSession extends Message {
	
	//The requestId is decided by the sender, and should be used when answering with a SessionState message.
	public int requestId;
	
	//Assigned by the Core before forwarding to the Agent, Client should leave this empty.
	public int sessionId;
	
	//API and Provider to start a Session with
	public String apiId;
	public String serviceProviderId;
	
	public MessageStartSession() {}
	
	public MessageStartSession(int requestId, Class<IServiceProvider> api, String serviceProviderId) {
		this.requestId = requestId;
		this.apiId = "";
		this.serviceProviderId = serviceProviderId;
	}

}
