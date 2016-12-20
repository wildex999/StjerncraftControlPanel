package com.stjerncraft.controlpanel.core.client;

import com.stjerncraft.controlpanel.core.ISession;

public interface IClient {
	/**
	 * Called by the Agent when a session with the Client is ended.
	 * The session is invalid at this point, and can not be used for further communication.
	 * @param session Session which has ended
	 * @param reason Reason for the session ending. Can be null.
	 */
	void onSessionEnd(ISession session, String reason);
}
