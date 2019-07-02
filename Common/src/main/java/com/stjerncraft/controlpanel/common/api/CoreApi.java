package com.stjerncraft.controlpanel.common.api;

import com.stjerncraft.controlpanel.api.annotation.ServiceApi;
import com.stjerncraft.controlpanel.common.data.AgentInfo;
import com.stjerncraft.controlpanel.common.data.ServiceApiInfo;
import com.stjerncraft.controlpanel.common.data.ServiceProviderInfo;


/**
 * Primary communication API between the Client and the Core.
 * This is the starting point for getting to other API's and Service Providers
 */
@ServiceApi(version=1)
public interface CoreApi {
	/**
	 * Get a list of all known Service APIs
	 */
	public ServiceApiInfo[] getAllServiceApis();
	
	/**
	 * Get all known versions of the named Service API
	 * @param name
	 */
	public ServiceApiInfo[] getServiceApis(String name);
	
	/**
	 * Get the specified Service API
	 * @param name
	 * @param version
	 * @return Null if not found.
	 */
	public ServiceApiInfo getServiceApi(String name, int version);
	
	/**
	 * Get all Service Providers providing the given Service API
	 * @param apiId
	 */
	public ServiceProviderInfo[] getServiceProviders(String apiId);
	
	/**
	 * Get information about the Service Provider with the given UUID.
	 * @param uuid
	 * @return Null if not found.
	 */
	public ServiceProviderInfo getServiceProvider(String uuid);
	
	/**
	 * Get all Agents registered.
	 */
	public AgentInfo[] getAgents();
	
	/**
	 * Get information about the Agent with the given UUID.
	 * @param uuid
	 * @return
	 */
	public AgentInfo getAgent(String uuid);
}
