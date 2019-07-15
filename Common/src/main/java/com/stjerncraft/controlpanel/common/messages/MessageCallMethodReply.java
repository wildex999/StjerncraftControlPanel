package com.stjerncraft.controlpanel.common.messages;

/**
 * Reply to a previous MessageCallMethod
 */
public class MessageCallMethodReply extends Message {
	//Session which the CallMethod was called for
	public int sessionId;
	
	//ID provided by the Client to the original CallMethod. This value should allow the client to uniquely identify which call the reply is for.
	public int callId;
	
	//A JSON array containing the return value
	public String returnJson;
	
	public MessageCallMethodReply() {}
	
	public MessageCallMethodReply(int sessionId, int callId, String returnJson) {
		this.sessionId = sessionId;
		this.callId = callId;
		this.returnJson = returnJson;
	}
}
