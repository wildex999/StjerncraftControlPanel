package com.stjerncraft.controlpanel.agent;

import java.util.List;

/**
 * Information about a Service Provider which exists with an Agent
 */

public class ServiceProvider<T extends ServiceApi> {
	final protected List<T> apiList;
	final protected IAgent<? extends ServiceProvider<T>, T> agent;
	final protected String uuid;
	
	public ServiceProvider(IAgent<? extends ServiceProvider<T>, T> agent, List<T> apiList, String uuid) {
		this.apiList = apiList;
		this.agent = agent;
		this.uuid = uuid;
	}
	
	public List<T> getApiList() {
		return apiList;
	}
	
	public IAgent<? extends ServiceProvider<T>, T> getAgent() {
		return agent;
	}
	
	/**
	 * Get the UUID of this Service Provider
	 * @return
	 */
	public String getId() {
		return uuid;
	}
}
