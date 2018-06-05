package com.stjerncraft.controlpanel.core.server;

import org.eclipse.jetty.websocket.api.Session;

import com.stjerncraft.controlpanel.agent.IRemoteClient;
import com.stjerncraft.controlpanel.agent.ISession;

/**
 * Remote Client communication through WebSocket.
 */
public class WebSocketClient implements IRemoteClient {
	CoreWebSocket server;
	Session socketSession;
	
	public WebSocketClient(CoreWebSocket server, Session socketSession) {
		this.server = server;
		this.socketSession = socketSession;
	}
	
	public Session getSocketSession() {
		return socketSession;
	}

	@Override
	public void onSessionEnd(ISession session, String reason) {
		server.endSession(this, session, reason);
	}

}
