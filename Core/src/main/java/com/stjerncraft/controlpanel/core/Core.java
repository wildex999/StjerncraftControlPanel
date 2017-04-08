package com.stjerncraft.controlpanel.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.stjerncraft.controlpanel.core.api.ServiceApi;

public class Core {
	
	Map<String, IAgent> agents;

	public Core() {
		agents = new HashMap<>();
	}
	
	public void addAgent(IAgent agent) {
		agents.put(agent.getName(), agent);
	}
	
	public IAgent getAgent(String name) {
		return agents.get(name);
	}
	
	public List<IAgent> getAgents() {
		return new ArrayList<>(agents.values());
	}
	
	public List<IAgent> getAgentsProviding(ServiceApi api) {
		List<IAgent> agentList = new ArrayList<>();
		
		for(IAgent agent : agents.values()) {
			if(agent.providesApi(api))
				agentList.add(agent);
		}
		
		return agentList;
	}
	
	/**
	 * Get all Service Providers implementing the given ServiceApi
	 * @param api
	 * @return
	 */
	public List<ServiceProvider<? extends ServiceApi>> getServiceProviders(ServiceApi api) {
		List<ServiceProvider<? extends ServiceApi>> serviceProviderList = new ArrayList<>();
		for(IAgent agent : agents.values())
			serviceProviderList.addAll(agent.getServiceProviders(api));

		return serviceProviderList;
	}
	
	public List<ServiceApi> getServiceApiList() {
		Set<ServiceApi> apiSet = new HashSet<ServiceApi>(); //Use Set to avoid duplicates
		for(IAgent agent : agents.values())
			apiSet.addAll(agent.getServiceApiList());
		
		return new ArrayList<>(apiSet);
	}
	
	/**
	 * Get all known version of the Service API.
	 * @param name The full name of the Service API to get
	 * @return List of versions of the Service API currently known by any agent.
	 */
	public List<ServiceApi> getServiceApi(String name) {
		List<ServiceApi> apiList = new ArrayList<>();
		
		for(IAgent agent : agents.values())
			apiList.addAll(agent.getServiceApiList(name));
		
		return apiList;
	}

}
