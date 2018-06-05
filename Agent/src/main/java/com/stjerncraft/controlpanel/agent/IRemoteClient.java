package com.stjerncraft.controlpanel.agent;

public interface IRemoteClient {
	/**
	 * Called by the Agent when a session with the Client is ended.
	 * The session is invalid at this point, and can not be used for further communication.
	 * Note: This should not be called directly to end a session!
	 * @param session Session which has ended
	 * @param reason Reason for the session ending. Can be null.
	 */
	void onSessionEnd(ISession session, String reason);
}
