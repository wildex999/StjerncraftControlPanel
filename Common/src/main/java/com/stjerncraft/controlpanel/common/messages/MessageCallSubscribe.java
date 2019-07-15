package com.stjerncraft.controlpanel.common.messages;

/**
 * Sent from the Client to the Agent through the Core, to subscribe to an Event Handler.
 * 
 * The Client will receive a MessageCallSubscribeReply message in reply from the Agent.
 * The Message MessageSubscriptionEvent will be sent for every event on the Subscription.
 */
public class MessageCallSubscribe extends Message {
	//Session with Service Provider this call is targeted at
	public int sessionId;
	
	//An Client Defined ID which will be used for the reply. This value should allow the client to uniquely identify which call the reply is for.
	public int callId;
	
	//A JSON object containing the method name and parameters.
	public String methodJson;
	
	public MessageCallSubscribe() {}
	
	public MessageCallSubscribe(int sessionId, int callId, String methodJson) {
		this.sessionId = sessionId;
		this.callId = callId;
		this.methodJson = methodJson;
	}
}
