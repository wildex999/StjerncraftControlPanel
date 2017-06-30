package com.stjerncraft.controlpanel.core.server.message;

import org.eclipse.jetty.websocket.api.Session;

import com.stjerncraft.controlpanel.core.server.HTTPServer;

/**
 * Interface for Server <-> Client messages.
 * Classes extending this should define the fields contained in the message, and a handle method.
 * The JSON parser will use this class as a template and create instances of it with the relevant fields filled.
 */
public interface IMessage {
	public String getMessageId();
	public void handle(Session wsSession, HTTPServer server);
}
