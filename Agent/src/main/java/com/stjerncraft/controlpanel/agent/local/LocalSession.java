package com.stjerncraft.controlpanel.agent.local;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.stjerncraft.controlpanel.agent.IRemoteClient;
import com.stjerncraft.controlpanel.agent.ISession;
import com.stjerncraft.controlpanel.agent.ISessionListener;
import com.stjerncraft.controlpanel.agent.ServiceProvider;
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
	Map<Integer, LocalEventSubscription> eventSubscriptions; //Key: Subscription ID
	int subscriptionIdCounter;

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
		subscriptionIdCounter = 0;
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
		
		//Setup the Context in ServiceManager
		LocalServiceManager manager = agent.serviceManager;
		manager.setClient(client);
		
		String ret = api.getGeneratedApi().callMethod(serviceProvider.getServiceProvider(), methodJson);
		if(returnCallback != null)
			returnCallback.accept(ret);
	}
	
	@Override
	public void callSubscribe(String methodJson, Consumer<Integer> subscribeCallback, BiConsumer<Integer, String> eventCallback, Consumer<Integer> unsubscribeCallback) {
		if(!exists || !hasStarted) {
			if(subscribeCallback != null)
				subscribeCallback.accept(null);
			return;
		}
		
		//Setup the Context in ServiceManager
		LocalServiceManager manager = agent.serviceManager;
		LocalEventSubscription subscription = new LocalEventSubscription(this, subscriptionIdCounter++, eventCallback, unsubscribeCallback);
		manager.setClient(client);
		manager.setEventContext(subscription);
		
		//Handle overflow. This is either a bug, or someone is abusing the server.
		if(subscriptionIdCounter < 0)
			throw new RuntimeException("Subscription ID overflow!");
		
		subscription.unsubscribeHandler = api.getGeneratedApi().callEventHandler(serviceProvider.getServiceProvider(), methodJson, subscription);
		if(subscription.unsubscribeHandler == null) {
			//Subscription was denied
			subscriptionIdCounter--; //TODO: The fact that the ID's of failed requests can be re-used should be properly explained
			if(subscribeCallback != null)
				subscribeCallback.accept(null);
			return;
		}
		
		eventSubscriptions.put(subscription.getSubscriptionId(), subscription);
		if(subscribeCallback != null)
			subscribeCallback.accept(subscription.getSubscriptionId());
	}
	
	@Override
	public void callUnsubscribe(IEventSubscription subscription) {
		if(!exists || !hasStarted)
			return;
		
		LocalEventSubscription localSubscription = eventSubscriptions.remove(subscription.getSubscriptionId());
		if(localSubscription == null)
			return;
		
		//Setup the Context in ServiceManager
		LocalServiceManager manager = agent.serviceManager;
		manager.setClient(client);
		manager.setEventContext(localSubscription);
		
		localSubscription.unsubscribeHandler.onUnsubscribe(localSubscription);
		localSubscription.getUnsubscribeCallback().accept(localSubscription.getSubscriptionId());
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
