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
import com.stjerncraft.controlpanel.agent.ISessionListener;
import com.stjerncraft.controlpanel.agent.ServiceProvider;
import com.stjerncraft.controlpanel.agent.local.LocalAgent;
import com.stjerncraft.controlpanel.agent.local.LocalServiceApi;
import com.stjerncraft.controlpanel.agent.local.LocalServiceProvider;
import com.stjerncraft.controlpanel.api.IServiceApiGenerated;
import com.stjerncraft.controlpanel.api.util.Generated;
import com.stjerncraft.controlpanel.common.ServiceApi;
import com.stjerncraft.controlpanel.common.exceptions.InvalidUUIDException;
import com.stjerncraft.controlpanel.common.util.UUID;
import com.stjerncraft.controlpanel.core.client.ClientManager;

public class Core {
	private static final Logger logger = LoggerFactory.getLogger(Core.class);
	
	//Map UUID -> instance
	Map<String, IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi>> uuidAgents;
	Map<String, ServiceProvider<? extends ServiceApi>> uuidProviders;
	
	Map<String, ServiceApi> apis; //Key: API ID
	Map<Integer, ISession> sessions; //Key: session ID
	int sessionIdCounter;
	
	ClientManager clientManager;

	public Core() {
		uuidAgents = new HashMap<>();
		uuidProviders = new HashMap<>();
		apis = new HashMap<>();
		sessions = new HashMap<>();
		clientManager = new ClientManager(this);
		
		sessionIdCounter = 0; 
	}
	
	public ClientManager getClientManager() {
		return clientManager;
	}
	
	/**
	 * Try starting a session with the Client towards the given API on the given Service Provider.
	 * @param client
	 * @param provider
	 * @param api
	 * @return Null if the session was not accepted
	 */
	public <T extends ServiceApi> ISession startSession(IRemoteClient client, ServiceProvider<? extends ServiceApi> provider, ServiceApi api) {
		if(provider == null || api == null)
			return null;
		if(!uuidAgents.containsKey(provider.getAgent().getUuid()) || !uuidProviders.containsKey(provider.getUuid()) || !provider.providesApi(api)) {
			logger.warn("Trying to start session with invalid parameters! Client: " + client + ". Provider: " + provider + ". Api: " + api + "\n"
					+ "HasAgent: " + uuidAgents.containsKey(provider.getAgent().getUuid()) + ". HasProvider: " + uuidProviders.containsKey(provider.getUuid()) + ". "
							+ "ProvidesApi: " + provider.providesApi(api));
			return null;
		}
		
		//TODO: Do permission check for whether the user is allowed to start a session with this service/api/agent.
		
		ISession session = provider.getAgent().startSession(api, provider, client, getNextSessionId());
		addSession(session);
				
		return session;
	}
	
	public void addSession(ISession newSession) {
		if(sessions.containsKey(newSession.getSessionId())) {
			logger.error("Adding session which conflicts with existing session: " + newSession.getSessionId());
			newSession.endSession("Session ID conflicts with existing session!");
			return;
		}
		
		sessions.put(newSession.getSessionId(), newSession);
		newSession.addListener(new ISessionListener() {
			@Override
			public void onSessionEnded(String reason) {
				endSession(newSession.getSessionId(), reason);
			}
		});
		
	}
	
	/**
	 * Get the session with the given Session ID.
	 * @param sessionId
	 * @return The session if found, null if it does not exist
	 */
	public ISession getSession(int sessionId) {
		return sessions.get(sessionId);
	}
	
	/**
	 * End the session and remove it from the list of sessions.
	 * @param sessionId
	 * @param reason
	 */
	public void endSession(int sessionId, String reason) {
		ISession s = sessions.remove(sessionId);
		if(s == null)
			return;
		
		s.endSession(reason);
	}
	
	public void addAgent(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent) throws InvalidUUIDException {
		String uuid = agent.getUuid();
		if(!UUID.isUuidValid(uuid))
			throw new InvalidUUIDException(uuid);
		if(uuidAgents.containsKey(uuid))
			throw new InvalidUUIDException(uuid, true);
		
		//Keep UUID map up-to-date
		agent.addListener(new IAgentListener() {

			@Override
			public void onApiAdded(ServiceApi api) throws InvalidUUIDException {
				String id = api.getId();
				if(apis.containsKey(id))
					throw new InvalidUUIDException(id, true);
				
				logger.info("Registered new API(" + api + ") on Agent: " + agent);
				apis.put(id, api);
			}

			@Override
			public void onApiRemoved(ServiceApi api) {
				if(apis.remove(api.getId()) != null)
					logger.info("Removed API(" + api +") from Agent: " + agent);
			}

			@Override
			public void onProviderAdded(ServiceProvider<? extends ServiceApi> provider) throws InvalidUUIDException {
				String uuid = provider.getUuid();
				if(!UUID.isUuidValid(uuid))
					throw new InvalidUUIDException(uuid);
				if(uuidProviders.containsKey(uuid))
					throw new InvalidUUIDException(uuid, true);
				
				logger.info("Added provider(" + provider + ") on Agent: " + agent);
				uuidProviders.put(uuid, provider);
			}

			@Override
			public void onProviderRemoved(ServiceProvider<? extends ServiceApi> provider) {
				if(uuidProviders.remove(provider.getUuid()) != null)
					logger.info("Removed provider(" + provider + ") from Agent: " + agent);
			}
		});
		
		
		uuidAgents.put(uuid, agent);
	}
	
