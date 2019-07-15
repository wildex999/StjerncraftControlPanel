package com.stjerncraft.controlpanel.module.core.client;

import com.google.gwt.core.client.EntryPoint;

public class Main implements EntryPoint {
	
	ClientCore clientCore;
	
	@Override
	public void onModuleLoad() {
		clientCore = new ClientCore("localhost:8080");
	}

}
