package com.stjerncraft.controlpanel.core.server;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stjerncraft.controlpanel.agent.IRemoteClient;
import com.stjerncraft.controlpanel.core.client.ClientManager;


/**
 * The main communication point between Remote Clients and the Core(and thus other Agents) for WebSocket clients.
 */
@WebSocket
public class CoreWebSocket {	
	private static final Logger logger = LoggerFactory.getLogger(CoreWebSocket.class);
	private final Map<Session, IRemoteClient> sessions = new ConcurrentHashMap<Session, IRemoteClient>();
	
	private ClientManager clientManager;
	
	public static final int WS_CODE_PROTOCOL_ERROR = 1002;
	public static final int WS_CODE_INTERNAL_ERROR = 1011;
	public static final int WS_CODE_UNSUPPORTED = 1003;
	public static final int WS_CODE_NORMAL = 1000;
	
	public CoreWebSocket(ClientManager clientManager) {
		this.clientManager = clientManager;
	}
	
	@OnWebSocketConnect
	public void onConnect(Session session) {
		//TODO: Disconnect the user if we do not get a version message in x seconds. 
		//TODO: The websocket has a 30 second timeout, but we might need a specific check to protect against unknown messages?
		logger.info("WebSocket Connect from " + session.getRemoteAddress());
		
		WebSocketClient client = new WebSocketClient(this, session, UUID.randomUUID().toString());
		sessions.put(session, client);
		clientManager.addClient(client);
	}
	
	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {		
		logger.info("WebSocket Disconnect from " + session.getRemoteAddress() + ": " + statusCode + "(" + reason + ")");
		
		IRemoteClient client = sessions.get(session);
		if(client == null)
			return;
		
		//TODO: End all Service Sessions for this client
		//TODO: Must be thread safe
		clientManager.removeClient(client);
		sessions.remove(session);
	}
	
	@OnWebSocketMessage
	public void message(Session session, String message) throws IOException {
		IRemoteClient client = sessions.get(session);
		if(client == null)
			return;
		
		clientManager.receiveMessage(client, message);
	}
}
