package com.stjerncraft.controlpanel.core.server;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class CoreWebSocket {

	private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
	
	@OnWebSocketConnect
	public void onConnect(Session session) {
		sessions.add(session);
	}
	
	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		sessions.remove(session);
	}
	
	@OnWebSocketMessage
	public void message(Session session, String message) throws IOException {
		System.out.println("Got: " + message);
		session.getRemote().sendString(message);
	}
}
