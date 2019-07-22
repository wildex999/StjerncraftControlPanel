package com.stjerncraft.controlpanel.common.api;

import com.stjerncraft.controlpanel.api.IUnsubscribeHandler;
import com.stjerncraft.controlpanel.api.annotation.EventHandler;
import com.stjerncraft.controlpanel.api.annotation.ServiceApi;
import com.stjerncraft.controlpanel.common.ModuleEvent;
import com.stjerncraft.controlpanel.common.data.ModuleInfo;

@ServiceApi(version=1)
public interface ModuleManagerApi {
	/**
	 * Get the named module if it exists.
	 * @param name
	 * @return Null if no module with that name exists
	 */
	public ModuleInfo getModule(String name);
	
	/**
	 * Return a list of all modules.
	 * Note: Will only list modules which the user has permission to view.
	 * @return
	 */
	public ModuleInfo[] getAllModules();
	
	/**
	 * Return a list of all active modules.
	 * Note: Will only list modules which the user has permission to view.
	 * @return
	 */
	public ModuleInfo[] getActiveModules();
	
	/**
	 * Activate a module.
	 * @param module The module to activate.
	 * @return False if the module was not enabled(Including if it was already activated)
	 */
	public boolean activateModule(ModuleInfo module);
	
	/**
	 * Activate modules.
	 * @param modules The list of modules to activate.
	 * @return A list of modules which were NOT activated.
	 */
	public ModuleInfo[] activateModules(ModuleInfo[] modules);
	
	/**
	 * Deactivate module
	 * @param module
	 * @return False if the module was not removed(Including if it was not activated to begin with)
	 */
	public boolean deactivateModule(ModuleInfo module);
	public ModuleInfo[] deactivateModules(ModuleInfo[] modules);
	
	/**
	 * Listen for Module events, like Modules being added, activated, removed etc.
	 * Note: Event will only ever include modules which a given user has permissions to see.
	 */
	@EventHandler(eventData=ModuleEvent.class)
	public IUnsubscribeHandler listenForModuleEvents();
}
