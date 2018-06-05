package com.stjerncraft.controlpanel.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stjerncraft.controlpanel.agent.IAgent;
import com.stjerncraft.controlpanel.agent.IAgentListener;
import com.stjerncraft.controlpanel.agent.IRemoteClient;
import com.stjerncraft.controlpanel.agent.ISession;
import com.stjerncraft.controlpanel.agent.InvalidUUIDException;
import com.stjerncraft.controlpanel.agent.ServiceApi;
import com.stjerncraft.controlpanel.agent.ServiceProvider;
import com.stjerncraft.controlpanel.agent.local.LocalAgent;
import com.stjerncraft.controlpanel.agent.local.LocalServiceApi;
import com.stjerncraft.controlpanel.agent.local.LocalServiceProvider;
import com.stjerncraft.controlpanel.api.IServiceApiGenerated;
import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.api.util.Generated;

public class Core {
	private static final Logger logger = LoggerFactory.getLogger(Core.class);
	
	Map<String, IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi>> agents;
	
	//Map UUID -> instance
	Map<String, IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi>> uuidAgents;
	Map<String, ServiceApi> uuidApis;
	Map<String, ServiceProvider<? extends ServiceApi>> uuidProviders;
	
	Map<Integer, ISession> sessions; //Key: session ID
	int sessionIdCounter;
	
	Set<IRemoteClient> clients;
	
	IAgentListener agentListener;

	public Core() {
		agents = new HashMap<>();
		uuidAgents = new HashMap<>();
		sessions = new HashMap<>();
		clients = new HashSet<>();
		
		sessionIdCounter = 0;
		
		agentListener = new IAgentListener() {

			@Override
			public void onApiAdded(ServiceApi api) throws InvalidUUIDException {
				String uuid = api.getUuid();
				if(!isUuidValid(uuid))
					throw new InvalidUUIDException(uuid);
				if(uuidApis.containsKey(uuid))
					throw new InvalidUUIDException(uuid, true);
			}

			@Override
			public void onApiRemoved(ServiceApi api) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderAdded(ServiceProvider<? extends ServiceApi> provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderRemoved(ServiceProvider<? extends ServiceApi> provider) {
				// TODO Auto-generated method stub
				
			}
		};
	}
	
	/**
	 * Create a new session id.
	 * 
	 * Note: It this reaches a negative number, it will overflow and start from 0.
	 * On overflow it might start getting collisions, which could lead to performance problems.
	 * @return
	 */
	public int getNextSessionId() {
		int id = sessionIdCounter++;
		if(id < 0) {
			logger.warn("Reached max session ID, restarting from 0. Server restart is recommended!");
			sessionIdCounter = 0;
			id = getNextSessionId();
		}
		if(sessions.containsKey(id)) {
			//If we get too many conflicts we might at some point hit an stack overflow.
			//This should in theory never happen, unless we have a ridiculous amount of active sessions.
			logger.warn("Hit session ID conflict at " + id + ". Server restart is recommended!");
			id = getNextSessionId();
		}
		
		return 0;
	}
	
	public void startSession() {
		
	}
	
	public void addSession(ISession newSession) {
		if(sessions.containsKey(newSession.getSessionId()))
			logger.error("Adding session which conflicts with existing session: " + newSession.getSessionId());
		
		sessions.put(newSession.getSessionId(), newSession);
	}
	
	public ISession getSession(int sessionId) {
		return sessions.get(sessionId);
	}
	
	/**
	 * End the session and remove it from the list of sessions.
	 * @param sessionId
	 * @param reason
	 * @return True if the session was ended, false if it was not found
	 */
	public boolean endSession(int sessionId, String reason) {
		ISession s = sessions.get(sessionId);
		if(s == null)
			return false;
		
		s.endSession(reason);
		return true;
	}
	
	public void addAgent(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent) throws InvalidUUIDException {
		String uuid = agent.getId();
		if(!isUuidValid(uuid))
			throw new InvalidUUIDException(uuid);
		if(uuidAgents.containsKey(uuid))
			throw new InvalidUUIDException(uuid, true);
		
		agents.put(agent.getName(), agent);
		uuidAgents.put(uuid, agent);
	}
	
	/**
	 * Remove the given Agent.
	 * This will remove any Service APIs and Service Providers managed by this agent!
	 * Any session with the Service Providers will be ended!
	 * @return
	 */
	public boolean removeAgent(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent) {
		if(!agents.containsKey(agent.getId()))
			return false;
		
		//End any sessions with the Service Providers
		for(ISession session : agent.getSessions())
			session.endSession("Agent is being removed");
		
		agents.remove(agent.getName());
		uuidAgents.remove(agent.getId());
		return true;
	}
	
	public IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> getAgentByName(String name) {
		return agents.get(name);
	}
	
	public IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> getAgentByUuid(String uuid) {
		return uuidAgents.get(uuid);
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
	
	private boolean isUuidValid(String uuid) {
		if(uuid == null || uuid.trim().length() != 36) //36 with dashes
			return false;
		return true;
	}

}
