package com.stjerncraft.controlpanel.client.api;

import java.util.List;

import com.stjerncraft.controlpanel.api.client.IServiceApiInfo;
import com.stjerncraft.controlpanel.api.client.IServiceProviderInfo;
import com.stjerncraft.controlpanel.common.data.IAgentInfo;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IClientServiceManager {
	
	void addEventHandler(IServiceManagerEventHandler handler);
	void removeEventHandler(IServiceManagerEventHandler handler);
	
	/**
	 * Get a list of all Service Providers which implements the given API
	 * @param api
	 * @return
	 */
	List<IServiceProviderInfo> getProvidersForApi(IServiceApiInfo api);
	
	/**
	 * Get a list of all Service PRoviders which implements the given API on the specified Agent.
	 * @param api
	 * @param agent
	 * @return
	 */
	List<IServiceProviderInfo> getProvidersForApiOnAgent(IServiceApiInfo api, IAgentInfo agent);
	
	/**
	 * Get the best Service Provider which implements the given API.
	 * This is usually chosen by Service Provider priority, though it can be overridden on a per-api basis
	 * @param api
	 * @return
	 */
	IServiceProviderInfo getBestProviderForApi(IServiceApiInfo api);
	
	/**
	 * Get the best Service Provider which implements the given API on the specified Agent.
	 * This is usually chosen by Service Provider priority, though it can be overridden on a per-api basis
	 * @param api
	 * @param agent
	 * @return
	 */
	IServiceProviderInfo getBestProviderForApiOnAgent(IServiceApiInfo api, IAgentInfo agent);
}
