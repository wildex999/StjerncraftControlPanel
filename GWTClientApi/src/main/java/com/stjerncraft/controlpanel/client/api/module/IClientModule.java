package com.stjerncraft.controlpanel.client.api.module;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IClientModule {
	String getName();
	
	void onActivate();
	void onDeactivate();
}
