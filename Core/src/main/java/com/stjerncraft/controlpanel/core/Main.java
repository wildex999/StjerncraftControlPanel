package com.stjerncraft.controlpanel.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stjerncraft.controlpanel.core.server.HTTPServer;
import com.stjerncraft.controlpanel.core.service.LocalAgent;
import com.stjerncraft.controlpanel.core.service.ServiceServer;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		//Setup Core and load config
		logger.info("Loading config");
		//TODO
		
		//Setup local Service Providers
		logger.info("Setting up local Agents and Services");
		LocalAgent localAgent = new LocalAgent("local");
		localAgent.addServiceProvider(new ServiceServer());

		//Setup Remote Agents
		logger.info("Setting up Remote Agents");
		//TODO
		
		//Start Web Server
		//TODO
		logger.info("Starting HTTP Server");
		HTTPServer server = new HTTPServer(new Core(), (short) 8080);
		server.start();
	}
}