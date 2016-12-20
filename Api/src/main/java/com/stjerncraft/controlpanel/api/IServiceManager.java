package com.stjerncraft.controlpanel.api;

import java.util.List;

/**
 * Responsible for managing the Service API implementations, forwarding the method calls, events etc.
 * Note: The service manager should return proxied implementations of the Service Providers, as getClient() is expected to
 * return the calling client.(Or should all local calls come from the "local" client?)
 */

public interface IServiceManager {
	/**
	 * The client doing the current method call/event subscription etc.
	 * @return
	 */
	public IClient getClient();
	
	/**
	 * Get the Service Provider for the given Service API
	 * @param apiInterfaceClass The Service API for which you want the Service Provider 
	 * @returnA Service Provider implementing the given Service API, or null if none is registered.
	 */
	public <T extends IServiceProvider> T getService(Class<T> apiInterfaceClass);
	
	/**
	 * Get all Service Providers of the given Service API
	 * @param apiInterfaceClass The Service API for which you want the Service Provider
	 * @return A list of all Service Providers implementing the given Service API.
	 */
	public <T extends IServiceProvider> List<T> getServices(Class<T> apiInterfaceClass);
}
