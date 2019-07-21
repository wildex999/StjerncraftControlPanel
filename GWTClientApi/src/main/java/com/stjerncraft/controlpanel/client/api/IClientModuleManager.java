package com.stjerncraft.controlpanel.client.api;

import com.stjerncraft.controlpanel.client.api.module.IClientModule;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IClientModuleManager {
	
	/**
	 * Register a loaded Module, making it available for activation.
	 * The module is expected to call this once it's done loading.
	 * @param module
	 */
	void registerModule(IClientModule module);
}
