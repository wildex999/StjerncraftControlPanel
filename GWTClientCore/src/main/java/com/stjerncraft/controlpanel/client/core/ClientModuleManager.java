package com.stjerncraft.controlpanel.client.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.StyleInjector;
import com.stjerncraft.controlpanel.client.api.IClientModuleListener;
import com.stjerncraft.controlpanel.client.api.IClientModuleManager;
import com.stjerncraft.controlpanel.client.api.module.IClientModule;
import com.stjerncraft.controlpanel.client.api.session.IClientSession;
import com.stjerncraft.controlpanel.client.api.session.ISessionListener;
import com.stjerncraft.controlpanel.common.api.ModuleManagerApiLibrary;
import com.stjerncraft.controlpanel.common.data.IModuleInfo;
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
	
	Set<IClientModuleListener> moduleListeners;
	
	Set<ModuleInfo> serverActiveModules;
	Map<String, IClientModule> activeModules;
	Map<String, ModuleInfo> loadingModules;
	Map<String, IClientModule> loadedModules;
	
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
			logger.warning("ModuleManager session ended!");
		}
	};
	
	
	public ClientModuleManager(ClientCore clientCore) {
		this.clientCore = clientCore;
		
		moduleListeners = new HashSet<IClientModuleListener>();
		serverActiveModules = new HashSet<ModuleInfo>();
		activeModules = new HashMap<String, IClientModule>();
		loadingModules = new HashMap<String, ModuleInfo>();
		loadedModules = new HashMap<String, IClientModule>();
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
		
		for(IClientModule module : activeModules.values()) {
			module.onDeactivate();
		}
		activeModules.clear();
		
		if(moduleManagerSession != null)
			moduleManagerSession.removeListener(sessionListener);
	}
	
	public boolean isModuleLoading(String module) {
		return loadingModules.containsKey(module);
	}
	
	public boolean isModuleLoaded(String module) {
		if(loadedModules.containsKey(module))
			return true;
		
		return false;
	}
	
	public boolean isModuleLoadingOrLoaded(String module) {
		return isModuleLoaded(module) || isModuleLoading(module);
	}
	
	@Override
	public boolean registerModule(IClientModule module) {
		//Verify that the named Module is actually still loading
		if(loadingModules.remove(module.getName()) == null) {
			logger.warning("Trying to register module which was not loading: " + module.getName());
			return false;
		}
		
		loadedModules.put(module.getName(), module);
		logger.info("Loaded module: " + module.getName());
		
		for(IClientModuleListener listener : moduleListeners)
			listener.onModuleLoaded(module);
		
		//Check if module should activate
		if(!shouldModuleBeActive(module))
			return true;
		
		activateModule(module);
		
		return true;
	}
	
	@Override
	public IClientModule getLoadedModule(String name) {
		return loadedModules.get(name);
	}
	
	@Override
	public IModuleInfo[] getLoadingModules() {
		return loadingModules.values().toArray(new IModuleInfo[loadingModules.size()]);
	}

	@Override
	public IClientModule[] getLoadedModules() {
		return loadedModules.values().toArray(new IClientModule[loadedModules.size()]);
	}

	@Override
	public IClientModule[] getActiveModules() {
		return activeModules.values().toArray(new IClientModule[activeModules.size()]);
	}

	@Override
	public void addModuleListener(IClientModuleListener listener) {
		moduleListeners.add(listener);
	}

	@Override
	public void removeModuleListener(IClientModuleListener listener) {
		moduleListeners.remove(listener);
	}
	
	@Override
	public boolean isModuleActive(String module) {
		return activeModules.containsKey(module);
	}
	
	private void activateModule(IClientModule module) {
		if(activeModules.containsKey(module.getName()))
			return;
		
		activeModules.put(module.getName(), module);
		module.onActivate();
		
		//Inform listeners
		for(IClientModuleListener listener : moduleListeners)
			listener.onModuleActivated(module);
		
		logger.info("Activated module: " + module.getName());
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
	
	/**
	 * Got a full list of currently active Modules from the server.
	 */
	private void onGotModules(ModuleInfo[] newActiveModules) {
		logger.info("Got full update from Server with " + newActiveModules.length + " modules");
		serverActiveModules = new HashSet<ModuleInfo>(Arrays.asList(newActiveModules));
		
		//Deactivate modules which were previously Active
		List<IClientModule> deactivateModules = new ArrayList<IClientModule>();
		for(IClientModule module : activeModules.values()) {
			if(!isModuleActive(module.getName())) {
				deactivateModules.add(module);
			}
		}
		for(IClientModule module : deactivateModules) {
			activeModules.remove(module.getName());
			module.onDeactivate();
		}
		
		//Load new modules
		for(ModuleInfo module : serverActiveModules) {
			loadModule(module);
		}
	}
	
	private void loadModule(ModuleInfo module) {
		if(isModuleLoadingOrLoaded(module.getName()))
			return;
		
		logger.info("Loading module: " + module.getName());
		loadingModules.put(module.getName(), module);
		
		for(IClientModuleListener listener : moduleListeners)
			listener.onModuleLoading(module.getName());
		
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
