package com.stjerncraft.controlpanel.core.server;

import static spark.Spark.*;

import com.stjerncraft.controlpanel.core.Core;

import spark.Request;
import spark.Response;

public class HTTPServer {
	
	protected short port;
	
	
	public HTTPServer(Core core, short port) {
		this.port = port;
	}
	
	public void setSecure(String keystoreFilePath, String keystorePassword, String truststoreFilePath, String truststorePassword) {
		//TODO
	}

	public void start() {
		port(port);
		
		staticFiles.externalLocation("/public");
		webSocket("/ws", new CoreWebSocket());
		
		//Help the user along
		redirect.get("/", "/index.html");
		
		//Modules
		before("/*", (req, res) -> handleAuthentication(req, res));
		get("/modules/:module", (req, res) -> { return handleModules(req, res); });
		
		awaitInitialization();
	}
	
	/**
	 * Check whether the client authentication is valid.
	 * 
	 */
	protected void handleAuthentication(Request req, Response res) {
		//Any non-public requests must contain a authorization token, which is checked for validity here.
		//If not valid, terminate the connection.
		//If valid, set the user attribute to the authorized User object.
	}
	
	/**
	 * Return module files if client has permission
	 */
	public Object handleModules(Request req, Response res) {
		//If no module name is given, return a list of modules the client is authorized to access
		
		//If module name is given, return the requested file for the given module, or the module main js if no file requested.
		//If client is not authenticated to access module, return 403, even if the module does not exist(To avoid scanning for installed modules)
		return null;
	}
}
