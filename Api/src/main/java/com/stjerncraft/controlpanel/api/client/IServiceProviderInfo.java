package com.stjerncraft.controlpanel.api.client;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IServiceProviderInfo {
	String getUuid();
	String getAgentUuid();
	
	/**
	 * This Service Provider's priority.
	 * Used when Client is deciding which Service Provider to use for an API
	 * @return
	 */
	ServiceProviderPriority getPriority();
	
	/**
	 * List of API's implemented by this Service Provider
	 * @return
	 */
	IServiceApiInfo[] getApis();
}
