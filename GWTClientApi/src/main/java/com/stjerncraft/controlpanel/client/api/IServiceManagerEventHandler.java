package com.stjerncraft.controlpanel.client.api;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IServiceManagerEventHandler {
	void onFullUpdate(IClientServiceManager serviceManager);
}
