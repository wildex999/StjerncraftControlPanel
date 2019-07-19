package com.stjerncraft.controlpanel.client.api.session;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface ISessionListener {
	/**
	 * Service Provider has accepted the session.
	 * If the session is rejected, onRejected is called instead.
	 */
	void onStarted(IClientSession session);
	
	/**
	 * Service Provider did not accept the session, or something else caused it to fail(Disconnect etc.)
	 */
	void onRejected(IClientSession session);
	
	/**
	 * Session is ended either locally or remotely.
	 * Called before the cleanup, so Subscriptions are still listed.
	 */
	void onEnded(IClientSession session);
}
