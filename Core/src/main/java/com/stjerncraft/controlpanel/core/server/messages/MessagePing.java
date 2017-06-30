package com.stjerncraft.controlpanel.core.server.messages;

import org.eclipse.jetty.websocket.api.Session;

import com.stjerncraft.controlpanel.core.server.HTTPServer;
import com.stjerncraft.controlpanel.core.server.message.IMessage;

/**
 * Message to verify an active connection, and check the round trip time/handling delay.
 */
public class MessagePing implements IMessage {
	boolean pong;

	@Override
	public String getMessageId() {
		return "Ping";
	}

	@Override
	public void handle(Session wsSession, HTTPServer server) {
		// TODO Auto-generated method stub
		
	}

}
