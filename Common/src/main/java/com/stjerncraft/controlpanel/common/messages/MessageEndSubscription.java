package com.stjerncraft.controlpanel.common.messages;

/**
 * Sent by either the Client or Agent to end an existing Subscription
 */
public class MessageEndSubscription extends Message {
	public int sessionId; //ID received from SessionState
	public int subscriptionId; //ID of the Subscription to end

	public MessageEndSubscription() {}
	
	public MessageEndSubscription(int sessionId, int subscriptionId) {
		this.sessionId = sessionId;
		this.subscriptionId = subscriptionId;
	}
}
