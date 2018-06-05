package com.stjerncraft.controlpanel.core;

import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.api.annotation.ServiceApi;

/**
 * The Core Service Provider, acting as the primary way of communication between a Client and the Core.
 * All Clients will start a session with this Service Provider when first starting up.
 */
@ServiceApi(version=1)
public interface ServiceCore extends IServiceProvider {
	/**
	 * Get a list of all known Service APIs
	 */
	public void getAllServiceApis();
	
	/**
	 * Get all known versions of the named Service API
	 * @param name
	 */
	public void getServiceApis(String name);
	
	/**
	 * Get the specified Service API
	 * @param name
	 * @param version
	 */
	public void getServiceApi(String name, String version);
	
	/**
	 * Get all Service Providers providing the given Service API
	 * @param apiId
	 */
	public void getServiceProviders(String apiId);
	
	
	//public void getServiceProvider(String apiId, String serviceProviderId);
	
	/**
	 * Get all Agents registered.
	 */
	public void getAgents();
}
