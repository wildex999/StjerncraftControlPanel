package com.stjerncraft.controlpanel.common.messages;

/**
 * Sent from the Client to the Agent through the Core, to call a method on a Service Provider.
 * 
 * The Client will receive a MessageCallMethodReply message in reply from the Agent.
 */
public class MessageCallMethod extends Message {
	//Session with Service Provider this call is targeted at
	public int sessionId;
	
	//An Client Defined ID which will be used for the reply. This value should allow the client to uniquely identify which call the reply is for.
	public int callId;
	
	//A JSON object containing the method name and parameters.
	public String methodJson;
	
	public MessageCallMethod() {}
	
	public MessageCallMethod(int sessionId, int callId, String methodJson) {
		this.sessionId = sessionId;
		this.callId = callId;
		this.methodJson = methodJson;
	}

}
