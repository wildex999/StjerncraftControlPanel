package com.stjerncraft.controlpanel.core.module;

import java.util.ArrayList;
import java.util.List;

import com.stjerncraft.controlpanel.api.IServiceManager;

public class ModuleManager implements ServiceModuleManager  {

	private ModuleManagerConfig config;
	private List<Module> modules;
	private List<Module> activeModules;
	
	IServiceManager serviceManager;
	
	public ModuleManager() {
		modules = new ArrayList<>();
		activeModules = new ArrayList<>();
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
	
	//Check for removed and added modules
	public void refreshModules() {
		
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
	public Module[] getAllModules() {
		//TODO: Check permission of user, and filter the list
		return modules.toArray(new Module[modules.size()]);
	}

	@Override
	public Module[] getActiveModules() {
		//TODO: Check permission of user, and filter the list
		return activeModules.toArray(new Module[activeModules.size()]);
	}

	@Override
	public boolean activateModule(Module module) {
		//TODO: Check if user has permission to activate the module
		if(activeModules.contains(module))
			return false;
		
		activeModules.add(module);
		
		return true;
	}

	@Override
	public Module[] activateModules(Module[] modules) {
		List<Module> failed = new ArrayList<>();
		
		for(Module module : modules) {
			if(!activateModule(module))
				failed.add(module);
		}
		
		return failed.toArray(new Module[failed.size()]);
	}

	@Override
	public boolean deactivateModule(Module module) {
		//TODO: Check if the user has permission to deactivate the module
		return activeModules.remove(module);
	}

	@Override
	public Module[] deactivateModules(Module[] modules) {
		List<Module> failed = new ArrayList<>();
		
		for(Module module : modules) {
			if(!deactivateModule(module))
				failed.add(module);
		}
		
		return failed.toArray(new Module[failed.size()]);
	}

	@Override
	public void listenForModuleEvents() {
		// TODO Auto-generated method stub
		
	}

}
