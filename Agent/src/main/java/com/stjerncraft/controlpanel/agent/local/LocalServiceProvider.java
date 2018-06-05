package com.stjerncraft.controlpanel.agent.local;

import java.util.List;
import java.util.UUID;

import com.stjerncraft.controlpanel.agent.ServiceProvider;
import com.stjerncraft.controlpanel.api.IServiceProvider;

public class LocalServiceProvider extends ServiceProvider<LocalServiceApi> {
	protected final IServiceProvider serviceProvider;
	
	public LocalServiceProvider(LocalAgent agent, IServiceProvider serviceProvider, List<LocalServiceApi> apiList) {
		super(agent, apiList, UUID.randomUUID().toString());
		this.serviceProvider = serviceProvider;
	}
	
	public IServiceProvider getServiceProvider() {
		return serviceProvider;
	}

}
