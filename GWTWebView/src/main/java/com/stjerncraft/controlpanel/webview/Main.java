package com.stjerncraft.controlpanel.webview;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.stjerncraft.controlpanel.client.api.GlobalClientCore;
import com.stjerncraft.controlpanel.client.api.IClientCoreApi;
import com.stjerncraft.controlpanel.client.api.module.IClientModule;

import jsinterop.annotations.JsType;

@JsType
public class Main implements EntryPoint, IClientModule {
	static Logger logger = Logger.getLogger("WebView");
	
	IClientCoreApi clientCore;
	
	@Override
	public void onModuleLoad() {
		logger.info("Loading WebView...");
		
		//Setup Template System
		//Setup Pages
		//Setup Widgets
		
		//Register Module with the Client Core
		clientCore = GlobalClientCore.get();
		if(clientCore == null) {
			logger.severe("Error while loading: Unable to get Client Core instance!");
			return;
		}
		
		clientCore.getModuleManager().registerModule(this);
	}

	@Override
	public int getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onActivate() {
		//If User not logged in, load the Login page.
		//Else, load the Dashboard page.
	}

	@Override
	public void onDeactivate() {
		//Clear our Page and it's content
	}

}
