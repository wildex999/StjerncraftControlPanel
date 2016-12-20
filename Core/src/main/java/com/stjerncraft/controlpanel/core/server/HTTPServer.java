package com.stjerncraft.controlpanel.core.server;

import static spark.Spark.*;

import com.stjerncraft.controlpanel.core.Core;

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
		
		get("/hello", (req, res) -> "Hello World");
		
		awaitInitialization();
	}
}
