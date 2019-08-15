package com.stjerncraft.controlpanel.webview;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.stjerncraft.controlpanel.client.api.module.BaseClientModule;
import com.stjerncraft.controlpanel.client.api.webview.IContentOutput;
import com.stjerncraft.controlpanel.client.api.webview.IOutputChangedListener;
import com.stjerncraft.controlpanel.client.api.webview.IPage;
import com.stjerncraft.controlpanel.client.api.webview.IWebView;
import com.stjerncraft.controlpanel.client.api.webview.IWidget;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketDefinition;

import jsinterop.annotations.JsType;

@JsType
public class WebView extends BaseClientModule implements IWebView {
	static Logger logger = Logger.getLogger("WebView");
	
	Map<String, IPage> pages;
	Map<String, ISocketDefinition> sockets;
	String currentPage;
	
	IOutputChangedListener pageChangedListener = new IOutputChangedListener() {
		
		@Override
		public void onOutputChanged(IContentOutput template) {			
			Element pageDiv = DOM.getElementById("page");
			if(pageDiv == null) {
				String err = "Failed to find page element in HTML!";
				logger.severe(err);
				return;
			}
			
			if(template == null)
				pageDiv.setInnerHTML("");
			else
				pageDiv.setInnerHTML(template.getOutput());
		}
	};
	
	
	public WebView() {
		pages = new HashMap<String, IPage>();
		sockets = new HashMap<String, ISocketDefinition>();
	}
	
	@Override
	public void registerPage(String name, IPage page) {
		IPage oldPage = pages.get(name);
		if(oldPage != null)
			setCurrentPage(null);
		
		pages.put(name, page);
		setCurrentPage(name);
	}

	@Override
	public void registerWidget(String socketName, IWidget widget) {
		ISocketDefinition socket = getOrCreateSocketDefinition(socketName);
		
		socket.addWidget(widget);
	}
	
	@Override
	public IPage getPage(String name) {
		return pages.get(name);
	}

	@Override
	public String setCurrentPage(String pageName) {
		if(pageName != null && pageName.equals(currentPage))
			return null;
		
		if(pageName != null && pageName.trim().isEmpty())
			pageName = null;
		
		IPage page = null;
		
		if(pageName != null)
		{
			page = pages.get(pageName);
			if(page == null) {
				String err = "No page with the name " + pageName + " has been registered!";
				logger.severe(err);
				return err;
			}
		}
		IPage oldPage = pages.get(currentPage);
		if(oldPage != null)
			oldPage.onPageUnloading();
		
		currentPage = pageName;
		if(page != null)
			page.onPageLoaded();
		
		pageChangedListener.onOutputChanged(page);
		
		return null;
	}
	
	@Override
	public String getCurrentPage() {
		return currentPage;
	}

	@Override
	public Collection<ISocketDefinition> getSocketDefinitions() {
		return sockets.values();
	}

	@Override
	public ISocketDefinition getSocketDefinition(String socketName) {
		return sockets.get(socketName);
	}

	@Override
	public ISocketDefinition getOrCreateSocketDefinition(String socketName) {
		ISocketDefinition socket = sockets.get(socketName);
		if(socket == null) {
			socket = new SocketDefinition(socketName);
			sockets.put(socketName, socket);
		}
		
		return socket;
	}

	@Override
	public void onActivate() {
		//If User not logged in, load the Login page.
		//Else, load the Dashboard page.
	}

	@Override
	public void onDeactivate() {
		//Clear our Page and it's content
	}

}
