package com.stjerncraft.controlpanel.webview;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.stjerncraft.controlpanel.client.api.IClientModuleManager;
import com.stjerncraft.controlpanel.client.api.module.BaseClientModule;
import com.stjerncraft.controlpanel.client.api.webview.IPage;
import com.stjerncraft.controlpanel.client.api.webview.ISetPageTimeoutHandler;
import com.stjerncraft.controlpanel.client.api.webview.IWebView;
import com.stjerncraft.controlpanel.client.api.webview.IWidget;
import com.stjerncraft.controlpanel.client.api.webview.IWidgetInstance;
import com.stjerncraft.controlpanel.client.api.webview.StaticPageNames;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketDefinition;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

import jsinterop.annotations.JsType;

@JsType
public class WebView extends BaseClientModule implements IWebView {
	static Logger logger = Logger.getLogger("WebView");
	
	private Map<String, IPage> pages;
	private Map<String, ISocketDefinition> sockets;
	private Map<Integer, IWidget> widgets;
	
	private SocketInstance pageSocket;
	//private SocketInstance overlaySocket; //Socket for Overlay Widgets, existing on top of and independent of the current page
	private String currentPage;
	
	private int idCounter = 0;
	private int widgetIdCounter = 0;
	
	private String waitForPage;
	private ISetPageTimeoutHandler waitForPageTimeoutHandler;
	private Timer waitForPageTimer = new Timer() {
		@Override
		public void run() {
			String timeoutPageName = waitForPage;
			waitForPage = null;
			waitForPageTimeoutHandler.onTimeout(timeoutPageName);
		}
	};
	
	
	public WebView(IClientModuleManager moduleManager) {
		super(moduleManager);
		
		pages = new HashMap<String, IPage>();
		sockets = new HashMap<String, ISocketDefinition>();
		widgets = new HashMap<Integer, IWidget>();
		
		ISocketDefinition pageSocketDefinition = new SocketDefinition("pageRoot");
		pageSocket = new SocketInstance(this, pageSocketDefinition, "pageRoot", "sccp-page");
	}
	
	@Override
	public void registerPage(String name, IPage page) {
		name = name.trim();
		
		logger.fine("Registering page: " + name);
		boolean overwritingCurrent = name.equals(currentPage);
		
		if(overwritingCurrent)
			setCurrentPage(null);
		
		pages.put(name, page);
		registerWidget(pageSocket.getDefinition().getName(), page);
		
		//Reload the current page if overwriting
		if(overwritingCurrent)
			setCurrentPage(name);
		
		//Load page which is queued
		if(waitForPage != null && waitForPage.equals(name))
			setCurrentPage(waitForPage);
	}

	@Override
	public void registerWidget(String socketName, IWidget widget) {
		//Inject any Scripts, Styling and Static Content.
		int widgetId = widgetIdCounter++;
		HeadElement head = Document.get().getHead();
		if(widget.getStylingUrls() != null) {
			for(String styleUrl : widget.getStylingUrls()) {
				LinkElement cssLink = LinkElement.as(DOM.createElement("link"));
				cssLink.setRel("stylesheet");
				cssLink.setHref(styleUrl);
				cssLink.addClassName("sccp-widget-" + widgetId);
				head.appendChild(cssLink);
			}
		}
		if(widget.getScriptUrls() != null) {
			for(String scriptUrl : widget.getScriptUrls()) {
				ScriptElement scriptLink = ScriptElement.as(DOM.createElement("script"));
				scriptLink.setSrc(scriptUrl);
				scriptLink.addClassName("sccp-widget-" + widgetId);
				head.appendChild(scriptLink);
			}
		}
		
		if(widget.getStaticContent() != null) {
			BodyElement body = Document.get().getBody();
			DivElement staticContentDiv = DivElement.as(DOM.createDiv());
			staticContentDiv.setInnerHTML(widget.getStaticContent());
			staticContentDiv.setId("sccp-widget-" + widgetId);
			body.appendChild(staticContentDiv);
		}
		
		widgets.put(widgetId, widget);
		ISocketDefinition socket = getOrCreateSocketDefinition(socketName);
		socket.addWidget(widget);
		
		//TODO: Check if this Widget is usable by any Sockets awaiting Widget
	}
	
	@Override
	public void findWidget(ISocketInstance socketInstance) {
		if(!socketInstance.isValid())
			return;
		
		
		ISocketDefinition socket = socketInstance.getDefinition();
		socket = getSocketDefinition(socket.getName());
		if(socket == null) {
			logger.warning("Unable to find Widget for Socket: " + socketInstance.getDefinition().getName() + ", not registered!");
			return;
		}
		
		Collection<IWidget> potentialWidgets = socket.getWidgets();
		if(potentialWidgets.size() == 0) {
			logger.warning("Unable to find Widget for Socket: " + socket.getName() + ", none registered!");
			return;
		}
		
		//TODO: Use Priority & Config(Size etc.) to select the preferred Widget
		IWidgetInstance newWidget = null;
		Iterator<IWidget> iter = potentialWidgets.iterator();
		while(iter.hasNext()) {
			IWidget widget = iter.next();
			
			if(!widget.canUseForSocket(socketInstance))
				continue;
			
			newWidget = widget.createInstance(socketInstance);
			socketInstance.setWidget(newWidget);
			break;
		}
		
		//TODO: Assign special "Widget Not Found" widget to inform user
		//TODO: Listen for valid Widget being registered after the fact, for delayed insert(Maybe the WidgetNotFound Widget will do this?)
		if(newWidget == null)
			logger.warning("Unable to find Widget for Socket: " + socket.getName() + ", none was accepted!");
		
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
		
		if(pageName == null) {
			String err = "Trying to change to null page!";
			logger.severe(err);
			return err;
		}
		
		logger.fine("Setting new page: " + pageName);

		IPage newPage = pages.get(pageName);
		if(newPage == null) {
			String err = "No page with the name " + pageName + " has been registered!";
			logger.severe(err);
			return err;
			//TODO: Show error to user about failing to change page(Popup/Toast/Overlay)
		}
		
		//Stop waiting for page
		waitForPage = null;
		waitForPageTimer.cancel();
		waitForPageTimeoutHandler = null;
		
		//TODO: Show some kind of loading message/transition
		//Load the new page
		pageSocket.setWidget(newPage);
		
		return null;
	}
	
	@Override
	public String setCurrentPageWhenReady(String pageName, int timeout, ISetPageTimeoutHandler onTimeout) {		
		if(hasPage(pageName)) {
			return setCurrentPage(pageName);
		}
		
		if(pageName != null && pageName.trim().isEmpty())
			pageName = null;
		
		if(pageName == null) {
			String err = "Trying to change to null page!";
			logger.severe(err);
			return err;
		}
		
		waitForPage = pageName;
		waitForPageTimeoutHandler = onTimeout;
		waitForPageTimer.cancel();
		waitForPageTimer.schedule(timeout * 1000);
		
		return null;
	}
	
	@Override
	public String getCurrentPage() {
		return currentPage;
	}
	
	@Override
	public boolean hasPage(String pageName) {
		return pages.containsKey(pageName);
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
		setCurrentPageWhenReady(StaticPageNames.Core_UserLogin, 30, pageName -> {
			//TODO: Show error popup
			logger.severe("Failed to load Login page, timed out while waiting for page!");
		});
	}

	@Override
	public void onDeactivate() {
		//Clear our Page and it's content
	}
	
	/**
	 * Get a new Unique Element ID to use for a Socket Instance.
	 * @return
	 */
	public String getNewId() {
		return "sccp-" + idCounter++;
	}
}
