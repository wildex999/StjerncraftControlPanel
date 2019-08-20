package com.stjerncraft.controlpanel.client.api.webview.base;

import com.stjerncraft.controlpanel.client.api.webview.IWidgetInstance;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

import jsinterop.annotations.JsType;

/**
 * Basic widget which has static HTML content
 */
@JsType
public class StaticWidgetInstance implements IWidgetInstance {

	private String html;
	protected String divId;
	
	public StaticWidgetInstance(String rawHtml) {
		html = rawHtml;
	}
	
	@Override
	public void onSetup(ISocketInstance socket) {
		socket.setContent(html);
	}
	
	@Override
	public void onSocketAdded(ISocketInstance childSocket) {}

	@Override
	public void onSocketRemoved(ISocketInstance childSocket) {}

	@Override
	public void onInsert(String divId) {
		this.divId = divId;
	}

	@Override
	public void onRemove() {
		divId = null;
	}

	@Override
	public void onDestroy() {}

}
