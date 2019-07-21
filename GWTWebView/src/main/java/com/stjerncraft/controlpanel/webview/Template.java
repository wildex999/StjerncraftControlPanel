package com.stjerncraft.controlpanel.webview;

import java.util.List;

import com.stjerncraft.controlpanel.client.api.webview.ITemplate;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public class Template implements ITemplate {
	private List<ISocketInstance<?>> sockets;
	private String rawHtml; //HTML without Sockets stripped
	private String processedHtml; //HTML with Sockets stripped
	
	@Override
	public List<ISocketInstance<?>> getSockets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOutput() {
		// TODO Auto-generated method stub
		return null;
	}

}
