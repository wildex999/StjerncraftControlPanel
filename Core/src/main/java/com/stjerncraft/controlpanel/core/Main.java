package com.stjerncraft.controlpanel.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stjerncraft.controlpanel.agent.InvalidUUIDException;
import com.stjerncraft.controlpanel.agent.local.LocalAgent;
import com.stjerncraft.controlpanel.core.module.ModuleManager;
import com.stjerncraft.controlpanel.core.module.ModuleManagerConfig;
import com.stjerncraft.controlpanel.core.module.ModuleManagerConfigLoadException;
import com.stjerncraft.controlpanel.core.server.HTTPServer;
import com.stjerncraft.controlpanel.exceptions.MissingServiceException;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {		
		Core core = new Core();
		
		//Setup Core and load config
		logger.info("Loading config");
		//TODO
		
		
		//Setup local Service Providers
		logger.info("Setting up local Agents and Services");
		LocalAgent localAgent = new LocalAgent("core");
		try {
			core.addAgent(localAgent);
		} catch (InvalidUUIDException e1) {
			e1.printStackTrace();
			return;
		}
		
		ModuleManager moduleManager = setupModuleManager();
		if(moduleManager == null)
			return;
		localAgent.addServiceProvider(moduleManager);

		//Setup Remote Agents
		logger.info("Setting up Remote Agents");
		//TODO
		
		//Start Web Server
		//TODO
		logger.info("Starting HTTP Server");
		HTTPServer server = new HTTPServer(core, (short) 8080);
		try {
			server.start();
		} catch (MissingServiceException | IOException e) {
			server.stop();
			logger.error("Failed to start HTTP Server: " + e);
			return;
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				logger.info("Got shutdown signal.");
				server.stop();
			}
		});
		
		//Handle messages on the main thread
		while(server.isRunning()) {
			//TODO: Wait on thread?
			//Handle Client messages
			server.handleMessages();
			
			//Handle Remote Agent messages
			//TODO
		}
	}
	
	private static ModuleManager setupModuleManager() {
		ModuleManager moduleManager = new ModuleManager();
		String modulesConfigJson = null;
		ModuleManagerConfig config = null;
		String modulesConfigPath = "modules/config.json";
		
		try {
			modulesConfigJson = new String(Files.readAllBytes(Paths.get(modulesConfigPath)));
		} catch (IOException e) {
			if(!(e instanceof NoSuchFileException)) {
				logger.error("Unable to read " + modulesConfigPath + ": " + e);
				return null;
			}
			logger.warn("No Module Manager config was found at " + modulesConfigPath);
		}
		
		
		if(modulesConfigJson != null && !modulesConfigJson.isEmpty()) {
			try {
				config = ModuleManagerConfig.load(modulesConfigJson);
			} catch (ModuleManagerConfigLoadException | IOException e) {
				logger.error("Failed to parse modules config from " + modulesConfigPath + ": " + e);
				return null;
			}
		} else {
			//If no config exists, create a default config
			logger.info("Creating default Module Manager config at " + modulesConfigPath);
			config = ModuleManagerConfig.createDefault();
			try {
				Path newPath = Paths.get(modulesConfigPath);
				newPath.toFile().getParentFile().mkdirs();
				Files.write(newPath, config.save().getBytes(), StandardOpenOption.CREATE_NEW);
			} catch (IOException e) {
				logger.error("Failed to write new Module Config: " + e);
				return null;
			}
		}

		moduleManager.loadModules(config);
		
		return moduleManager;
	}
}