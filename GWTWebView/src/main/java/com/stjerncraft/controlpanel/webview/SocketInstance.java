package com.stjerncraft.controlpanel.webview;

import com.stjerncraft.controlpanel.client.api.webview.IWebView;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketContext;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketDefinition;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

import jsinterop.annotations.JsType;

@JsType
public class SocketInstance extends Template implements ISocketInstance {
	private String name;
	
	public SocketInstance(IWebView webView, ISocketDefinition socket, String instanceName) {
		super(webView, "Test " + socket.getName() + ": " + instanceName);
		
		name = instanceName;
	}

	@Override
	public ISocketContext getContext() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setContext(ISocketContext value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ISocketDefinition getCurrentSocket() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
