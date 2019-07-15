package com.stjerncraft.controlpanel.agent.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;
import com.stjerncraft.controlpanel.agent.IAgent;
import com.stjerncraft.controlpanel.agent.IAgentListener;
import com.stjerncraft.controlpanel.agent.IRemoteClient;
import com.stjerncraft.controlpanel.agent.ISession;
import com.stjerncraft.controlpanel.agent.ISessionListener;
import com.stjerncraft.controlpanel.agent.ServiceProvider;
import com.stjerncraft.controlpanel.api.IServiceApiGenerated;
import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.api.processor.ApiStrings;
import com.stjerncraft.controlpanel.common.ServiceApi;
import com.stjerncraft.controlpanel.common.util.ListenerHandler;

/**
 * Agent for local Service Providers.
 */

public class LocalAgent implements IAgent<LocalServiceProvider, LocalServiceApi> {
	protected String name;
	protected String uuid;
	
	Map<Class<?>, LocalServiceApi> apiRegister;
	Map<IServiceProvider, LocalServiceProvider> serviceProviders;
	Map<String, LocalServiceApi> apiNameRegister;
	Map<LocalServiceApi, Set<LocalServiceProvider>> apiServiceProviders;
	
	Set<LocalSession> sessions;
	ListenerHandler<IAgentListener> listeners;
	LocalServiceManager serviceManager;
	
	/**
	 * 
	 * @param name
	 * @param uuid If null a random one will be generated
	 */
	public LocalAgent(String name, String uuid) {
		this.name = name;
		if(uuid != null)
			this.uuid = uuid;
		else
			this.uuid = UUID.randomUUID().toString();
		
		apiRegister = new HashMap<>();
		apiNameRegister = new HashMap<>();
		serviceProviders = new HashMap<>();
		apiServiceProviders = new HashMap<>();
		
		sessions = new HashSet<>();
		listeners = new ListenerHandler<>();
		
		serviceManager = new LocalServiceManager(this);
	}
	
	/**
	 * Add the given Service Provider to the Agent.
	 * @param serviceProvider Service Provider implementing one or more Service API's.
	 * @param uuid The UUID to assign to this Service Provider. Null to generate it.
	 * @throws IllegalArgumentException If the given serviceProvider implements no Service API, or it fails to register(Conflict etc.).
	 */
	public void addServiceProvider(IServiceProvider serviceProvider, String uuid) throws IllegalArgumentException {
		List<LocalServiceApi> apiList = parseServiceApis(serviceProvider.getClass());
		if(apiList.isEmpty())
			throw new IllegalArgumentException("The Service Provider " + serviceProvider + " implements no valid Service API!");
		
		LocalServiceProvider newServiceProvider = new LocalServiceProvider(this, serviceProvider, apiList, uuid);
		serviceProviders.put(serviceProvider, newServiceProvider);
		String err = listeners.runTry(l -> {
			try {
				l.onProviderAdded(newServiceProvider);
			} catch (Exception e) {
				return e.getMessage();
			}
			
			return null;
		});
		
		if(err != null) {
			removeServiceProvider(serviceProvider);
			throw new IllegalArgumentException("Failed to register Service Provider " + serviceProvider + ": " + err);
		}
		
		//Map API to Service Providers
		for(LocalServiceApi api : apiList) {
			Set<LocalServiceProvider> providers = apiServiceProviders.get(api);
			if(providers == null) {
				providers = new HashSet<>();
				apiServiceProviders.put(api, providers);
			}
			providers.add(newServiceProvider);
			err = listeners.runTry(l -> {
				try {
					l.onApiAdded(api);
				} catch (Exception e) {
					return e.getMessage();
				}
				
				return null;
			});
			
			if(err != null) {
				removeServiceProvider(serviceProvider);
				throw new IllegalArgumentException("Failed to register API " + api + " for Service Provider " + serviceProvider + ": " + err);
			}
		}
		
		serviceProvider.onRegister(serviceManager);
	}
	
	public void removeServiceProvider(IServiceProvider serviceProvider) {
		LocalServiceProvider provider = serviceProviders.get(serviceProvider);
		if(provider != null)
			removeServiceProvider(provider);
	}
	
