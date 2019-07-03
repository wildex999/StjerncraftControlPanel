package com.stjerncraft.controlpanel.core.server;

import static spark.Spark.awaitInitialization;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.redirect;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.stjerncraft.controlpanel.agent.local.LocalServiceApi;
import com.stjerncraft.controlpanel.agent.local.LocalServiceProvider;
import com.stjerncraft.controlpanel.common.api.ModuleManagerApi;
import com.stjerncraft.controlpanel.core.Core;
import com.stjerncraft.controlpanel.exceptions.MissingServiceException;

import spark.Request;
import spark.Response;
import spark.Spark;

public class HTTPServer {
	
	protected short port;
	protected Core core;
	protected CoreWebSocket coreWebSocket;
	protected AtomicBoolean isRunning;
	
	private ModuleManagerApi moduleManager;
	
	private static final Logger logger = LoggerFactory.getLogger(HTTPServer.class);
	
	public HTTPServer(Core core, short port) {
		this.core = core;
		this.port = port;
		
		isRunning = new AtomicBoolean();
	}
	
	public void setSecure(String keystoreFilePath, String keystorePassword, String truststoreFilePath, String truststorePassword) {
		//TODO: Handle HTTPS
	}

	public void start() throws MissingServiceException, IOException {
		port(port);
		
		String publicPath = System.getProperty("publicPath");
		if(Objects.equal(null, publicPath))
			publicPath = Paths.get("public").toRealPath().toString();
		else
			publicPath = Paths.get(publicPath).toRealPath().toString();
		
		staticFiles.externalLocation(publicPath);
		coreWebSocket = new CoreWebSocket(core.getClientManager());
		webSocket("/ws", coreWebSocket);
		
		//Help the user along
		redirect.get("/", "/index.html");
		
		awaitInitialization();
		
		//TODO: Remove in production build. Only used for testing
		get("/stop", (req, res) -> { stop(); return null; });
		
		//Authentication
		before("/*", (req, res) -> handleAuthentication(req, res));
		
		//Modules
		setupModules();
		
		isRunning.set(true);
	}
	
	public void stop() {
		logger.info("Shutting down HTTP Server.");
		Spark.stop();
		isRunning.set(false);
	}
	
	public boolean isRunning() {
		return isRunning.get();
	}

	private void setupModules() throws MissingServiceException  {
		LocalServiceApi moduleManagerApi = core.getLocalServiceApi(ModuleManagerApi.class);
		if(moduleManagerApi == null)
			throw new MissingServiceException("No valid Module Manager Service API was found.");
		List<LocalServiceProvider> moduleManagers = core.getLocalServiceProviders(moduleManagerApi);
		if(moduleManagers.isEmpty())
			throw new MissingServiceException("No valid Module Manager Service was found.");
		moduleManager = (ModuleManagerApi)moduleManagers.get(0).getServiceProvider();
		if(moduleManager == null)
			throw new MissingServiceException("Module Manager Service Provider is null!");
		
		get("/modules/:module", (req, res) -> { return handleModules(req, res); });
	}
	
	/**
	 * Check whether the client authentication is valid.
	 * 
	 */
	protected void handleAuthentication(Request req, Response res) {
		//Any non-public requests must contain a authorization token, which is checked for validity here.
		//If not valid, terminate the connection.
		//If valid, set the user attribute to the authorized User object.
		//TODO
		//TODO: This must be thread safe!
	}
	
	/**
	 * Return module files if client has permission
	 */
	public Object handleModules(Request req, Response res) {
		//If no module name is given, return a list of modules the client is authorized to access
		
		//If module name is given, return the requested file for the given module, or the module main js if no file requested.
		//If client is not authenticated to access module, return 403, even if the module does not exist(To avoid scanning for installed modules)
		//TODO: This must be thread safe!
		return null;
	}
	
	public Core getCore() {
		return core;
	}
	
	public CoreWebSocket getWebSocket() {
		return coreWebSocket;
	}
}
