package com.stjerncraft.controlpanel.client.api.webview.socket;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface ISocketDefinition<T extends ISocketContext> {
	/**
	 * Get the name of this Socket Definition
	 */
	String getName();
	
	void addInstance(ISocketInstance<T> instance);
}
