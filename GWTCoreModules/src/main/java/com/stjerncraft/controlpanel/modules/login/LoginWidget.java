package com.stjerncraft.controlpanel.modules.login;

import com.stjerncraft.controlpanel.client.api.module.IClientModule;
import com.stjerncraft.controlpanel.client.api.webview.IWidgetInstance;
import com.stjerncraft.controlpanel.client.api.webview.base.BaseWidget;
import com.stjerncraft.controlpanel.client.api.webview.base.StaticWidgetInstance;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

import jsinterop.annotations.JsType;

@JsType
public class LoginWidget extends BaseWidget {	
	public LoginWidget(IClientModule module) {
		super(module);
	}

	@Override
	public IWidgetInstance createInstance(ISocketInstance socket) {
		return new StaticWidgetInstance(LoginResources.DATA.loginWidgetHtml().getText());
	}
}
