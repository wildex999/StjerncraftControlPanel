package com.stjerncraft.controlpanel.client.api;

import com.stjerncraft.controlpanel.client.api.module.IClientModule;

import jsinterop.annotations.JsType;

/**
 * Listener for Modules being Loaded, Activated and Deactivated
 */
@JsType(isNative=true)
public interface IClientModuleListener {
	void onModuleLoading(String module);
	void onModuleLoaded(IClientModule module);
	void onModuleActivated(IClientModule module);
	void onModuleDeactivated(IClientModule module);
}
