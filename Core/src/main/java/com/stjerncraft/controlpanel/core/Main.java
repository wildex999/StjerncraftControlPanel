package com.stjerncraft.controlpanel.core;

import com.stjerncraft.controlpanel.core.server.HTTPServer;
import com.stjerncraft.controlpanel.core.service.Agent;
import com.stjerncraft.controlpanel.core.service.ServiceServer;

public class Main {
	public static void main(String[] args) {
		//Setup Core and load config
		//TODO
		
		//Setup local Service Providers
		Agent localAgent = new Agent("local");
		localAgent.addServiceProvider(new ServiceServer());

		//Setup Remote Agents
		//TODO
		
		//Start Web Server
		//TODO
		HTTPServer server = new HTTPServer(new Core(), (short) 8080);
		server.start();
	}
}