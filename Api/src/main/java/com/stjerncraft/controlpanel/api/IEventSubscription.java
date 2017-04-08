package com.stjerncraft.controlpanel.api;

public interface IEventSubscription {
	/**
	 * Unsubscribe the client from the event
	 */
	public void unsubscribe();
	
	/**
	 * Send an event to the subscribed user
	 */
	public void sendEvent();
}
