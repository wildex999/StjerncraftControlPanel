package com.stjerncraft.controlpanel.agent.local;

import java.util.function.Consumer;

import com.stjerncraft.controlpanel.agent.IRemoteClient;
import com.stjerncraft.controlpanel.agent.ISession;
import com.stjerncraft.controlpanel.agent.ServiceApi;
import com.stjerncraft.controlpanel.agent.ServiceProvider;

/**
 * Session between a Local Service Provider and a Remote Client.
 */
public class LocalSession implements ISession {
	IRemoteClient client;
	LocalAgent agent;
	LocalServiceProvider serviceProvider;
	LocalServiceApi api;
	int sessionId;

	public LocalSession(LocalAgent agent, IRemoteClient client, LocalServiceProvider serviceProvider, LocalServiceApi api, int sessionId) {
		this.agent = agent;
		this.client = client;
		this.serviceProvider = serviceProvider;
		this.api = api;
		this.sessionId = sessionId;
	}
	
	@Override
	public int getSessionId() {
		return sessionId;
	}
	
	@Override
	public IRemoteClient getClient() {
		return client;
	}
	
	@Override
	public ServiceApi getServiceApi() {
		return api;
	}

	@Override
	public ServiceProvider<LocalServiceApi> getServiceProvider() {
		return serviceProvider;
	}

	@Override
	public void callMethod(String methodJson, Consumer<String> returnCallback) {
		String ret = api.getGeneratedApi().callMethod(serviceProvider.getServiceProvider(), methodJson);
		if(returnCallback != null)
			returnCallback.accept(ret);
	}

	@Override
	public void endSession(String reason) {
		agent.endSession(this, reason);
	}

}
