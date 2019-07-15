package com.stjerncraft.controlpanel.core.server;

import java.time.LocalDateTime;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stjerncraft.controlpanel.agent.IRemoteClient;
import com.stjerncraft.controlpanel.api.IUser;

/**
 * Remote Client communication through WebSocket.
 */
public class WebSocketClient implements IRemoteClient {
	private static final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);
	
	CoreWebSocket server;
	Session socketSession;
	String uuid;
	LocalDateTime connectionDateTime;
	String name = "WebsocketClient"; //TODO: Allow Client to set own name
	
	volatile boolean isConnected; //Set to false when disconnecting from our end(Don't handle data while waiting for clean disconnect)
	
	WriteCallback writeCallback;
	
	public WebSocketClient(CoreWebSocket server, Session socketSession, String uuid) {
		this.server = server;
		this.socketSession = socketSession;
		this.uuid = uuid;
		this.connectionDateTime = LocalDateTime.now();
		
		isConnected = true;
		
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
		//Note: We assume close is thread safe, since it will enqueue the actual close.
		isConnected = false;
		socketSession.close(CoreWebSocket.WS_CODE_NORMAL, reason);
		logger.info("Disconnecting remote client " + uuid + " for reason: " + reason);
	}

	/**
	 * Encode the given message and sent it to the remote target of the given session.
	 * Will listen for success and failure, ending the session if the send fails.
	 * @param msg The message to encode and send.
	 */
	@Override
	public void sendMessage(String msg) {
		if(!isConnected)
			return;
		
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
	public String getName() {
		return name;
	}

	@Override
	public boolean isConnected() {
		return isConnected;
	}
	
	@Override
	public String toString() {
		return uuid;
	}
}
