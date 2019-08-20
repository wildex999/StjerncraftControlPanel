package com.stjerncraft.controlpanel.webview;

import java.util.Collection;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.stjerncraft.controlpanel.client.api.webview.IWidgetInstance;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketContext;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketDefinition;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

import jsinterop.annotations.JsType;

@JsType
public class SocketInstance extends Template implements ISocketInstance {
	private String name;
	private ISocketDefinition socket;
	private ISocketContext context;
	private IWidgetInstance widget;
	
	private boolean isValid;
	private String divId;
	
	public SocketInstance(WebView webView, ISocketDefinition socketDefinition, String instanceName, String id) {
		super(webView);
		
		name = instanceName;
		socket = socketDefinition;
		isValid = true;
		divId = id;
	}

	@Override
	public ISocketContext getContext() {
		return context;
	}
	
	@Override
	public void setContext(ISocketContext value) {
		context = value;
		
		//Changing context means reloading the widget or finding a better one
		webView.findWidget(this);
	}
	
	@Override
	public IWidgetInstance getWidget() {
		return widget;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ISocketDefinition getDefinition() {
		return socket;
	}

	@Override
	public String getContent() {
		return getHtml();
	}
	
	@Override
	public void setContent(String rawHtml) {
		if(!isValid())
			return;
		
		logger.fine("Setting content on socket " + getDefinition().getName() + ":" + getName() + " = " + rawHtml);
		setHtml(rawHtml);
		
		//Give Child Sockets to the Widget
		Collection<ISocketInstance> sockets = getSockets().values();
		if(widget != null) {
			for(ISocketInstance socket : sockets) {
				logger.info("Adding new child socket to " + getDefinition().getName() + ":" + getName() + ": " + socket.getDefinition().getName() + ":" + socket.getName());
				widget.onSocketAdded(socket);
				
				//If setContext is called in onSocketAdded, it will find a Widget at that point.
				if(socket.getWidget() == null)
					webView.findWidget(socket);
					
			}
		}
		
		insertIntoDOM();
	}

	@Override
	public String getId() {
		return divId;
	}
	
	@Override
	public void onInsert() {
		if(widget != null)
			widget.onInsert(getId());
			
		//All Child Socket Instance widgets are also inserted at this point
		Collection<ISocketInstance> sockets = getSockets().values();
		for(ISocketInstance socket : sockets)  {
			socket.onInsert();
		}
	}
	
	@Override
	public void onRemove() {
		if(widget != null)
			widget.onRemove();
		
		//All Child Socket Instance widgets are also removed at this point
		Collection<ISocketInstance> sockets = getSockets().values();
		for(ISocketInstance socket : sockets)  {
			socket.onRemove();
		}
	}

	@Override
	public void onDestroy() {
		isValid = false;
		
		if(widget != null)
			widget.onDestroy();
		
		//All Child Socket Instance widgets are also destroyed at this point
		Collection<ISocketInstance> sockets = getSockets().values();
		for(ISocketInstance socket : sockets)  {
			socket.onDestroy();
		}
	}
	
	@Override
	public boolean isValid() {
		return isValid;
	}
	
	public void setWidget(IWidgetInstance newWidget) {
		if(newWidget == widget)
			return;
		
		if(widget != null) {
			widget.onRemove();
			widget.onDestroy();
		}
		
		widget = newWidget;
		if(widget != null)
			widget.onSetup(this);
	}
	
	protected void insertIntoDOM() {
		Element socketDiv = DOM.getElementById(getId());
		if(socketDiv == null) {
			String err = "Failed to insert socket content into DOM, unable to find Socket element: " + getId();
			logger.severe(err);
			return;
		}
		
		socketDiv.setInnerHTML(getOutput());
		onInsert();
	}
	
}
