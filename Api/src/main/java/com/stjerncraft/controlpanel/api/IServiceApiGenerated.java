package com.stjerncraft.controlpanel.api;

/**
 * The generated class for the Service API will implement this interface.
 * This will define the interaction surface between the Agent and the Service Provider implementation.
 */
public interface IServiceApiGenerated {
	public int getSerializerVersion();
	public int getApiVersion();
	public String getApiName();
	
	public String callMethod(IServiceProvider serviceProvider, String methodJson);
	public boolean callEventHandler(IServiceProvider serviceProvider, String eventMethodJson);
}
