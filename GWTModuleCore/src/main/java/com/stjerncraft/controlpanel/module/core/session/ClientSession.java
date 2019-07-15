package com.stjerncraft.controlpanel.module.core.session;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.stjerncraft.controlpanel.common.data.ServiceApiInfo;
import com.stjerncraft.controlpanel.common.data.ServiceProviderInfo;

public class ClientSession {
	static Logger logger = Logger.getLogger("ClientSessions");
	
	private SessionState state;
	private int sessionId;
	private Set<ISessionListener> listeners;
	//private Set<ISubscription> subscriptions;
	private ServiceApiInfo api;
	private ServiceProviderInfo serviceProvider;
	
	/**
	 * @param socket Valid connection the Core Server.
	 * @param api
	 * @param serviceProvider
	 */
	public ClientSession(ServiceApiInfo api, ServiceProviderInfo serviceProvider, ISessionListener listener) {
		this.api = api;
		this.serviceProvider = serviceProvider;
		
		listeners = new HashSet<ISessionListener>();
		state = SessionState.IDLE;
		if(listener != null)
			addListener(listener);
	}
	
	public void addListener(ISessionListener listener) {
		listeners.add(listener);
	}
	
	public SessionState getCurrentState() {
		return state;
	}
	
	public ServiceApiInfo getApi() {
		return api;
	}
	
	public ServiceProviderInfo getServiceProvider() {
		return serviceProvider;
	}
	
	public int getSessionId() {
		return sessionId;
	}
	
	/**
	 * Called when Core has sent Session Start request
	 */
	public void onPending() {
		state = SessionState.PENDING;
	}
	
	/**
	 * Called when Service Provider has accepted the session.
	 * If the session is rejected, onRejected is called instead.
	 * @param newSessionId The Session ID assigned to this session.
	 */
	public void onStarted(int newSessionId) {
		logger.info("Started Session(" + newSessionId + ") with Service Provider " + serviceProvider.uuid + " for API " + api.name);
		state = SessionState.ACTIVE;
		sessionId = newSessionId;
		listeners.forEach(listener -> { listener.onStarted(this); });
	}
	
	/**
	 * Called when the Service Provider did not accept the session, or something else caused it to fail(Disconnect etc.)
	 */
	public void onRejected() {
		logger.info("Session with Service Provider " + serviceProvider.uuid + " for API " + api.name + " was rejected!");
		state = SessionState.REJECTED;
		listeners.forEach(listener -> { listener.onRejected(this); });
	}
	
	/**
	 * Called when the Session is ended either locally or remotely.
	 * This will do cleanup and end all Subscriptions.
	 */
	public void onEnded() {
		logger.info("Ended Session " + sessionId);
		state = SessionState.INACTIVE;
		listeners.forEach(listener -> { listener.onEnded(this); });
		
		//TODO: End subscriptions
	}
}
