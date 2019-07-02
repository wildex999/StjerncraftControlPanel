package com.stjerncraft.controlpanel.api;

/**
 * The ServiceProvider implementation is responsible for handling Service API calls and events.
 * The Service API calls for each given object instance are single-threaded. 
 * You can get information about the one calling the method at any given time by asking the
 * ServiceManager given during registerService()
 */

public interface IServiceProvider {
	
	/**
	 * Called when the ServiceProvider is registered to the Agent.
	 * @param manager The manager for the Service Manager to get the current user and to access other services.
	 */
	public void onRegister(IServiceManager manager);
	
	/**
	 * Call when the ServiceProvider is removed from the Agent.
	 * Note: The manager is no longer valid at this point!
	 */
	public void onUnregister();
}
