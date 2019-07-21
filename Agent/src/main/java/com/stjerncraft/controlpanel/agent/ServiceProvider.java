package com.stjerncraft.controlpanel.agent;

import java.util.List;
import java.util.UUID;

import com.stjerncraft.controlpanel.api.client.ServiceProviderPriority;
import com.stjerncraft.controlpanel.common.ServiceApi;
import com.stjerncraft.controlpanel.common.data.ServiceApiInfo;
import com.stjerncraft.controlpanel.common.data.ServiceProviderInfo;

/**
 * Information about a Service Provider which exists with an Agent
 */
public class ServiceProvider<T extends ServiceApi> {
	final protected List<T> apiList;
	final protected IAgent<? extends ServiceProvider<T>, T> agent;
	final protected String uuid;
	final protected ServiceProviderPriority priority;
	
	public ServiceProvider(IAgent<? extends ServiceProvider<T>, T> agent, List<T> apiList, String uuid) {
		this.apiList = apiList;
		this.agent = agent;
		
		if(uuid == null)
			this.uuid = UUID.randomUUID().toString();
		else
			this.uuid = uuid;
		
		//TODO: Set Priority in config
		//TODO: Allow Per API priority
		this.priority = ServiceProviderPriority.NORMAL;
	}
	
	public List<T> getApiList() {
		return apiList;
	}
	
	public boolean providesApi(ServiceApi hasApi) {
		for(T api : apiList) {
			if(api == hasApi)
				return true;
		}
		
		return false;
	}
	
	public IAgent<? extends ServiceProvider<T>, T> getAgent() {
		return agent;
	}
	
	/**
	 * Get the UUID of this Service Provider
	 * @return
	 */
	public String getUuid() {
		return uuid;
	}
	
	public ServiceProviderInfo getInfo() {
		//TODO: Cache this?
		ServiceApiInfo[] apis = apiList.toArray(new ServiceApiInfo[apiList.size()]);
		ServiceProviderInfo info = new ServiceProviderInfo(getUuid(), agent.getUuid(), priority, apis);
		return info;
	}
	
	@Override
	public String toString() {
		return uuid;
	}
}
