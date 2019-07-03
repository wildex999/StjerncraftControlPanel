package com.stjerncraft.controlpanel.agent.local;

import com.stjerncraft.controlpanel.api.IClient;
import com.stjerncraft.controlpanel.api.IEventSubscription;

/**
 * Event Subscription from Remote Client to a Local Service Provider
 */
public class LocalEventSubscription implements IEventSubscription {
	LocalSession session;
	
	public LocalEventSubscription(LocalSession session) {
		this.session = session;
	}
	
	@Override
	public IClient getClient() {
		return session.getRemoteClient();
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEvent(Object data) {
		// TODO Auto-generated method stub
		
	}

}
