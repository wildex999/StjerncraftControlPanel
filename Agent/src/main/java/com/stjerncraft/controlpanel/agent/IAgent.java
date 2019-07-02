package com.stjerncraft.controlpanel.agent;

import java.util.List;

import com.stjerncraft.controlpanel.common.ServiceApi;
import com.stjerncraft.controlpanel.common.data.AgentInfo;
import com.stjerncraft.controlpanel.common.data.ServiceProviderInfo;

/**
 * The agent is the representation of a group of Service Providers.
 * An agent might be a Minecraft Server, or any other remote(or local) host of Service Providers.
 */

public interface IAgent<T extends ServiceProvider<V>, V extends ServiceApi> {
	/**
	 * Get the Uuid of the agent.
	 * The Agent should preferably have the same UUID always, allowing the Agent/Server to be remembered between restarts.
	 * @return
	 */
	public String getUuid();
	
	/**
	 * The name of the Agent.
	 * This is usually what is shown to the user when choosing Server/Core.
	 * @return
	 */
	public String getName();
	
	public List<V> getServiceApiList();
	public List<V> getServiceApiList(String apiName);

	public List<T> getServiceProviders();
	public List<T> getServiceProviders(ServiceApi api);
	
	/**
	 * Get all the active sessions with Service Providers in this Agent
	 * @return
	 */
	public List<ISession> getSessions();
	
	/**
	 * Check whether this Agent has a Service Provider for the given API
	 * @param api
	 * @return
	 */
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
	
	public default AgentInfo getInfo() {
		//TODO: Cache this?
		List<T> providers = getServiceProviders();
		ServiceProviderInfo[] providersInfo = new ServiceProviderInfo[providers.size()];
		for(int i = 0; i < providers.size(); i++)
			providersInfo[i] = providers.get(i).getInfo();
		
		AgentInfo info = new AgentInfo(getUuid(), getName(), providersInfo);
		return info;
	}
}
