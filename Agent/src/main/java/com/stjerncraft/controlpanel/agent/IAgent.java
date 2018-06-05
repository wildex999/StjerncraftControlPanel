package com.stjerncraft.controlpanel.agent;

import java.util.List;

/**
 * The agent is the representation of a group of Service Providers.
 * An agent might be a Minecraft Server, or any other remote(or local) host of Service Providers.
 */

public interface IAgent<T extends ServiceProvider<V>, V extends ServiceApi> {
	//The name of a given Agent should remain the same when possible, allowing the user to specify which agent to use by name.
	//The Agent's name must be unique, and should be representative of the server it is on.
	public String getName();
	public String getId();
	
	public List<V> getServiceApiList();
	public List<V> getServiceApiList(String apiName);

	public List<T> getServiceProviders();
	public List<T> getServiceProviders(ServiceApi api);
	
	/**
	 * Get all the active sessions with Service Providers in this Agent
	 * @return
	 */
	public List<ISession> getSessions();
	
	public boolean providesApi(ServiceApi api);
	
	/**
	 * Add listener for the adding/removal of API and Service Providers
	 */
	public void addListener(IAgentListener listener);
	public boolean removeListener(IAgentListener listener);
	
	/**
	 * Start a session with a ServiceProvider registered to this Agent
	 * @param service Service Provider to start a session with.
	 * @param client Client which is starting the session.
	 * @param sessionId Unique ID for the session
	 * @return A session object to use for further communication, or null if it failed to create a session.
	 */
	public ISession startSession(ServiceApi api, ServiceProvider<? extends ServiceApi> service, IRemoteClient client, int sessionId);
}
