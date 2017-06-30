package com.stjerncraft.controlpanel.core.server.messages;

import org.eclipse.jetty.websocket.api.Session;

import com.stjerncraft.controlpanel.core.server.HTTPServer;
import com.stjerncraft.controlpanel.core.server.message.IMessage;

public class MessageGetAllServiceApis implements IMessage {

	@Override
	public String getMessageId() {
		return "GetAllServiceApis";
	}

	@Override
	public void handle(Session wsSession, HTTPServer server) {
		// TODO Auto-generated method stub
		
	}

}
