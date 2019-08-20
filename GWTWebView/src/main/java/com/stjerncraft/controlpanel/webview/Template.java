package com.stjerncraft.controlpanel.webview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.stjerncraft.controlpanel.client.api.webview.IOutputChangedListener;
import com.stjerncraft.controlpanel.client.api.webview.ITemplate;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketContext;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketDefinition;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

import jsinterop.annotations.JsType;

@JsType
public class Template implements ITemplate {
	static Logger logger = Logger.getLogger("WebView.Template");
	
	private class TemplateEntry {
		public String html; //Pre-socket html
		public ISocketInstance socket;
		
		public TemplateEntry(String html, ISocketInstance socket) {
			this.html = html;
			this.socket = socket;
		}
	}
	
	protected WebView webView;
	
	private Map<String, ISocketInstance> sockets; //Map Instance Name -> Socket Instance
	private String rawHtml; //HTML without Sockets stripped
	private List<TemplateEntry> templateEntries; //In-order list of Sockets and their preceding html
	private RegExp socketParser;
	private Set<IOutputChangedListener> outputChangedListeners;
	
	public Template(WebView webView) {
		this.webView = webView;
		
		sockets = new HashMap<String, ISocketInstance>();
		templateEntries = new ArrayList<Template.TemplateEntry>();
		outputChangedListeners = new HashSet<IOutputChangedListener>();
		
		//Parse the format {{SocketName,SocketInstanceName}}
		if(socketParser == null)
			socketParser = RegExp.compile("\\{([^\\{\\}]+?),([^\\{\\}]+?)\\}", "g"); 
		//TODO: Find a way to avoid recompiling RegEx for each instance. Can't be static as we use the "global" index tracking.
	}
	
	@Override
	public Map<String, ISocketInstance> getSockets() {
		return sockets;
	}
	
	@Override
	public ISocketInstance getSocket(String instanceName) {
		return sockets.get(instanceName);
	}
	
	@Override
	public boolean setSocketContext(String instanceName, ISocketContext context) {
		ISocketInstance socket = sockets.get(instanceName);
		if(socket == null)
			return false;
		
		socket.setContext(context);
		return true;
	}
	
	@Override
	public void addOutputChangedListener(IOutputChangedListener listener) {
		outputChangedListeners.add(listener);
	}

	@Override
	public void removeOutputChangedListener(IOutputChangedListener listener) {
		outputChangedListeners.remove(listener);
	}

	@Override
	public String getOutput() {
		//Insert Socket HTML into strippedHtml and return it.
		//TODO: Cache Socket HTML and only update where needed
		StringBuilder output = new StringBuilder();
		for(TemplateEntry entry : templateEntries) {
			output.append(entry.html);
			if(entry.socket != null) {
				output.append("<div id='" + entry.socket.getId() + "' style='display: inline'>");
				output.append(entry.socket.getOutput());
				output.append("</div>");
			}
		}
		
		return output.toString();
	}
	
	public void setHtml(String rawHtml) {
		this.rawHtml = rawHtml;
		processHtml();
	}
	
	public String getHtml() {
		return rawHtml;
	}
	
	protected void addSocket(ISocketDefinition socket, SocketInstance instance) {
		sockets.put(instance.getName(), instance);
	}
	
	protected void removeSocket(SocketInstance instance) {
		sockets.remove(instance.getName());
	}
	
	protected void clearSockets() {		
		sockets.clear();
		templateEntries.clear();
	}
	
	/**
	 * Find all Sockets in the raw HTML, register them, and then strip them from the HTML.
	 */
	protected void processHtml() {
		clearSockets();
		socketParser.setLastIndex(0);
		
		logger.info("Process: " + rawHtml);
		
		MatchResult result = socketParser.exec(rawHtml);
		int index = 0;
		while(result != null) {
			String html;
			
			if(result.getIndex() - index > 0) {
				html = rawHtml.substring(index, result.getIndex());
			} else {
				html = "";
			}
			
			String socketName = result.getGroup(1).trim();
			String instanceName = result.getGroup(2).trim();
			
			if(socketName.length() != 0 && instanceName.length() != 0)
			{
				//Create the new Socket instance
				ISocketDefinition socket = webView.getOrCreateSocketDefinition(socketName);
				SocketInstance instance = new SocketInstance(webView, socket, instanceName, webView.getNewId());
				addSocket(socket, instance);
				
				templateEntries.add(new TemplateEntry(html, instance));
			}
			
			//Global flag is set, so it will continue to search after previous result
			index = socketParser.getLastIndex();
			result = socketParser.exec(rawHtml);
		}
		
		if(index < rawHtml.length() - 1) {
			templateEntries.add(new TemplateEntry(rawHtml.substring(index), null));
		}
		
		//Inform any listeners that our content has changed
		for(IOutputChangedListener listener : outputChangedListeners) {
			listener.onOutputChanged(Template.this);
		}
	}

}
