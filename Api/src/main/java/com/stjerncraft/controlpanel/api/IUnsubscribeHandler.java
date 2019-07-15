package com.stjerncraft.controlpanel.api;

/**
 * Unsubscribe Handler returned by the Service Provider for every Subscription.
 * When a Subscription is ended, this is called to inform the Service Provider about it.
 */
public interface IUnsubscribeHandler {
	void onUnsubscribe(IEventSubscription subscription);
}
