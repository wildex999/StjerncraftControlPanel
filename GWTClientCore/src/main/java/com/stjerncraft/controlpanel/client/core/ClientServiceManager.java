package com.stjerncraft.controlpanel.client.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.stjerncraft.controlpanel.api.client.IServiceApiInfo;
import com.stjerncraft.controlpanel.api.client.IServiceProviderInfo;
import com.stjerncraft.controlpanel.api.client.ServiceProviderPriority;
import com.stjerncraft.controlpanel.client.api.IClientServiceManager;
import com.stjerncraft.controlpanel.client.api.IServiceManagerEventHandler;
import com.stjerncraft.controlpanel.client.api.session.IClientSession;
import com.stjerncraft.controlpanel.client.api.session.ISessionListener;
import com.stjerncraft.controlpanel.client.api.session.SessionState;
import com.stjerncraft.controlpanel.common.Statics;
import com.stjerncraft.controlpanel.common.api.CoreApiLibrary;
import com.stjerncraft.controlpanel.common.data.AgentInfo;
import com.stjerncraft.controlpanel.common.data.IAgentInfo;
import com.stjerncraft.controlpanel.common.data.ServiceApiInfo;
import com.stjerncraft.controlpanel.common.data.ServiceProviderInfo;

/**
 * Manages an up-to-date list of available Service API's, Service Providers and Agents.
 * 
 * Note: It is assumed that the Core Server will manage breaking any existing sessions if Service Providers/Agents are removed.
 */
public class ClientServiceManager implements IClientServiceManager {
	static Logger logger = Logger.getLogger("ClientServiceManager");
	
	ClientCore clientCore;
	
	//TODO: Do some mapping of API -> Providers, API -> Agents etc. for faster lookup.
	Map<String, AgentInfo> agents; //UUID -> Agent
	Map<String, IServiceApiInfo> apis; //API ID -> Service API
	Map<String, IServiceProviderInfo> providers; //UUID -> Service Provider
	
	Set<IServiceManagerEventHandler> handlers;
	
	IClientSession session;
	CoreApiLibrary coreApi;
	ISessionListener sessionListener = new ISessionListener() {
		
		@Override
		public void onStarted(IClientSession session) {
			requestFullListFromServer();
		}
		
		@Override
		public void onRejected(IClientSession session) {}
		
		@Override
		public void onEnded(IClientSession session) {}
	};
	
	public ClientServiceManager(ClientCore clientCore) {
		this.clientCore = clientCore;
		
		agents = new HashMap<String, AgentInfo>();
		apis = new HashMap<String, IServiceApiInfo>();
		providers = new HashMap<String, IServiceProviderInfo>();
		
		handlers = new HashSet<IServiceManagerEventHandler>();
		
		if(clientCore == null) {
			logger.severe("Failed to load: Client Core is null!");
			return;
		}
	}
	
	public void setup() {
		//Listen for updates added/removed Service Providers, and API's added/removed from Service Providers(Can this happen?)
		
		logger.info("Starting Service Manager...");
		
		agents.clear();
		apis.clear();
		providers.clear();
		
		//Setup a default CoreApi Services, Provider and Agent
		ServiceApiInfo defaultCoreApi = new ServiceApiInfo(CoreApiLibrary.getName(), CoreApiLibrary.getVersion());
		ServiceApiInfo[] defaultCoreServices = new ServiceApiInfo[] {defaultCoreApi};
		ServiceProviderInfo defaultCoreServiceProvider = new ServiceProviderInfo(Statics.CORE_PROVIDER_UUID, 
				Statics.CORE_AGENT_UUID, ServiceProviderPriority.NORMAL, defaultCoreServices);
		AgentInfo defaultCoreAgent = new AgentInfo(Statics.CORE_AGENT_UUID, "CoreAgent", new ServiceProviderInfo[] {defaultCoreServiceProvider});
		updateFromAgents(new AgentInfo[] {defaultCoreAgent});
		
		//We use the Core API
		session = clientCore.getCoreSession();
		session.addListener(sessionListener);
		coreApi = new CoreApiLibrary(clientCore, session);
		
		if(session.getCurrentState() == SessionState.ACTIVE) {
			sessionListener.onStarted(session);
		}
	}
	
