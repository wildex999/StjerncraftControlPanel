package com.stjerncraft.controlpanel.module.core.session;

public interface ISessionListener {
	/**
	 * Service Provider has accepted the session.
	 * If the session is rejected, onRejected is called instead.
	 */
	void onStarted(ClientSession session);
	
	/**
	 * Service Provider did not accept the session, or something else caused it to fail(Disconnect etc.)
	 */
	void onRejected(ClientSession session);
	
	/**
	 * Session is ended either locally or remotely.
	 * Called before the cleanup, so Subscriptions are still listed.
	 */
	void onEnded(ClientSession session);
}
