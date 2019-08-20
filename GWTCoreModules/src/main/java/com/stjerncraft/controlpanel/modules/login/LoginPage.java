package com.stjerncraft.controlpanel.modules.login;

import java.util.Arrays;
import java.util.List;

import com.stjerncraft.controlpanel.client.api.module.IClientModule;
import com.stjerncraft.controlpanel.client.api.webview.base.StaticPage;

public class LoginPage extends StaticPage {

	public LoginPage(IClientModule module) {
		super(module, LoginResources.DATA.loginPageHtml().getText());
	}
	
	@Override
	public List<String> getStylingUrls() {
		return Arrays.asList(LoginResources.DATA.loginPageCss().getSafeUri().asString());
	}
	
}
