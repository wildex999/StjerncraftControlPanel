package com.stjerncraft.controlpanel.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;
import com.stjerncraft.controlpanel.api.IServiceApiGenerated;
import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.api.processor.ApiStrings;
import com.stjerncraft.controlpanel.core.IAgent;
import com.stjerncraft.controlpanel.core.ISession;
import com.stjerncraft.controlpanel.core.ServiceProvider;
import com.stjerncraft.controlpanel.core.api.ServiceApi;
import com.stjerncraft.controlpanel.core.client.IClient;

/**
 * Agent for local Service Providers.
 */

public class LocalAgent implements IAgent {
	protected String name;
	
	Map<Class<? extends IServiceProvider>, LocalServiceApi> apiRegister;
	Map<IServiceProvider, LocalServiceProvider> serviceProviders;
	Map<String, LocalServiceApi> apiNameRegister;
	Map<LocalServiceApi, Set<LocalServiceProvider>> apiServiceProviders;
	
	Set<LocalSession> sessions;
	
	public LocalAgent(String name) {
		this.name = name;
		
		apiRegister = new HashMap<>();
		apiNameRegister = new HashMap<>();
		serviceProviders = new HashMap<>();
		apiServiceProviders = new HashMap<>();
		
		sessions = new HashSet<>();
	}
	
	/**
	 * Add the given Service Provider to the Agent.
	 * @param serviceProvider Service Provider implementing one or more Service API's.
	 * @throws IllegalArgumentException If the given serviceProvider implements no Service API.
	 */
	public void addServiceProvider(IServiceProvider serviceProvider) throws IllegalArgumentException {
		List<LocalServiceApi> apiList = parseServiceApis(serviceProvider.getClass());
		if(apiList.isEmpty())
			throw new IllegalArgumentException("The Service Provider " + serviceProvider + " implements no valid Service API!");
		
		LocalServiceProvider newServiceProvider = new LocalServiceProvider(this, serviceProvider, apiList);
		serviceProviders.put(serviceProvider, newServiceProvider);
		
		//Map API to Service Providers
		for(LocalServiceApi api : apiList) {
			Set<LocalServiceProvider> providers = apiServiceProviders.get(api);
			if(providers == null) {
				providers = new HashSet<>();
				apiServiceProviders.put(api, providers);
			}
			providers.add(newServiceProvider);
		}
	}
	
	public void removeServiceProvider(IServiceProvider serviceProvider) {
		LocalServiceProvider provider = serviceProviders.get(serviceProvider);
		if(provider != null)
			removeServiceProvider(provider);
	}
	
	public void removeServiceProvider(LocalServiceProvider serviceProvider) {	
		if(!serviceProviders.containsKey(serviceProvider.getServiceProvider()))
			return;
		
		serviceProviders.remove(serviceProvider.getServiceProvider());
		
		//Remove from API map
		for(LocalServiceApi api : serviceProvider.getApiList()) {
			Set<LocalServiceProvider> providers = apiServiceProviders.get(api);
			if(providers == null)
				continue;
			
			providers.remove(serviceProvider);
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
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public List<ServiceApi> getServiceApiList() {
		return new ArrayList<>(apiRegister.values());
	}

	@Override
	public List<ServiceApi> getServiceApiList(String apiName) {
		List<ServiceApi> apiList = new ArrayList<>();
		for(LocalServiceApi api : apiRegister.values()) {
			if(Objects.equal(api.getName(), apiName))
				apiList.add(api);
		}
		
		return apiList;
	}

	@Override
	public List<ServiceProvider<? extends ServiceApi>> getServiceProviders() {
		return new ArrayList<>(serviceProviders.values());
	}

	@Override
	public List<ServiceProvider<? extends ServiceApi>> getServiceProviders(ServiceApi api) {
		return new ArrayList<>(apiServiceProviders.get(api));
	}

	@Override
	public boolean providesApi(ServiceApi api) {
		return apiServiceProviders.containsKey(api);
	}

	@Override
	public ISession startSession(ServiceProvider<? extends ServiceApi> service, IClient client) {
		if(!(service instanceof LocalServiceProvider))
			return null;
		LocalServiceProvider localService = (LocalServiceProvider)service;
		
		if(!serviceProviders.containsKey(localService.getServiceProvider()))
			return null;
		
		LocalSession newSession = new LocalSession(this, client, localService);
		sessions.add(newSession);
		
		return newSession;
	}
	
	/**
	 * End the given session.
	 * @param session Session to end.
	 * @param reason Reason for ending the session. Can be null.
	 */
	public void endSession(LocalSession session, String reason) {
		if(!sessions.remove(session))
			return;
		
		session.getClient().onSessionEnd(session, reason);
	}
	
	/**
	 * Find all the Service API's implemented by the given Service Provider and add them to the API Register.
	 * @param serviceClass The class of a Service Provider implementation.
	 * @return A Set containing all the Service API's implemented by the Service Provider.
	 */
	@SuppressWarnings({"unchecked"})
	protected List<LocalServiceApi> parseServiceApis(Class<? extends IServiceProvider> serviceClass) {
		TypeToken<? extends IServiceProvider>.TypeSet interfaceSet = TypeToken.of(serviceClass).getTypes().interfaces();
		
		Set<LocalServiceApi> apiSet = new HashSet<>();
		for(Class<?> typeClass : interfaceSet.rawTypes()) {
			Class<? extends IServiceProvider> apiInterface = (Class<? extends IServiceProvider>)typeClass;
			
			//Check if any of it's DIRECT implemented interfaces are IServiceProvider
			for(Class<?> impl : apiInterface.getInterfaces() ) {
				if(impl != IServiceProvider.class)
					continue;

				//Check for Generated API
				LocalServiceApi newApi = apiRegister.get(apiInterface);
				if(newApi != null) {
					apiSet.add(newApi);
					continue;
				}
				
				try {
					Class<?> clazz = Class.forName(apiInterface.getCanonicalName() + ApiStrings.APISUFFIX);
					IServiceApiGenerated generatedClass = (IServiceApiGenerated)clazz.newInstance();
					
					newApi = new LocalServiceApi(apiInterface, generatedClass);
					apiRegister.put(apiInterface, newApi);
					apiNameRegister.put(generatedClass.getApiName(), newApi);
					apiSet.add(newApi);
				} catch (ClassNotFoundException | ClassCastException | InstantiationException | IllegalAccessException e) {
					continue;	
				}
			}
		}
		
		return new ArrayList<>(apiSet);
	}

}
