package com.stjerncraft.controlpanel.api.client;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IClientApiLibrary {
	String getApiName();
	int getApiVersion();
	
	/**
	 * Set the Session this Api Library will make calls on
	 * @param session
	 */
	void setSession(ISession session);
}
