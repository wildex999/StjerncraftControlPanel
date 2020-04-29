package com.stjerncraft.controlpanel.core.module;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stjerncraft.controlpanel.api.IClient;
import com.stjerncraft.controlpanel.api.IEventSubscription;
import com.stjerncraft.controlpanel.api.IServiceManager;
import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.api.IUnsubscribeHandler;
import com.stjerncraft.controlpanel.common.ModuleEvent;
import com.stjerncraft.controlpanel.common.ModuleEvent.Action;
import com.stjerncraft.controlpanel.common.api.ModuleManagerApi;
import com.stjerncraft.controlpanel.common.data.ModuleInfo;

public class ModuleManager implements ModuleManagerApi, IServiceProvider  {
	private static final Logger logger = LoggerFactory.getLogger(ModuleManager.class);
	
	
	private ModuleManagerConfig config;
	private Map<String, Module> modules; //Name -> Module
	private Map<String, Module> activeModules; //Name -> Module
	
	private Map<Integer, IEventSubscription> subscriptions; //SubscriptionId -> Subscription
	
	IServiceManager serviceManager;
	
	public ModuleManager() {
		//Getting the Modules must be Thread Safe
		modules = new ConcurrentHashMap<>();
		
		activeModules = new HashMap<>();
		subscriptions = new HashMap<>();
	}
	
	/**
	 * Load the modules, using the given config which contains the list of active modules
	 * @param config A pre-filled config
	 * @throws IOException 
	 */
	public void loadModules(ModuleManagerConfig config) throws IOException {
		this.config = config;
		
		//TODO: Don't Remove Modules which are still here, but instead mark them as updated
		
		//Inform subscriptions about clearing existing modules
		broadcastEvent(new ModuleEvent(Action.Deactivated, getActiveModules()));
		broadcastEvent(new ModuleEvent(Action.Removed, getAllModules()));
		
		modules.clear();
		activeModules.clear();
		
		//Load modules from the given location
		Files.list(new File("modules").toPath()).forEach(path -> {
			File file = path.toFile();
			if(!file.isDirectory())
				return;
			
			String moduleName = file.getName();
			
			logger.info("Loading Module: " + moduleName);
			
			//Try to get a Config for the module
			File configFile = Paths.get(path.toString(), "config.json").toFile();
			if(!configFile.exists()) {
				logger.error("No config file found at " + configFile + "!");
				return;
			}
			
			Module newModule = new Module(moduleName);
			try {
				newModule.load(path);
			} catch (IOException | ModuleConfigLoadException e) {
				logger.error("Failed to load module " + moduleName + ": " + e);
				return;
			}
			
			//Verify the existence of the Module javascript module.nocache.js
			File sourceFile = newModule.getSourceFile().toFile();
			if(!sourceFile.exists()) {
				logger.error("No main file found for " + moduleName + ". Looking for " + sourceFile.toPath().toString());
				return;
			}
			
			modules.put(newModule.getName(), newModule);
			logger.info("Loaded Module: " + newModule.descriptiveName);
		});
		
		//Make list of all active modules
		for(Module module : modules.values()) {
			if(config.active.contains(module.getName())) {
				logger.info("Activating Module: " + module.descriptiveName);
				activeModules.put(module.getName(), module);
			}
		}
		
		
		//Send updated list to Subscriptions
		broadcastEvent(new ModuleEvent(Action.Added, getAllModules()));
		broadcastEvent(new ModuleEvent(Action.Activated, getActiveModules()));
	}
	
	//Check for removed and added modules in location
	public void refreshModules() {
		if(config == null)
			return;
		
		//TODO
	}
	
	/**
	 * This must be Thread-Safe
	 * @param name
	 * @return
	 */
	@Override
	public Module getModule(String name) {
		Module module = modules.get(name);
		return module;
	}
	
	@Override
	public void onRegister(IServiceManager manager) {
		serviceManager = manager;
	}

	@Override
	public void onUnregister() {
		serviceManager = null;
	}

	/**
	 * This must be Thread-Safe
	 */
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
		
		String name = module.getName();
		Module storedModule = modules.get(name);
		if(storedModule == null)
			return false;
		if(activeModules.containsKey(name))
			return false;
		
		activeModules.put(name, storedModule);
		
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
		return activeModules.remove(module.getName()) != null;
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
			//TODO: Remove Modules which the given user is not allowed to see.
			subscription.sendEvent(event);
		}
	}

}
