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
		
		staticFiles.location("/public");
		
		before("/modules/:module", (req, res) -> handleAuthentication(req, res));
		get("/modules/*", (req, res) -> { return handleModules(req, res); });
		
		
		
		awaitInitialization();
	}
	
	protected void handleAuthentication(Request req, Response res) {
		
	}
	
	public Object handleModules(Request req, Response res) {
		return null;
	}
}
