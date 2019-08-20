package com.stjerncraft.controlpanel.webview;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.stjerncraft.controlpanel.client.api.GlobalClientCore;
import com.stjerncraft.controlpanel.client.api.IClientCoreApi;

public class Main implements EntryPoint {
	static Logger logger = Logger.getLogger("WebView.Main");
	
	IClientCoreApi clientCore;
	WebView webView;
	
	@Override
	public void onModuleLoad() {
		logger.info("Loading WebView...");
		
		clientCore = GlobalClientCore.get();
		if(clientCore == null) {
			logger.severe("Error while loading: Unable to get Client Core instance!");
			return;
		}
		
		//Register Module with the Client Core
		webView = new WebView(clientCore.getModuleManager());
		clientCore.getModuleManager().registerModule(webView);
		logger.info("Loaded WebView.");
	}

}
