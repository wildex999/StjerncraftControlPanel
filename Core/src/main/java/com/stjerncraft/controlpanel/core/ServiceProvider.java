package com.stjerncraft.controlpanel.core;

import java.util.List;

import com.stjerncraft.controlpanel.core.api.ServiceApi;

/**
 * Information about a Service Provider which exists with an Agent
 */

public class ServiceProvider<T extends ServiceApi> {
	final protected List<T> apiList;
	final protected IAgent agent;
	
	public ServiceProvider(IAgent agent, List<T> apiList) {
		this.apiList = apiList;
		this.agent = agent;
	}
	
	public List<T> getApiList() {
		return apiList;
	}
	
	public IAgent getAgent() {
		return agent;
	}
}
