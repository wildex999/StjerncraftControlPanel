package com.stjerncraft.controlpanel.client.api.module;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.google.gwt.core.client.GWT;
import com.stjerncraft.controlpanel.client.api.IClientModuleListener;
import com.stjerncraft.controlpanel.client.api.IClientModuleManager;
import com.stjerncraft.controlpanel.client.api.StaticModuleNames;
import com.stjerncraft.controlpanel.client.api.webview.IWebView;

import jsinterop.annotations.JsType;

@JsType
public abstract class BaseClientModule implements IClientModule {
	IClientModuleListener webViewListener = new IClientModuleListener() {
		
		@Override
		public void onModuleLoading(String module) {}
		
		@Override
		public void onModuleLoaded(IClientModule module) {}
		
		@Override
		public void onModuleDeactivated(IClientModule module) {}
		
		@Override
		public void onModuleActivated(IClientModule module) {
			if(!module.getName().equals(StaticModuleNames.WebView))
				return;
			
			webView = (IWebView) module;
			moduleManager.removeModuleListener(this);
			
			for(Consumer<IWebView> handler : webViewHandlers)
				handler.accept(webView);
			
			webViewHandlers.clear();
			webViewHandlers = null; //This is not needed anymore
		}
	};
	
	protected IClientModuleManager moduleManager;
	private IWebView webView;
	private ArrayList<Consumer<IWebView>> webViewHandlers;
	
	public BaseClientModule(IClientModuleManager moduleManager) {
		this.moduleManager = moduleManager;
		webView = null;
		webViewHandlers = new ArrayList<Consumer<IWebView>>();
	}
	
	@Override
	public String getName() {
		//The name it is registered as in the Core is the real name, which might differ from the GWT Module name.
		//So we try to get it from the path we loaded the Module from, using the GWT module name as a fallback.
		String[] path = GWT.getModuleBaseURL().split("/");
		if(path.length == 0) {
			return GWT.getModuleName();
		}
		
		return path[path.length-1];
	}
	
	/**
	 * Get the WebView instance.
	 * If the WebView is already loaded and ready, the handler will be called instantly.
	 * Else it will be called once the WebView has loaded and becomes ready.
	 * @param handler
	 */
	protected void getWebView(Consumer<IWebView> handler)
	{
		if(webView != null) {
			handler.accept(webView);
			return;
		}
		
		//Find the WebView, or listen for it to become ready
		IClientModule webViewModule = moduleManager.getLoadedModule(StaticModuleNames.WebView);
		webView = (IWebView) webViewModule;
		if(webView != null)
		{
			handler.accept(webView);
			return;
		}
		
		webViewHandlers.add(handler);
		
		if(webViewHandlers.size() == 1)
			moduleManager.addModuleListener(webViewListener);
	}
}
