package com.stjerncraft.controlpanel.api;

/**
 * Event Subscription stored by the Agent, providing the ability to send event data, and end the subscription.
 */
public interface IEventSubscription {
	/**
	 * Get the Client with this subscription
	 * @return
	 */
	public IClient getClient();
	
	/**
	 * End this subscription
	 */
	public void end();
	
	/**
	 * Send an event to the subscribed user.
	 * @param data The DataObject to send. Should not be modified after passing to sendEvent!
	 */
	public void sendEvent(Object data);
	
	/**
	 * Session unique identifier for this Event Subscription.
	 * NOTE: In the case of rejected subscriptions, this can be re-used.
	 * @return
	 */
	public int getSubscriptionId();
}
