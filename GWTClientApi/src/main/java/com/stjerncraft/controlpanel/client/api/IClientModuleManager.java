package com.stjerncraft.controlpanel.client.api;

import com.stjerncraft.controlpanel.client.api.module.IClientModule;
import com.stjerncraft.controlpanel.common.data.IModuleInfo;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IClientModuleManager {
	
	/**
	 * Register a loaded Module, making it available for activation.
	 * The module is expected to call this once it's done loading.
	 * @param module
	 * @param return True on success. False if it failed to register, and the Module should unload.
	 */
	boolean registerModule(IClientModule module);
	
	/**
	 * Get a list of all Modules currently loading
	 * @return
	 */
	IModuleInfo[] getLoadingModules();
	
	/**
	 * Get a list of all Loaded modules.
	 * @return
	 */
	IClientModule[] getLoadedModules();
	
	/**
	 * Get a list of all activated modules.
	 * @return
	 */
	IClientModule[] getActiveModules();
	
	/**
	 * Get the named module if it has been loaded on the client
	 * @return The Module, or null if it has not yet been loaded.
	 */
	IClientModule getLoadedModule(String name);
	
	/**
	 * Check if the named module has been loaded and is active.
	 * @param name
	 * @return
	 */
	boolean isModuleActive(String name);
	
	void addModuleListener(IClientModuleListener listener);
	void removeModuleListener(IClientModuleListener listener);
}
