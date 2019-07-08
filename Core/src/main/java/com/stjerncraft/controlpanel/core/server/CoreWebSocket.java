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
		
		WebSocketClient client = new WebSocketClient(this, session, UUID.randomUUID().toString());
		sessions.put(session, client);
		clientManager.addClient(client);
		
		logger.info("WebSocket Connect from " + session.getRemoteAddress() + ". Assigned UUID: " + client.getUuid());
	}
	
	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {		
		logger.info("WebSocket Disconnect from " + session.getRemoteAddress() + ": " + statusCode + "(" + reason + ")");
		
		IRemoteClient client = sessions.get(session);
		if(client == null)
			return;
		
		//Note: This is most likely happening in a Jetty worker thread, so we have to be careful.
		//Both clientManager.removeClient and sessions.remove are thread safe, working on concurrent hashmaps.
		//The clientManager might be working on the clients at this point and might try to send messages, which will cause an error.
		//In that case we might have multiple threads trying to remove the client at the same time. 
		//Worst case we get multiple log messages about the disconnect.
		clientManager.removeClient(client);
		sessions.remove(session);
	}
	
	@OnWebSocketMessage
	public void message(Session session, String message) throws IOException {
		IRemoteClient client = sessions.get(session);
		if(client == null)
			return; //Ignore messages from clients which have been disconnected
		
		clientManager.receiveMessage(client, message);
	}
}
