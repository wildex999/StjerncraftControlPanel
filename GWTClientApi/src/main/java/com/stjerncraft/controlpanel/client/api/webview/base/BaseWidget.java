package com.stjerncraft.controlpanel.client.api.webview.base;

import java.util.List;

import com.stjerncraft.controlpanel.client.api.module.IClientModule;
import com.stjerncraft.controlpanel.client.api.webview.IWidget;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

/**
 * Basic implementation of IWidget to use as a base.
 */
public abstract class BaseWidget implements IWidget {

	private IClientModule module;
	
	public BaseWidget(IClientModule module) {
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
