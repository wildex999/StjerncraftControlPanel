package com.stjerncraft.controlpanel.module.core.messages;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.typedarrays.client.JsUtils;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.stjerncraft.controlpanel.common.messages.Message;
import com.stjerncraft.controlpanel.module.core.websocket.IWebsocketListener;
import com.stjerncraft.controlpanel.module.core.websocket.WebSocket;

/**
 * Handle messages through WebSockets.
 * TODO: Allow for multiple sockets for messaging channels?
 *
 */
public class Messages extends com.stjerncraft.controlpanel.common.messages.Messages<WebSocket> {
	private WebSocket ws;
	
	
	public Messages() {
		registerMessages();
	}
	
	/**
	 * Set the socket to use when sending and receiving messages
	 * @param ws
	 */
	public void setSocket(WebSocket ws) {
		this.ws = ws;
		
		ws.addListener(new IWebsocketListener() {
			@Override
			public void onMessage(ArrayBuffer data) {
				String strData = JsUtils.stringFromArrayBuffer(data);
				onMessage(strData);
			}
			
			@Override
			public void onMessage(JavaScriptObject data) {
				throw new UnsupportedOperationException("Got data in an unspported format: " + data.toString());
			}
			
			
			@Override
			public void onMessage(String data) {
				Message msg = decode(data);
				handleMessage(msg, ws);
			}
		});
	}
	
	public void sendMessage(Message msg) {
		String msgData = encode(msg);
		ws.send(msgData);
	}
	
}
