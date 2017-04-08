package com.stjerncraft.controlpanel.core.service;

import java.util.function.Function;

import com.stjerncraft.controlpanel.core.ISession;
import com.stjerncraft.controlpanel.core.ServiceProvider;
import com.stjerncraft.controlpanel.core.api.ServiceApi;
import com.stjerncraft.controlpanel.core.client.IClient;

public class LocalSession implements ISession {
	IClient client;
	LocalAgent agent;
	LocalServiceProvider serviceProvider;

	public LocalSession(LocalAgent agent, IClient client, LocalServiceProvider serviceProvider) {
		this.agent = agent;
		this.client = client;
		this.serviceProvider = serviceProvider;
	}
	
	@Override
	public IClient getClient() {
		return client;
	}

	@Override
	public ServiceProvider<? extends ServiceApi> getServiceProvider() {
		return serviceProvider;
	}

	@Override
	public void callMethod(String methodJson, Function<String, Void> returnCallback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endSession(String reason) {
		agent.endSession(this, reason);
	}

}
