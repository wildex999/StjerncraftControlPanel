package com.stjerncraft.controlpanel.core;

import java.util.List;

import com.stjerncraft.controlpanel.agent.IAgent;
import com.stjerncraft.controlpanel.agent.ServiceProvider;
import com.stjerncraft.controlpanel.api.IServiceManager;
import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.common.ServiceApi;
import com.stjerncraft.controlpanel.common.api.CoreApi;
import com.stjerncraft.controlpanel.common.data.AgentInfo;
import com.stjerncraft.controlpanel.common.data.ServiceApiInfo;
import com.stjerncraft.controlpanel.common.data.ServiceProviderInfo;

/**
 * The Core Service Provider, acting as the primary way of communication between a Client and the Core.
 * All Clients will start a session with this Service Provider when first starting up(How else would you get to know about the other Service Providers?).
 */
public class CoreService implements CoreApi, IServiceProvider {

	private Core core;
	
	public CoreService(Core core) {
		this.core = core;
	}
	
	@Override
	public void onRegister(IServiceManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnregister() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServiceApiInfo[] getAllServiceApis() {
		List<ServiceApi> apis = core.getServiceApiList();
		return apis.toArray(new ServiceApiInfo[apis.size()]);
	}

	@Override
	public ServiceApiInfo[] getServiceApis(String name) {
		List<ServiceApi> apis = core.getServiceApiWithName(name);
		return apis.toArray(new ServiceApiInfo[apis.size()]);
	}

	@Override
	public ServiceApiInfo getServiceApi(String name, int version) {
		ServiceApi api = core.getServiceApi(ServiceApiInfo.getId(name, version));
		return api;
	}

	@Override
	public ServiceProviderInfo[] getServiceProviders(String apiId) {
		ServiceApi api = core.getServiceApi(apiId);
		if(api == null)
			return new ServiceProviderInfo[0];
		
		List<ServiceProvider<? extends ServiceApi>> providers = core.getServiceProviders(api);
		return providers.toArray(new ServiceProviderInfo[providers.size()]);
	}

	@Override
	public ServiceProviderInfo getServiceProvider(String uuid) {
		ServiceProvider<? extends ServiceApi> provider = core.getServiceProvider(uuid);
		if(provider == null)
			return null;
		
		return provider.getInfo();
	}
	
	@Override
	public AgentInfo[] getAgents() {
		List<IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi>> agentList = core.getAgents();
		AgentInfo[] agentInfo = new AgentInfo[agentList.size()];
		for(int i = 0; i < agentList.size(); i++) {
			agentInfo[i] = agentList.get(i).getInfo();
		}
		
		return agentInfo;
	}
	
	@Override
	public AgentInfo getAgent(String uuid) {
		IAgent<? extends ServiceProvider<? extends ServiceApi>, ? extends ServiceApi> agent = core.getAgent(uuid);
		if(agent == null)
			return null;
		
		return agent.getInfo();
	}
}
