package com.stjerncraft.controlpanel.agent.local;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.stjerncraft.controlpanel.api.IClient;
import com.stjerncraft.controlpanel.api.IEventSubscription;
import com.stjerncraft.controlpanel.api.IUnsubscribeHandler;

/**
 * Event Subscription from Remote Client to a Local Service Provider
 */
public class LocalEventSubscription implements IEventSubscription {
	private LocalSession session;
	private int subscriptionId;
	private BiConsumer<Integer, String> eventCallback;
	private Consumer<Integer> unsubscribeCallback;
	private Function<Object, String> serializer;
	
	protected IUnsubscribeHandler unsubscribeHandler;
	
	public LocalEventSubscription(LocalSession session, int subscriptionId, BiConsumer<Integer, String> eventCallback, Consumer<Integer> unsubscribeCallback) {
		this.session = session;
		this.eventCallback = eventCallback;
		this.unsubscribeCallback = unsubscribeCallback;
	}
	
	public IUnsubscribeHandler getUnsubscribeHandler() {
		return unsubscribeHandler;
	}
	
	public Consumer<Integer> getUnsubscribeCallback() {
		return unsubscribeCallback;
	}
	
	@Override
	public IClient getClient() {
		return session.getRemoteClient();
	}

	@Override
	public void end() {
		session.callUnsubscribe(this);
	}
	
	@Override
	public void setDataSerializer(Function<Object, String> serializer) {
		this.serializer = serializer;
	}

	@Override
	public void sendEvent(Object data) {
		if(serializer == null)
			throw new RuntimeException("Missing serializer for Subscription " + subscriptionId + " on Session " + session.getSessionId());
		
		String dataJson = serializer.apply(data);
		eventCallback.accept(subscriptionId, dataJson);
	}
	
	@Override
	public int getSubscriptionId() {
		return subscriptionId;
	}

}
