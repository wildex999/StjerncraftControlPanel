package com.stjerncraft.controlpanel.core.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stjerncraft.controlpanel.api.IServiceManager;
import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.common.ModuleInfo;
import com.stjerncraft.controlpanel.common.api.ModuleManagerApi;

public class ModuleManager implements ModuleManagerApi, IServiceProvider  {

	private ModuleManagerConfig config;
	private Map<String, Module> modules;
	private Map<String, Module> activeModules;
	
	IServiceManager serviceManager;
	
	public ModuleManager() {
		modules = new HashMap<>();
		activeModules = new HashMap<>();
	}
	
	/**
	 * Load the modules, using the given config which contains the list of active modules
	 * @param config A pre-filled config
	 */
	public void loadModules(ModuleManagerConfig config) {
		this.config = config;
		
		//Load modules from the given location
		
		
		//Make list of all active modules
	}
	
	//Check for removed and added modules in location
	public void refreshModules() {
		if(config == null)
			return;
	}
	
	@Override
	public void onRegister(IServiceManager manager) {
		serviceManager = manager;
	}

	@Override
	public void onUnregister() {
		serviceManager = null;
	}

	@Override
	public ModuleInfo[] getAllModules() {
		//TODO: Check permission of user, and filter the list
		return modules.values().toArray(new ModuleInfo[modules.size()]);
	}

	@Override
	public ModuleInfo[] getActiveModules() {
		//TODO: Check permission of user, and filter the list
		return activeModules.values().toArray(new ModuleInfo[activeModules.size()]);
	}

	@Override
	public boolean activateModule(ModuleInfo module) {
		//TODO: Check if user has permission to activate the module
		String id = module.getId();
		Module storedModule = modules.get(id);
		if(storedModule == null)
			return false;
		if(activeModules.containsKey(id))
			return false;
		
		activeModules.put(id, storedModule);
		
		return true;
	}

	@Override
	public ModuleInfo[] activateModules(ModuleInfo[] modules) {
		List<ModuleInfo> failed = new ArrayList<>();
		
		for(ModuleInfo module : modules) {
			if(!activateModule(module))
				failed.add(module);
		}
		
		return failed.toArray(new ModuleInfo[failed.size()]);
	}

	@Override
	public boolean deactivateModule(ModuleInfo module) {
		//TODO: Check if the user has permission to deactivate the module
		return activeModules.remove(module.getId()) != null;
	}

	@Override
	public ModuleInfo[] deactivateModules(ModuleInfo[] modules) {
		List<ModuleInfo> failed = new ArrayList<>();
		
		for(ModuleInfo module : modules) {
			if(!deactivateModule(module))
				failed.add(module);
		}
		
		return failed.toArray(new ModuleInfo[failed.size()]);
	}

	@Override
	public boolean listenForModuleEvents() {
		// TODO Auto-generated method stub
		return false;
	}

}
