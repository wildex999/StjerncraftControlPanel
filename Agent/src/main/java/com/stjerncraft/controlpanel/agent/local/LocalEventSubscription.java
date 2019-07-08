package com.stjerncraft.controlpanel.agent.local;

import com.stjerncraft.controlpanel.api.IClient;
import com.stjerncraft.controlpanel.api.IEventSubscription;

/**
 * Event Subscription from Remote Client to a Local Service Provider
 */
public class LocalEventSubscription implements IEventSubscription {
	private LocalSession session;
	private int subscriptionId;
	
	public LocalEventSubscription(LocalSession session, int subscriptionId) {
		this.session = session;
	}
	
	@Override
	public IClient getClient() {
		return session.getRemoteClient();
	}

	@Override
	public void end() {
		session.eventUnsubscribe(this);
	}

	@Override
	public void sendEvent(Object data) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getSubscriptionId() {
		return subscriptionId;
	}

}
