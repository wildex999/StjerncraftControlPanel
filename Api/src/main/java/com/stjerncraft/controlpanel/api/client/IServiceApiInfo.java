package com.stjerncraft.controlpanel.api.client;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IServiceApiInfo {
	String getName();
	int getVersion();
	
	/**
	 * Get the ID uniquely identifying this API(Name + Version)
	 * @return
	 */
	String getId();
}
