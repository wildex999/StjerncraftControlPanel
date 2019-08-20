package com.stjerncraft.controlpanel.client.api.webview.base;

import java.util.List;

import com.stjerncraft.controlpanel.client.api.module.IClientModule;
import com.stjerncraft.controlpanel.client.api.webview.IPage;
import com.stjerncraft.controlpanel.client.api.webview.IWidgetInstance;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

import jsinterop.annotations.JsType;

/**
 * Basic page which has static HTML content
 */
@JsType
public class StaticPage extends StaticWidgetInstance implements IPage {
	private IClientModule module;
	
	public StaticPage(IClientModule module, String rawHtml) {
		super(rawHtml);
		
		this.module = module;
	}
	
	@Override
	public IClientModule getModule() {
		return module;
	}
	
	@Override
	public boolean canUseForSocket(ISocketInstance socket) {
		return true;
	}

	@Override
	public IWidgetInstance createInstance(ISocketInstance socket) {
		//There is only ever one instance of a page
		return this;
	}
	
	@Override
	public void onSocketAdded(ISocketInstance childSocket) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSocketRemoved(ISocketInstance childSocket) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<String> getStylingUrls() {
		return null;
	}

	@Override
	public List<String> getScriptUrls() {
		return null;
	}

	@Override
	public String getStaticContent() {
		return null;
	}
}
