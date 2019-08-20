package com.stjerncraft.controlpanel.client.api.webview;

import java.util.Collection;

import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketDefinition;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

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
	 * Find a new Widget for the given Socket Instance.
	 * This will try to find the best match given the Socket's Context and config.
	 * Any existing Widget will be replaced if a better match is found.
	 * @param socketInstance
	 */
	void findWidget(ISocketInstance socketInstance);
	
	/**
	 * Set the current Page to this new Page, unloading the current Page.
	 * If the page is not registered, this will fail and the current Page will not be unloaded.
	 * @param pageName
	 * @return Null on success. Error message if it failed.
	 */
	String setCurrentPage(String pageName);
	
	/**
	 * Set the current Page to this new Page, unloading the current page.
	 * If the page is not registered, this will wait for "timeout" seconds before failing.
	 * On failing it will not unload the current Page, and will call the onTimeout handler.
	 * 
	 * Note: If another page is set while waiting, we stop waiting for this page and onTimeout will not be called.
	 * @param pageName
	 * @param timeout How many seconds to wait for the page to become available before failing.
	 * @param onTimeout The handler which will be called on timeout
	 * @return
	 */
	String setCurrentPageWhenReady(String pageName, int timeout, ISetPageTimeoutHandler onTimeout);
	
	String getCurrentPage();
	
	/**
	 * Check whether a handler is registered for the given page
	 * @param pageName
	 * @return
	 */
	boolean hasPage(String pageName);
	
	
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
