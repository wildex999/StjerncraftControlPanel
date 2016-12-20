package com.stjerncraft.controlpanel.core.service;

import java.util.List;

import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.core.IAgent;
import com.stjerncraft.controlpanel.core.ServiceProvider;

public class LocalServiceProvider extends ServiceProvider<LocalServiceApi> {
	protected final IServiceProvider serviceProvider;
	
	public LocalServiceProvider(IAgent agent, IServiceProvider serviceProvider, List<LocalServiceApi> apiList) {
		super(agent, apiList);
		this.serviceProvider = serviceProvider;
	}
	
	public IServiceProvider getServiceProvider() {
		return serviceProvider;
	}

}
