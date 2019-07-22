package com.stjerncraft.controlpanel.client.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
import com.stjerncraft.controlpanel.client.api.IClientModuleManager;
import com.stjerncraft.controlpanel.client.api.module.IClientModule;
import com.stjerncraft.controlpanel.client.api.session.IClientSession;
import com.stjerncraft.controlpanel.client.api.session.ISessionListener;
import com.stjerncraft.controlpanel.common.api.ModuleManagerApiLibrary;
import com.stjerncraft.controlpanel.common.data.ModuleInfo;

import jsinterop.annotations.JsType;

/**
 * Manages the list of modules, and takes care of loading and unloading Modules.
 *
 */
@JsType
public class ClientModuleManager implements IClientModuleManager {
	static Logger logger = Logger.getLogger("ClientModuleManager");
	
	ClientCore clientCore;
	IClientSession moduleManagerSession;
	ModuleManagerApiLibrary moduleManagerApi;
	
	Set<ModuleInfo> serverActiveModules;
	Set<IClientModule> activeModules;
	Set<ModuleInfo> loadingModules;
	Set<IClientModule> loadedModules;
	
	//Handle session events
	ISessionListener sessionListener = new ISessionListener() {
		
		@Override
		public void onStarted(IClientSession session) {
			if(clientCore == null)
				return;
			
			//Get all Modules and Register to Subscriptions
			requestModules();
			setupSubscriptions();
		}
		
		@Override
		public void onRejected(IClientSession session) {
			logger.severe("Failed to start session for ModuleManager!");
			//TODO: How can we recover from this?
		}
		
		@Override
		public void onEnded(IClientSession session) {
			//For now we keep all Modules as is until we get a new session.
			//At this point subscriptions are broken.
		}
	};
	
	
	public ClientModuleManager(ClientCore clientCore) {
		this.clientCore = clientCore;
		
		serverActiveModules = new HashSet<ModuleInfo>();
		activeModules = new HashSet<IClientModule>();
		loadingModules = new HashSet<ModuleInfo>();
		loadedModules = new HashSet<IClientModule>();
		if(clientCore == null) {
			logger.severe("Failed to load: Client Core is null!");
			return;
		}
		
		moduleManagerApi = ModuleManagerApiLibrary.get(clientCore);
	}
	
	public void setup() {
		if(clientCore == null)
			return;
		
		logger.info("Staring Module Manager...");
		
		moduleManagerSession = clientCore.startSession(moduleManagerApi, sessionListener);
		if(moduleManagerSession == null) {
			logger.severe("Failed to load: Unable to start session with Service Provider!");
			return;
		}
	}
	
	public void cleanup() {
		serverActiveModules.clear();
		
		for(IClientModule module : activeModules) {
			module.onDeactivate();
		}
		activeModules.clear();
		
		if(moduleManagerSession != null)
			moduleManagerSession.removeListener(sessionListener);
	}
	
	@Override
	public void registerModule(IClientModule module) {		
		loadedModules.add(module);
		
		//Check if module should activate active
		if(!shouldModuleBeActive(module))
			return;
		
		activeModules.add(module);
		module.onActivate();
	}
	
	/**
	 * Get all active modules from the Server
	 */
	private void requestModules() {
		logger.info("Requesting full update from Server");
		
		if(!moduleManagerApi.getActiveModules(this::onGotModules)) {
			logger.severe("Failed to request update from Server!");
		}
	}
	
	/**
	 * Subscribe to Module events
	 */
	private void setupSubscriptions() {
		//TODO
	}
	
	/**
	 * Whether the given module should be active, as given by the Server.
	 * @param module
	 * @return
	 */
	private boolean shouldModuleBeActive(IClientModule module) {
		for(ModuleInfo moduleInfo : serverActiveModules) {
			if(moduleInfo.getName().equals(module.getName()))
				return true;
		}
		
		return false;
	}
	
	public boolean isModuleActive(IClientModule module) {
		return activeModules.contains(module);
	}
	
	public boolean isModuleLoading(ModuleInfo module) {
		for(ModuleInfo loadingModule : loadingModules) {
			if(module.equals(loadingModule))
				return true;
		}
		
		return false;
	}
	
	public boolean isModuleLoaded(ModuleInfo module) {
		for(IClientModule loadedModule : loadedModules) {
			if(module.getName().equals(loadedModule.getName()))
				return true;
		}
		
		return false;
	}
	
	public boolean isModuleLoadingOrLoaded(ModuleInfo module) {
		return isModuleLoaded(module) || isModuleLoading(module);
	}
	
	/**
	 * Got a full list of currently active Modules from the server.
	 */
	private void onGotModules(ModuleInfo[] newActiveModules) {
		logger.info("Got full update from Server with " + newActiveModules.length + " modules");
		serverActiveModules = new HashSet<ModuleInfo>(Arrays.asList(newActiveModules));
		
		//Deactivate modules which were previously Active
		List<IClientModule> deactivateModules = new ArrayList<IClientModule>();
		for(IClientModule module : activeModules) {
			if(!isModuleActive(module)) {
				deactivateModules.add(module);
			}
		}
		activeModules.removeAll(deactivateModules);
		for(IClientModule module : deactivateModules) {
			module.onDeactivate();
		}
		
		//Load new modules
		for(ModuleInfo module : serverActiveModules) {
			loadModule(module);
		}
	}
	
	private void loadModule(ModuleInfo module) {
		if(isModuleLoadingOrLoaded(module))
			return;
		
		logger.info("Loading module: " + module.getDescriptiveName());
		
		//Inject the module script
		ScriptInjector.fromUrl("modules/" + module.getName() + "/" + module.getName() + ".nocache.js").setCallback(new Callback<Void, Exception>() {
			
			@Override
			public void onSuccess(Void result) {
				//TODO: Progress tracking?
			}
			
			@Override
			public void onFailure(Exception reason) {
				logger.severe("Failed to load Module: " + module.getName() + " at " + module.getName());
				//TODO: Propagate this event to the user somehow?
			}
		}).setWindow(ScriptInjector.TOP_WINDOW).inject();
	}

}
