package com.stjerncraft.controlpanel.client.api.webview;

import java.util.Collection;

import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketDefinition;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IWebView {
	/**
	 * Register a new page
	 */
	void registerPage(String name, IPage page);
	
	/**
	 * Get the page registered with the given name if it exists.
	 * @param name
	 * @return Null if no page is registered for that name
	 */
	IPage getPage(String name);
	
	void registerWidget(String socketName, IWidget widget);
	
	/**
	 * Set the current Page to this new Page, unloading the current Page.
	 * If the page is not registered, this will fail and the current Page will not be unloaded.
	 * @param page
	 * @return Null on success. Error message if it failed.
	 */
	String setCurrentPage(String pageName);
	
	String getCurrentPage();
	
	
	Collection<ISocketDefinition> getSocketDefinitions();
	
	/**
	 * Get the Socket Definition with the given name.
	 * @param socketName
	 * @return Null if it does not exist.
	 */
	ISocketDefinition getSocketDefinition(String socketName);
	
	/**
	 * Get the Socket Definition with the given name, or create it if it doesn't exist
	 * @param socketName
	 * @return The existing or newly created Socket Definition.
	 */
	ISocketDefinition getOrCreateSocketDefinition(String socketName);
}