	public void removeServiceProvider(LocalServiceProvider serviceProvider) {			
		if(serviceProviders.remove(serviceProvider.getServiceProvider()) == null)
				return;
		
		listeners.run(l -> l.onProviderRemoved(serviceProvider));
		
		//Remove from API map
		for(LocalServiceApi api : serviceProvider.getApiList()) {
			Set<LocalServiceProvider> providers = apiServiceProviders.get(api);
			if(providers == null)
				continue;
			
			if(providers.remove(serviceProvider))
				listeners.run(l -> l.onApiRemoved(api));
			if(providers.isEmpty())
				apiServiceProviders.remove(api);
		}
		
		//End all Sessions for the given Service Provider
		List<LocalSession> sessionsToEnd = new ArrayList<>();
		for(LocalSession session : sessions) {
			if(session.getServiceProvider() == serviceProvider)
				sessionsToEnd.add(session);
		}
		for(LocalSession session : sessionsToEnd)
			endSession(session, "Service Provider is removed.");
		
		serviceProvider.getServiceProvider().onUnregister();
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getUuid() {
		return uuid;
	}
	
	@Override
	public List<LocalServiceApi> getServiceApiList() {
		return new ArrayList<>(apiRegister.values());
	}

	@Override
	public List<LocalServiceApi> getServiceApiList(String apiName) {
		List<LocalServiceApi> apiList = new ArrayList<>();
		for(LocalServiceApi api : apiRegister.values()) {
			if(Objects.equal(api.getName(), apiName))
				apiList.add(api);
		}
		
		return apiList;
	}

	@Override
	public List<LocalServiceProvider> getServiceProviders() {
		return new ArrayList<>(serviceProviders.values());
	}

	@Override
	public List<LocalServiceProvider> getServiceProviders(ServiceApi api) {
		return new ArrayList<>(apiServiceProviders.get(api));
	}
	
	@Override
	public List<ISession> getSessions() {
		return new ArrayList<ISession>(sessions);
	}

	@Override
	public boolean providesApi(ServiceApi api) {
		return apiServiceProviders.containsKey(api);
	}
	
	@Override
	public void addListener(IAgentListener listener) {
		listeners.add(listener);
	}

	@Override
	public boolean removeListener(IAgentListener listener) {
		return listeners.remove(listener);
	}

	@Override
	public ISession startSession(ServiceApi api, ServiceProvider<? extends ServiceApi> service, IRemoteClient client, int sessionId) {
		if(!(service instanceof LocalServiceProvider))
			return null;
		LocalServiceProvider localService = (LocalServiceProvider)service;
		LocalServiceApi localApi = (LocalServiceApi)api;
		
		if(!serviceProviders.containsKey(localService.getServiceProvider()))
			return null;
		if(!apiServiceProviders.containsKey(api))
			return null;
		
		LocalSession newSession = new LocalSession(this, client, localService, localApi, sessionId);
		newSession.addListener(new ISessionListener() {
			@Override
			public void onSessionEnded(String reason) {
				endSession(newSession, reason);
			}
		});
		
		sessions.add(newSession);
		newSession.startSession();
		
		return newSession;
	}
	
	/**
	 * End the given session if it exists.
	 * @param session Session to end.
	 * @param reason Reason for ending the session. Can be null.
	 */
	public void endSession(LocalSession session, String reason) {
		if(!sessions.remove(session))
			return;
		
		session.endSession(reason);
	}
	
	/**
	 * Find all the Service API's implemented by the given Service Provider and add them to the API Register.
	 * @param serviceClass The class of a Service Provider implementation.
	 * @return A Set containing all the Service API's implemented by the Service Provider.
	 */
	protected List<LocalServiceApi> parseServiceApis(Class<? extends IServiceProvider> serviceClass) {
		//Get all direct and inherited interfaces for this class(Including interfaces extended by interfaces)
		TypeToken<?>.TypeSet interfaceSet = TypeToken.of(serviceClass).getTypes().interfaces();
		
		Set<LocalServiceApi> apiSet = new HashSet<>();
		for(Class<?> apiInterface : interfaceSet.rawTypes()) {
			//Only care for interfaces DIRECTLY annotated as a ServiceApi
			if(apiInterface.getDeclaredAnnotation(com.stjerncraft.controlpanel.api.annotation.ServiceApi.class) == null)
				continue;

			//Check for Generated API
			LocalServiceApi newApi = apiRegister.get(apiInterface);
			if(newApi != null) {
				apiSet.add(newApi);
				continue;
			}
			
			try {
				//Find the generated class, instantiate it and add it to the Agents API register
				Class<?> clazz = Class.forName(apiInterface.getCanonicalName() + ApiStrings.APISUFFIX);
				IServiceApiGenerated generatedClass = (IServiceApiGenerated)clazz.newInstance();
				
				newApi = new LocalServiceApi(apiInterface, generatedClass);
				apiRegister.put(apiInterface, newApi);
				apiNameRegister.put(generatedClass.getApiName(), newApi);
				apiSet.add(newApi);
			} catch (ClassNotFoundException | ClassCastException | InstantiationException | IllegalAccessException e) {
				System.err.println("Error while parsing Service API " + apiInterface + ": " + e);
				continue;	
			}
		}
		
		return new ArrayList<>(apiSet);
	}

}