	/**
	 * Remove the given Agent.
	 * This will remove any Service APIs and Service Providers managed by this agent!
	 * Any session with the Service Providers will be ended!
	 * @return
	 */
	public boolean removeAgent(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent) {
		if(!uuidAgents.containsKey(agent.getUuid()))
			return false;
		
		//End any sessions with the Service Providers
		for(ISession session : agent.getSessions())
			session.endSession("Agent is being removed");
		
		uuidAgents.remove(agent.getUuid());
		return true;
	}
	
	public IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> getAgent(String uuid) {
		return uuidAgents.get(uuid);
	}
	
	public List<IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi>> getAgents() {
		return new ArrayList<>(uuidAgents.values());
	}
	
	public List<IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi>> getAgentsProviding(ServiceApi api) {
		List<IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi>> agentList = new ArrayList<>();
		
		for(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent : uuidAgents.values()) {
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
		for(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent : uuidAgents.values())
			serviceProviderList.addAll(agent.getServiceProviders(api));

		return serviceProviderList;
	}
	
	/**
	 * Get the Service Provider with the given UUID.
	 * @param uuid
	 * @return Null if not found.
	 */
	public ServiceProvider<? extends ServiceApi> getServiceProvider(String uuid) {
		return uuidProviders.get(uuid);
	}
	
	/**
	 * Get all Local Service Providers implementing the given ServiceApi
	 * @param api
	 * @return
	 */
	public List<LocalServiceProvider> getLocalServiceProviders(ServiceApi api) {
		List<LocalServiceProvider> serviceProviderList = new ArrayList<>();
		for(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent : uuidAgents.values()) {
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
		for(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent : uuidAgents.values())
			apiSet.addAll(agent.getServiceApiList());
		
		return new ArrayList<>(apiSet);
	}
	
	public List<LocalServiceApi> getLocalServiceApiList() {
		Set<LocalServiceApi> apiSet = new HashSet<>(); //Use Set to avoid duplicates
		for(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent : uuidAgents.values()) {
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
	public List<ServiceApi> getServiceApiWithName(String name) {
		List<ServiceApi> apiList = new ArrayList<>();
		
		for(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent : uuidAgents.values())
			apiList.addAll(agent.getServiceApiList(name));
		
		return apiList;
	}
	
	/**
	 * Get the Service Api with the given ID.
	 * @param id
	 * @return Null if not found.
	 */
	public ServiceApi getServiceApi(String id) {
		return apis.get(id);
	}
	
	/**
	 * Get all known local versions of the Service API
	 * @param name The full name of the Service API to get
	 * @return List of versions of the Service API currently known by any local agent.
	 */
	public List<LocalServiceApi> getLocalServiceApiWithName(String name) {
		List<LocalServiceApi> apiList = new ArrayList<>();
		
		for(IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent : uuidAgents.values()) {
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
	public LocalServiceApi getLocalServiceApi(Class<?> apiInterfaceClass) {
		//Get the Generated API class for the given Service API
		IServiceApiGenerated generatedApi;
		try {
			generatedApi = Generated.getGeneratedApi(apiInterfaceClass);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			logger.debug("Failed to get Local Service Api " + apiInterfaceClass + ": " + e);
			return null;
		}
		
		//Get API with the correct version
		List<LocalServiceApi> apiList = getLocalServiceApiWithName(generatedApi.getApiName());
		for(LocalServiceApi api : apiList) {
			if(api.getVersion() == generatedApi.getApiVersion())
				return api;
		}
		
		return null;
	}
	
	/**
	 * Create a new session id.
	 * 
	 * Note: If this reaches a negative number, it will overflow and start from 0.
	 * On overflow it might start getting collisions, which could lead to performance problems as it tries to resolve them.
	 * @return
	 */
	private int getNextSessionId() {
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

}
