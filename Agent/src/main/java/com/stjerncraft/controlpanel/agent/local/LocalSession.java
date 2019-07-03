package com.stjerncraft.controlpanel.agent.local;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.stjerncraft.controlpanel.agent.IRemoteClient;
import com.stjerncraft.controlpanel.agent.ISession;
import com.stjerncraft.controlpanel.agent.ISessionListener;
import com.stjerncraft.controlpanel.agent.ServiceProvider;
import com.stjerncraft.controlpanel.api.EventAction;
import com.stjerncraft.controlpanel.api.IEventSubscription;
import com.stjerncraft.controlpanel.common.ServiceApi;
import com.stjerncraft.controlpanel.common.util.ListenerHandler;

/**
 * Session between a Local Service Provider and a Remote Client.
 */
public class LocalSession implements ISession {
	IRemoteClient client;
	LocalAgent agent;
	LocalServiceProvider serviceProvider;
	LocalServiceApi api;
	int sessionId;
	
	boolean exists;
	boolean hasStarted;
	
	ListenerHandler<ISessionListener> listeners;
	Map<Integer, IEventSubscription> eventSubscriptions;

	public LocalSession(LocalAgent agent, IRemoteClient client, LocalServiceProvider serviceProvider, LocalServiceApi api, int sessionId) {
		this.agent = agent;
		this.client = client;
		this.serviceProvider = serviceProvider;
		this.api = api;
		this.sessionId = sessionId;
		
		exists = true;
		hasStarted = false;
		listeners = new ListenerHandler<>();
		eventSubscriptions = new HashMap<>();
	}
	
	@Override
	public int getSessionId() {
		return sessionId;
	}
	
	@Override
	public IRemoteClient getRemoteClient() {
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
	public boolean hasStarted() {
		return hasStarted;
	}
	
	@Override
	public void addListener(ISessionListener listener) {
		listeners.add(listener);
	}
	
	@Override
	public void removeListener(ISessionListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void callMethod(String methodJson, Consumer<String> returnCallback) {
		if(!exists || !hasStarted)
			return;
		
		//TODO: Setup the Context in ServiceManager
		
		String ret = api.getGeneratedApi().callMethod(serviceProvider.getServiceProvider(), methodJson);
		if(returnCallback != null)
			returnCallback.accept(ret);
	}
	
	@Override
	public IEventSubscription eventSubscribe(String methodJson) {
		if(!exists || !hasStarted)
			return null;
		
		//Setup the Context in ServiceManager
		LocalServiceManager manager = agent.serviceManager;
		LocalEventSubscription subscription = new LocalEventSubscription(this);
		manager.setEventContext(EventAction.Subscribe, subscription);
		
		boolean ret = api.getGeneratedApi().callEventHandler(serviceProvider.getServiceProvider(), methodJson);
		
		if(!ret) {
			//TODO: Remove subscription
			return null;
		}
		
		//TODO: Return Event Subscription
		return null;
	}
	
	@Override
	public void eventUnsubscribe(IEventSubscription subscription) {
		if(!exists || !hasStarted)
			return;
		
		//Setup the Context in ServiceManager
		LocalServiceManager manager = agent.serviceManager;
		manager.setEventContext(EventAction.Unsubscribe, subscription);
		
		//TODO: Call method
	}

	@Override
	public void startSession() {
		if(!exists)
			return;
		
		hasStarted = true;
		listeners.run(l -> l.onSessionStarted());
	}
	
	@Override
	public void endSession(String reason) {
		if(!exists)
			return;
		
		//TODO: End subscriptions
		
		exists = false;
		hasStarted = false;
		listeners.run(l -> l.onSessionEnded(reason));
	}

}
