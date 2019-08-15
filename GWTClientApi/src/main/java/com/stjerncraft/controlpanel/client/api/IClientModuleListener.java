package com.stjerncraft.controlpanel.client.api;

import com.stjerncraft.controlpanel.client.api.module.IClientModule;

/**
 * Listener for Modules being Loaded, Activated and Deactivated
 */
public interface IClientModuleListener {
	void onModuleLoading(String module);
	void onModuleLoaded(IClientModule module);
	void onModuleActivated(IClientModule module);
	void onModuleDeactivated(IClientModule module);
}
