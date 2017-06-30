package com.stjerncraft.controlpanel.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stjerncraft.controlpanel.api.IServiceApiGenerated;
import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.api.util.Generated;
import com.stjerncraft.controlpanel.core.api.ServiceApi;
import com.stjerncraft.controlpanel.core.service.LocalAgent;
import com.stjerncraft.controlpanel.core.service.LocalServiceApi;
import com.stjerncraft.controlpanel.core.service.LocalServiceProvider;

public class Core {
	private static final Logger logger = LoggerFactory.getLogger(Core.class);
	
	Map<String, IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi>> agents;

	public Core() {
		agents = new HashMap<>();
	}
	
	public void addAgent(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent) {
		agents.put(agent.getName(), agent);
	}
	
	public IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> getAgent(String name) {
		return agents.get(name);
	}
	
	public List<IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi>> getAgents() {
		return new ArrayList<>(agents.values());
	}
	
	public List<IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi>> getAgentsProviding(ServiceApi api) {
		List<IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi>> agentList = new ArrayList<>();
		
		for(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent : agents.values()) {
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
		for(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent : agents.values())
			serviceProviderList.addAll(agent.getServiceProviders(api));

		return serviceProviderList;
	}
	
	/**
	 * Get all Local Service Providers implementing the given ServiceApi
	 * @param api
	 * @return
	 */
	public List<LocalServiceProvider> getLocalServiceProviders(ServiceApi api) {
		List<LocalServiceProvider> serviceProviderList = new ArrayList<>();
		for(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent : agents.values()) {
			if(!(agent instanceof LocalAgent))
				continue;
			
			LocalAgent localAgent = (LocalAgent)agent;
			for(ServiceProvider<? extends ServiceApi> provider : localAgent.getServiceProviders(api))
				serviceProviderList.add((LocalServiceProvider)provider);
		}

		return serviceProviderList;
	}
	
	/**
	 * Get a list of all registered Service APIs.
	 * @return
	 */
	public List<ServiceApi> getServiceApiList() {
		Set<ServiceApi> apiSet = new HashSet<ServiceApi>(); //Use Set to avoid duplicates
		for(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent : agents.values())
			apiSet.addAll(agent.getServiceApiList());
		
		return new ArrayList<>(apiSet);
	}
	
	public List<LocalServiceApi> getLocalServiceApiList() {
		Set<LocalServiceApi> apiSet = new HashSet<>(); //Use Set to avoid duplicates
		for(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent : agents.values()) {
			if(!(agent instanceof LocalAgent))
				continue;
			LocalAgent localAgent = (LocalAgent)agent;
			apiSet.addAll(localAgent.getServiceApiList());
		}
		
		return new ArrayList<>(apiSet);
	}
	
	/**
	 * Get all known versions of the Service API.
	 * @param name The full name of the Service API to get
	 * @return List of versions of the Service API currently known by any agent.
	 */
	public List<ServiceApi> getServiceApi(String name) {
		List<ServiceApi> apiList = new ArrayList<>();
		
		for(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent : agents.values())
			apiList.addAll(agent.getServiceApiList(name));
		
		return apiList;
	}
	
	/**
	 * Get all known local versions of the Service API
	 * @param name The full name of the Service API to get
	 * @return List of versions of the Service API currently known by any local agent.
	 */
	public List<LocalServiceApi> getLocalServiceApi(String name) {
		List<LocalServiceApi> apiList = new ArrayList<>();
		
		for(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent : agents.values()) {
			if(!(agent instanceof LocalAgent))
				continue;
			
			LocalAgent localAgent = (LocalAgent)agent;
			apiList.addAll(localAgent.getServiceApiList(name));
		}
		
		return apiList;
	}
	
	/**
	 * Get the Local Service API for the given Service API Interface, where both name and version match.
	 * @param apiInterfaceClass Interface Class with the ServiceApi annotation.
	 * @return Null if no API with the given name and version is registered.
	 */
	public LocalServiceApi getLocalServiceApi(Class<? extends IServiceProvider> apiInterfaceClass) {
		//Get the Generated API class for the given Service API
		IServiceApiGenerated generatedApi;
		try {
			generatedApi = Generated.getGeneratedApi(apiInterfaceClass);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			logger.debug("Failed to get Local Service Api " + apiInterfaceClass + ": " + e);
			return null;
		}
		
		//Get API with the correct version
		List<LocalServiceApi> apiList = getLocalServiceApi(generatedApi.getApiName());
		for(LocalServiceApi api : apiList) {
			if(api.getVersion() == generatedApi.getApiVersion())
				return api;
		}
		
		return null;
	}

}
