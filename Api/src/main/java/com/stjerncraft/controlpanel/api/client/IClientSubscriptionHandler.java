package com.stjerncraft.controlpanel.api.client;

import jsinterop.annotations.JsType;

/**
 * A Client side handler for a given Subscription.
 * Handles the events for the Subscription from the Service Provider.
 */
@JsType(isNative=true)
public interface IClientSubscriptionHandler<T> {
	
	/**
	 * Called whenever there is an event from the Service Provider on the Subscription.
	 * @param subscriptionId The ID of the Subscription
	 * @param value The event value sent by the Service Provider
	 */
	void OnEvent(int subscriptionId, T value);
	 
	/**
	 * Called with the result of an initial Subscription Request.
	 * @param subscriptionId The ID given for this new Subscription.
	 * @param callId The ID returned on the callSubscribe call for associating a callSubscribe with its result.
	 * @param success True if the Subscription was a success. If false there will never be an event, and no OnUnsubscribed call.
	 */
	void OnSubscribed(int subscriptionId, int callId, boolean success);
	
	/**
	 * Called when an Active Subscription is stopped. Either by the Client, the Service Provider, or because the Session ended.
	 * @param subscriptionId The ID of the Subscription.
	 */
	void OnUnsubscribed(int subscriptionId);
}
