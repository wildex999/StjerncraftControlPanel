package com.stjerncraft.controlpanel.api.client;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface ISession {
	IServiceApiInfo getApi();
	IServiceProviderInfo getServiceProvider();
	int getSessionId();
	
	/**
	 * Whether this Session is currently in a valid usable state
	 * @return
	 */
	boolean isValid();
}
