package com.stjerncraft.controlpanel.module.core.client;

import com.stjerncraft.controlpanel.api.client.IClientSubscriptionHandler;
import com.stjerncraft.controlpanel.module.core.session.ClientSession;

import jsinterop.annotations.JsType;

@JsType
public class ClientSubscription {
	private ClientSession session;
	private int subscriptionId;
	private IClientSubscriptionHandler<String> handler;
	
	public ClientSubscription(ClientSession session, int subscriptionId, IClientSubscriptionHandler<String> handler) {
		this.session = session;
		this.subscriptionId = subscriptionId;
		this.handler = handler;
	}
	
	public ClientSession getSession() {
		return session;
	}
	
	public int getSubscriptionId() {
		return subscriptionId;
	}
	
	public IClientSubscriptionHandler<String> getHandler() {
		return handler;
	}
}
