package com.stjerncraft.controlpanel.core.module;

import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.api.annotation.EventHandler;
import com.stjerncraft.controlpanel.api.annotation.ServiceApi;

@ServiceApi(version=1)
public interface ServiceModuleManager extends IServiceProvider {
	/**
	 * Return a list of all modules.
	 * Note: Will only list modules which the user has permission to view.
	 * @return
	 */
	public Module[] getAllModules();
	
	/**
	 * Return a list of all active modules.
	 * Note: Will only list modules which the user has permission to view.
	 * @return
	 */
	public Module[] getActiveModules();
	
	/**
	 * Activate a module.
	 * @param module The module to activate.
	 * @return False if the module was not enabled(Including if it was already activated)
	 */
	public boolean activateModule(Module module);
	
	/**
	 * Activate modules.
	 * @param modules The list of modules to activate.
	 * @return A list of modules which were NOT activated.
	 */
	public Module[] activateModules(Module[] modules);
	
	/**
	 * Deactivate module
	 * @param module
	 * @return False if the module was not removed(Including if it was not activated to begin with)
	 */
	public boolean deactivateModule(Module module);
	public Module[] deactivateModules(Module[] modules);
	
	/**
	 * Listen for Module events, like Modules being added, activated, removed etc.
	 * Note: Event will only be sent to user if the user has the permissions to see the Module.
	 */
	@EventHandler(eventType=ModuleEvent.class)
	public void listenForModuleEvents();
}
