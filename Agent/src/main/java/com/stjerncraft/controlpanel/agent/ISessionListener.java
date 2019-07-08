package com.stjerncraft.controlpanel.agent;

/**
 * NOTE: These methods are NOT thread safe. They should always be called on the main thread!
 */
public interface ISessionListener {
	/**
	 * Called by the Agent when the session with the Client has been accepted.
	 * Before this the session is not valid for use.
	 */
	default void onSessionStarted() {};
	
	/**
	 * Called by the Agent when the session with the Client is ended.
	 * The session is invalid at this point, and can not be used for further communication.
	 */
	void onSessionEnded(String reason);
}
