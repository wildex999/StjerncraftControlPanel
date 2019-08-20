package com.stjerncraft.controlpanel.modules;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.stjerncraft.controlpanel.client.api.GlobalClientCore;
import com.stjerncraft.controlpanel.client.api.IClientCoreApi;

public class Main implements EntryPoint {
	static Logger logger = Logger.getLogger("CoreModules.Main");
	
	IClientCoreApi clientCore;

	
	@Override
	public void onModuleLoad() {
		logger.info("Loading Core Modules...");
		
		clientCore = GlobalClientCore.get();
		if(clientCore == null) {
			logger.severe("Error while loading: Unable to get Client Core instance!");
			return;
		}
		
		//Register the Module with the Client Core
		CoreModules coreModules = new CoreModules(clientCore.getModuleManager());
		clientCore.getModuleManager().registerModule(coreModules);
		
		logger.info("Loaded Core Modules.");
	}

}
