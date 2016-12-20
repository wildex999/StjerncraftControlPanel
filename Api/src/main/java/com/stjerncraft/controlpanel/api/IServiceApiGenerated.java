package com.stjerncraft.controlpanel.api;

import org.json.JSONObject;

/**
 * The generated class for the Service API will implement this interface.
 * This is will define the interaction surface between the Agent and the Service Provider implementation.
 */

public interface IServiceApiGenerated {
	public int getSerializerVersion();
	public int getApiVersion();
	public String getApiName();
	
	public String callMethod(IServiceProvider serviceProvider, JSONObject method);
}
