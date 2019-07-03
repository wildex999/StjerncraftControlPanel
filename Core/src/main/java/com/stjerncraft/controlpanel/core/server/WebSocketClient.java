package com.stjerncraft.controlpanel.core.server;

import java.time.LocalDateTime;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WriteCallback;

import com.stjerncraft.controlpanel.agent.IRemoteClient;
import com.stjerncraft.controlpanel.api.IClient;
import com.stjerncraft.controlpanel.api.IUser;

/**
 * Remote Client communication through WebSocket.
 */
public class WebSocketClient implements IRemoteClient {
	CoreWebSocket server;
	Session socketSession;
	String uuid;
	LocalDateTime connectionDateTime;
	String agent;
	
	WriteCallback writeCallback;
	
	public WebSocketClient(CoreWebSocket server, Session socketSession, String uuid) {
		this.server = server;
		this.socketSession = socketSession;
		this.uuid = uuid;
		this.connectionDateTime = LocalDateTime.now();
		
		writeCallback = new WriteCallback() {
			
			@Override
			public void writeSuccess() {
				//TODO: Count bytes sent for statistics
			}
			
			@Override
			public void writeFailed(Throwable x) {
				//TODO: Can this fail for normal reasons, like low bandwidth?
				socketSession.close(CoreWebSocket.WS_CODE_INTERNAL_ERROR, "Failed to send data: " + x.getMessage());
			}
		};
	}
	
	public Session getSocketSession() {
		return socketSession;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public void disconnect(String reason) {
		socketSession.close(CoreWebSocket.WS_CODE_NORMAL, reason);
	}

	/**
	 * Encode the given message and sent it to the remote target of the given session.
	 * Will listen for success and failure, ending the session if the send fails.
	 * @param msg The message to encode and send.
	 */
	@Override
	public void sendMessage(String msg) {
		socketSession.getRemote().sendString(msg, writeCallback);
	}

	@Override
	public IUser getUser() {
		//TODO: Return a user once authorized
		return null;
	}

	@Override
	public LocalDateTime getDateConnected() {
		return connectionDateTime;
	}

	@Override
	public String getAgent() {
		return agent;
	}
}
