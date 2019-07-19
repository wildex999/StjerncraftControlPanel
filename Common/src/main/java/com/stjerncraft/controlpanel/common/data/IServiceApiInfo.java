package com.stjerncraft.controlpanel.common.data;

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
