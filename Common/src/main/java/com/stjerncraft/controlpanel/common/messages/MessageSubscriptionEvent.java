package com.stjerncraft.controlpanel.common.messages;

/**
 * Event on an existing Subscription
 */
public class MessageSubscriptionEvent extends Message {
	//Session which the CallMethod was called for
	public int sessionId;
	
	//The assigned Subscription ID
	public int subscriptionId;
	
	public String valueJson;
	
	
	public MessageSubscriptionEvent() {}
	
	public MessageSubscriptionEvent(int sessionId, int subscriptionId, String valueJson) {
		this.sessionId = sessionId;
		this.subscriptionId = subscriptionId;
		this.valueJson = valueJson;
	}
}
