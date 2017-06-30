package com.stjerncraft.controlpanel.core.server;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stjerncraft.controlpanel.core.server.message.IMessage;
import com.stjerncraft.controlpanel.core.server.message.MessageHandler;

@WebSocket
public class CoreWebSocket {
	private static final Logger logger = LoggerFactory.getLogger(CoreWebSocket.class);
	
	private final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
	private MessageHandler messageHandler = new MessageHandler();
	private HTTPServer server;
	
	private static final int WS_CODE_PROTOCOL_ERROR = 1002;
	private static final int WS_CODE_INTERNAL_ERROR = 1011;
	
	public CoreWebSocket(HTTPServer server) {
		this.server = server;
	}
	
	@OnWebSocketConnect
	public void onConnect(Session session) {
		logger.info("WebSocket Connect from " + session.getRemoteAddress());
		sessions.add(session);
	}
	
	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		logger.info("WebSocket Disconnect from " + session.getRemoteAddress() + ": " + statusCode + "(" + reason + ")");
		//TODO: End all Service Sessions for this client
		sessions.remove(session);
	}
	
	@OnWebSocketMessage
	public void message(Session session, String message) throws IOException {
		System.out.println("Got: " + message);
		
		//Parse and handle the message
		IMessage parsedMessage;
		try {
			parsedMessage = messageHandler.parseMessage(message);
		} catch(Exception e) {
			logger.warn("Failed to parser client message: " + e);
			session.close(WS_CODE_PROTOCOL_ERROR, "Error while parsing message");
			return;
		}
		
		try {
			parsedMessage.handle(session, server);
		} catch(Exception e) {
			logger.error("Error while handling client message: " + e);
			session.close(WS_CODE_INTERNAL_ERROR, "Error while handling message");
		}
	}
	
	private void getAllServiceApis() {
		
	}
	
	private void getServiceApis(String name) {
		
	}
	
	private void getServiceApi(String name, String version) {
		
	}
	
	private void getServiceProviders(String apiId) {
		
	}
	
	private void getServiceProvider(String apiId, String serviceProviderId) {
		
	}
	
	private void startSession(String apiId, String serviceProviderId) {
		
	}
}
