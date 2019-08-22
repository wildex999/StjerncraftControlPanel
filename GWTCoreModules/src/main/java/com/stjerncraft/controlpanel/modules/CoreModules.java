package com.stjerncraft.controlpanel.modules;

import java.util.logging.Logger;

import com.google.gwt.core.client.ScriptInjector;
import com.stjerncraft.controlpanel.client.api.IClientModuleManager;
import com.stjerncraft.controlpanel.client.api.module.BaseClientModule;
import com.stjerncraft.controlpanel.client.api.webview.IWebView;
import com.stjerncraft.controlpanel.client.api.webview.StaticPageNames;
import com.stjerncraft.controlpanel.client.api.webview.StaticSocketNames;
import com.stjerncraft.controlpanel.modules.loading.LoadingProgressResources;
import com.stjerncraft.controlpanel.modules.loading.LoadingProgressWidget;
import com.stjerncraft.controlpanel.modules.login.LoginPage;
import com.stjerncraft.controlpanel.modules.login.LoginWidget;

public class CoreModules extends BaseClientModule {
	static Logger logger = Logger.getLogger("CoreModules");
	
	
	public CoreModules(IClientModuleManager moduleManager) {
		super(moduleManager);
	}

	@Override
	public void onActivate() {
		ScriptInjector.fromString(LoadingProgressResources.DATA.loadingProgressWidgetJs().getText()).inject();
		
		getWebView(this::setup);
	}

	@Override
	public void onDeactivate() {
		// TODO Auto-generated method stub
		
	}
	
	private void setup(IWebView webView) {
		//Setup Widgets
		webView.registerWidget(StaticSocketNames.ModulesLoadingProgress, new LoadingProgressWidget(this, moduleManager));
		
		webView.registerWidget(StaticSocketNames.Core_UserLogin, new LoginWidget(this));
		webView.registerPage(StaticPageNames.Core_UserLogin, new LoginPage(this));
		
		
	}

}
