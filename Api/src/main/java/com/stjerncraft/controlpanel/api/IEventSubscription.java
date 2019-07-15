package com.stjerncraft.controlpanel.api;

import java.util.function.Function;

/**
 * Event Subscription used by the Agent and Service Provider, providing the ability to send event data, and end the subscription.
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
	 * Serializer takes in the data of a type specified for the EventHandler, and serializes it into a JSON array containing the value.
	 * This is set by the Generated API class when the Client subscribes.
	 * @param serializer
	 */
	public void setDataSerializer(Function<Object, String> serializer);
	
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
