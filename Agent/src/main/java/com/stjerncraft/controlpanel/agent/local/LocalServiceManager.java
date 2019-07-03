package com.stjerncraft.controlpanel.agent.local;

import java.util.List;

import com.stjerncraft.controlpanel.api.EventAction;
import com.stjerncraft.controlpanel.api.IClient;
import com.stjerncraft.controlpanel.api.IEventSubscription;
import com.stjerncraft.controlpanel.api.IServiceManager;
import com.stjerncraft.controlpanel.api.IServiceProvider;

/**
 * Service Manager for local Service Providers.
 */

public class LocalServiceManager implements IServiceManager {
	LocalAgent agent;
	EventAction eventAction;
	IEventSubscription eventSubscription;
	IClient client;
	
	public LocalServiceManager(LocalAgent agent) {
		this.agent = agent;
	}
	
	/**
	 * Set the current User calling method/event.
	 * @param client
	 */
	public void setUser(IClient client) {
		this.client = client;
	}
	
	/**
	 * Set the current EvenHandler context
	 * @param action
	 * @param subscription
	 */
	public void setEventContext(EventAction action, IEventSubscription subscription) {
		this.eventAction = action;
		this.eventSubscription = subscription;
	}
	
	@Override
	public IClient getClient() {
		return client;
	}

	@Override
	public IEventSubscription getEventSubscription() {
		return eventSubscription;
	}

	@Override
	public EventAction getEventAction() {
		return eventAction;
	}

	@Override
	public <T extends IServiceProvider> T getService(Class<T> apiInterfaceClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends IServiceProvider> List<T> getServices(Class<T> apiInterfaceClass) {
		// TODO Auto-generated method stub
		return null;
	}

}
