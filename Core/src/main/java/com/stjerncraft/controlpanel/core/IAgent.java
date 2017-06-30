package com.stjerncraft.controlpanel.core;

import java.util.List;

import com.stjerncraft.controlpanel.core.api.ServiceApi;
import com.stjerncraft.controlpanel.core.client.IClient;

/**
 * The agent is the representation of a group of Service Providers.
 * An agent might be a Minecraft Server, or any other remote(or local) host of Service Providers.
 */

public interface IAgent<T extends ServiceProvider<? extends ServiceApi>, V extends ServiceApi> {
	//The name of a given Agent should remain the same when possible, allowing the user to specify which agent to use by name.
	//The Agent's name must be unique.
	public String getName();
	
	public List<V> getServiceApiList();
	public List<V> getServiceApiList(String apiName);

	public List<T> getServiceProviders();
	public List<T> getServiceProviders(ServiceApi api);
	
	public boolean providesApi(ServiceApi api);
	
	/**
	 * Start a session with a ServiceProvider registered to this Agent
	 * @param service Service Provider to start a session with.
	 * @param client Client which is starting the session.
	 * @return A session object to use for further communication, or null if it failed to create a session.
	 */
	public ISession startSession(ServiceProvider<? extends ServiceApi> service, IClient client);
}