	@Override
	public void addEventHandler(IServiceManagerEventHandler handler) {
		handlers.add(handler);
	}

	@Override
	public void removeEventHandler(IServiceManagerEventHandler handler) {
		handlers.remove(handler);
	}
	
	@Override
	public List<IServiceProviderInfo> getProvidersForApi(IServiceApiInfo api) {
		List<IServiceProviderInfo> foundProviders = new ArrayList<>();
		
		for(IServiceProviderInfo provider : providers.values()) {
			for(IServiceApiInfo providerApi : provider.getApis()) {
				if(api.equals(providerApi)) {
					foundProviders.add(provider);
					break;
				}
			}
		}
		
		return foundProviders;
	}

	@Override
	public List<IServiceProviderInfo> getProvidersForApiOnAgent(IServiceApiInfo wantedApi, IAgentInfo preferredAgent) {
		List<IServiceProviderInfo> foundProviders = new ArrayList<>();
		
		IAgentInfo agent = agents.get(preferredAgent.getUuid());
		if(agent == null)
			return foundProviders;
		
		for(IServiceProviderInfo provider : agent.getProviders()) {
			for(IServiceApiInfo api : provider.getApis()) {
				if(api.equals(wantedApi)) {
					foundProviders.add(provider);
					break;
				}
			}
		}
		
		return foundProviders;
	}

	@Override
	public IServiceProviderInfo getBestProviderForApi(IServiceApiInfo wantedApi) {
		IServiceProviderInfo best = null;
		for(IServiceProviderInfo provider : providers.values()) {
			//No need to find the API if the provider has lower priority
			if(best != null && provider.getPriority().compareTo(best.getPriority()) > 0)
				continue;
			
			for(IServiceApiInfo api : provider.getApis()) {
				if(api.equals(wantedApi)) {
					best = provider;
					break;
				}
			}
		}
		
		return best;
	}

	@Override
	public IServiceProviderInfo getBestProviderForApiOnAgent(IServiceApiInfo wantedApi, IAgentInfo preferredAgent) {
		IAgentInfo agent = agents.get(preferredAgent.getUuid());
		if(agent == null)
			return null;
		
		IServiceProviderInfo best = null;
		for(IServiceProviderInfo provider : agent.getProviders()) {
			//No need to find the API if the provider has lower priority
			if(best != null && provider.getPriority().compareTo(best.getPriority()) > 0)
				continue;
			
			for(IServiceApiInfo api : provider.getApis()) {
				if(api.equals(wantedApi)) {
					best = provider;
					break;
				}
			}
		}
		
		return best;
	}
	
	/**
	 * The the full list of Agents and Service Providers from the Server
	 */
	private void requestFullListFromServer() {
		logger.info("Requesting Full update from server");
		
		//We only request Agents, as that will include all Service Providers, which will contain all implemented API's
		if(!coreApi.getAgents(this::onFullUpdateFromServer)) {
			//This should only happen if the Session is ended, so we will try again on session restart.
			logger.severe("Failed to request update from server!");
		}
	}
	
	/**
	 * Clear all current Agents, Apis and Service Providers, replacing them with those defined in the provided agents from the server.
	 * @param newAgents
	 */
	private void onFullUpdateFromServer(AgentInfo[] newAgents) {
		logger.info("Got Full Update from server with " + newAgents.length + " agents.");
		
		agents.clear();
		apis.clear();
		providers.clear();
		
		updateFromAgents(newAgents);
		
		for(IServiceManagerEventHandler handler : handlers) {
			handler.onFullUpdate(this);
		}
	}

	/**
	 * Add new Agents, Apis and Service Providers from the given list of Agents.
	 * @param updatedAgents
	 */
	private void updateFromAgents(AgentInfo[] updatedAgents) {
		for(AgentInfo agent : updatedAgents) {
			logger.info("Adding Agent: " + agent.getUuid());
			agents.put(agent.getUuid(), agent);
			
			//Get Service Providers
			for(IServiceProviderInfo provider : agent.getProviders()) {
				logger.info("Adding Service Provider: " + provider.getUuid());
				providers.put(provider.getUuid(), provider);
				
				//Get APIs
				for(IServiceApiInfo api : provider.getApis()) {
					logger.info("Adding Api: " + api.getId());
					apis.put(api.getId(), api);
				}
			}
		}
	}
}
