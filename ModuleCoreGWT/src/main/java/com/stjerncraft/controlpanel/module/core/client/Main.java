package com.stjerncraft.controlpanel.module.core.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.stjerncraft.controlpanel.common.Version;
import com.stjerncraft.controlpanel.common.messages.MessageVersion;
import com.stjerncraft.controlpanel.module.core.messages.Messages;
import com.stjerncraft.controlpanel.module.core.websocket.IWebsocketListener;
import com.stjerncraft.controlpanel.module.core.websocket.WebSocket;

public class Main implements EntryPoint {
	Messages messages;
	
	@Override
	public void onModuleLoad() {
		messages = new Messages();
		messages.setHandler(MessageVersion.class, (msg, ws) -> {
			GWT.log("GOT VERSION: " + msg.versionMajor + "." + msg.versionMinor + "." + msg.versionFix + " on Socket: " + ws);
		});
		
		WebSocket ws = new WebSocket("ws://localhost:8080/ws");
		messages.setSocket(ws);
		ws.addListener(new IWebsocketListener() {
			@Override
			public void onOpen() {
				messages.sendMessage(new MessageVersion(Version.Major, Version.Minor, Version.Fix));
			}
		});
	}

}
