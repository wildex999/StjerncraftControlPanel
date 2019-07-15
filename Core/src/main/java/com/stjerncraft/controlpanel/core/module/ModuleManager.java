package com.stjerncraft.controlpanel.core.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stjerncraft.controlpanel.api.IClient;
import com.stjerncraft.controlpanel.api.IEventSubscription;
import com.stjerncraft.controlpanel.api.IServiceManager;
import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.api.IUnsubscribeHandler;
import com.stjerncraft.controlpanel.common.ModuleEvent;
import com.stjerncraft.controlpanel.common.ModuleEvent.Action;
import com.stjerncraft.controlpanel.common.ModuleInfo;
import com.stjerncraft.controlpanel.common.api.ModuleManagerApi;

public class ModuleManager implements ModuleManagerApi, IServiceProvider  {

	private ModuleManagerConfig config;
	private Map<String, Module> modules;
	private Map<String, Module> activeModules;
	
	private Map<Integer, IEventSubscription> subscriptions;
	
	IServiceManager serviceManager;
	
	public ModuleManager() {
		modules = new HashMap<>();
		activeModules = new HashMap<>();
		subscriptions = new HashMap<>();
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
		IClient client = serviceManager.getClient();
		if(client != null && client.getUser() == null)
			return false;
		
		//TODO: Check if user has permission to activate the module
		
		String id = module.getId();
		Module storedModule = modules.get(id);
		if(storedModule == null)
			return false;
		if(activeModules.containsKey(id))
			return false;
		
		activeModules.put(id, storedModule);
		
		ModuleEvent event = new ModuleEvent(Action.Activated, module);
		broadcastEvent(event);
		
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
		IClient client = serviceManager.getClient();
		if(client != null && client.getUser() == null)
			return false;
		
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
	public IUnsubscribeHandler listenForModuleEvents() {
		IEventSubscription newSubscription = serviceManager.getEventSubscription();
		subscriptions.put(newSubscription.getSubscriptionId(), newSubscription);
		
		return subscription -> {
			subscriptions.remove(subscription.getSubscriptionId());
		};
	}
	
	/**
	 * Broadcast an event to all current subscriptions
	 * @param event
	 */
	private void broadcastEvent(ModuleEvent event) {
		for(IEventSubscription subscription : subscriptions.values()) {
			//TODO: Verify that subscription user has permission to "see" this Module(And thus any events involving it)
			subscription.sendEvent(event);
		}
	}

}
