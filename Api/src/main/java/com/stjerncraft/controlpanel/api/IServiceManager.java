package com.stjerncraft.controlpanel.api;

import java.util.List;

/**
 * Responsible for managing the Service API implementations, forwarding the method calls, events etc.
 * 
 * The Service Manager will also provide context to the Service Provider, like which client is doing the method call.
 */

public interface IServiceManager {
	/**
	 * The client doing the current method call/event subscription etc.
	 * This will be null for all direct method calls internally.
	 */
	public IClient getClient();
	
	/**
	 * Get the event subscription for the current event subscription.
	 * This will only return a non-null value when called inside a method tagged with EventHandler.
	 */
	public IEventSubscription getEventSubscription();
	
	/**
	 * Get the event action for the current event subscription.
	 * This will only return a non-null value when called inside a method tagged with EventHandler.
	 */
	public EventAction getEventAction();
	
	/**
	 * Get the Service Provider for the given Service API
	 * @param apiInterfaceClass The Service API for which you want the Service Provider 
	 * @return A Service Provider implementing the given Service API, or null if none is registered.
	 */
	public <T extends IServiceProvider> T getService(Class<T> apiInterfaceClass);
	
	/**
	 * Get all Service Providers of the given Service API
	 * @param apiInterfaceClass The Service API for which you want the Service Provider
	 * @return A list of all Service Providers implementing the given Service API.
	 */
	public <T extends IServiceProvider> List<T> getServices(Class<T> apiInterfaceClass);
}
