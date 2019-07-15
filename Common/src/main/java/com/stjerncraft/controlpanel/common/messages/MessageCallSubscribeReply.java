package com.stjerncraft.controlpanel.common.messages;

/**
 * Reply to a previous MessageCallSubscribe message.
 * 
 * This will contain the Subscription ID and whether the Subscription was accepted
 */
public class MessageCallSubscribeReply extends Message {
	//Session which the CallMethod was called for
	public int sessionId;
	
	//ID provided by the Client to the original CallMethod. This value should allow the client to uniquely identify which call the reply is for.
	public int callId;
	
	//Whether the Subscription was accepted. If this is false then the subscriptionId is invalid.
	public boolean accepted;
	
	//The assigned Subscription ID
	public int subscriptionId;
	
	
	public MessageCallSubscribeReply() {}
	
	public MessageCallSubscribeReply(int sessionId, int callId, boolean accepted, int subscriptionId) {
		this.sessionId = sessionId;
		this.callId = callId;
		this.accepted = accepted;
		this.subscriptionId = subscriptionId;
	}
}
