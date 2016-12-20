package com.stjerncraft.controlpanel.api;

/**
 * The ServiceProvider implementation is responsible for handling Service API calls and events.
 * The Service API calls for each given object instance are single-threaded. 
 * You can get information about the one calling the method at any given time by asking the
 * ServiceManager given during registerService()
 */

public interface IServiceProvider {

	/**
	 * Called when the ServiceProvider is registered to the given Control Panel.
	 */
	public void registerService(IServiceManager manager);
	
	/**
	 * Call when the ServiceProvider is removed from the given core.
	 * Note: The manager is no longer valid at this point!
	 */
	public void unregisterService();
}
