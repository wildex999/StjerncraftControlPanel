package com.stjerncraft.controlpanel.core.server;

import static spark.Spark.awaitInitialization;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.redirect;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.stjerncraft.controlpanel.agent.local.LocalServiceApi;
import com.stjerncraft.controlpanel.agent.local.LocalServiceProvider;
import com.stjerncraft.controlpanel.common.api.ModuleManagerApi;
import com.stjerncraft.controlpanel.common.data.ModuleInfo;
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
	private Path modulesPath;
	
	private static final String noPermissionMessage = "No Permission";
	
	private static final Logger logger = LoggerFactory.getLogger(HTTPServer.class);
	
	public HTTPServer(Core core, short port, Path modulesPath) {
		this.core = core;
		this.port = port;
		this.modulesPath = modulesPath;
		
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
		
		get("/modules", (req, res) -> { return handleModules(req, res); });
		get("/modules/:module", (req, res) -> { return handleModules(req, res); });
		get("/modules/:module/:file", (req, res) -> { return handleModules(req, res); });
	}
	
	/**
	 * Check whether the client authentication is valid.
	 * Note: Must be thread-safe
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
	 * Note: Must be thread-safe
	 */
	public Object handleModules(Request req, Response res) {
		String moduleName = req.params("module");
		String file = req.params("file");
		
		//If no module name is given, return a list of modules the client is authorized to access
		if(moduleName == null) {
			//TODO: Limit to what the User is allowed to see
			ModuleInfo[] modules = moduleManager.getAllModules();
			JSONArray jsonArray = new JSONArray();
			for(ModuleInfo moduleInfo : modules) {
				jsonArray.put(moduleInfo.getName());
			}
			return jsonArray.toString();
		}
		
		//If module name is given, return the requested file for the given module.
		//If client is not authenticated to access module, return 403, even if the module does not exist(To avoid scanning for installed modules)
		ModuleInfo module = moduleManager.getModule(moduleName);
		if(module == null) {
			res.status(403);
			return noPermissionMessage;
		}
		
		if(file == null) {
			res.status(403);
			return noPermissionMessage;
		}
		
		//Write raw file (TODO: handle binary)
		File readingFile = modulesPath.resolve(moduleName).resolve(file).toFile();
		if(!readingFile.exists() && readingFile.canRead()) {
			logger.warn("Failed to read " + file + " for Module: " + moduleName);
			res.status(403);
			return noPermissionMessage;
		}
		
		try {
			return new String(Files.readAllBytes(readingFile.toPath()));
		} catch (IOException e) {
			logger.warn("Failed to read file " + readingFile + ": " + e);
			res.status(403);
			return noPermissionMessage;
		}
	}
	
	public Core getCore() {
		return core;
	}
	
	public CoreWebSocket getWebSocket() {
		return coreWebSocket;
	}
}
