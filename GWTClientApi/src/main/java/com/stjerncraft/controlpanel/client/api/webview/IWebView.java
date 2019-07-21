package com.stjerncraft.controlpanel.client.api.webview;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IWebView {
	/**
	 * Register a new page
	 */
	void RegisterPage(IPage page);
	
	void RegisterWidget();
	
	/**
	 * Set the current Page to this new Page, unloading the current Page.
	 * If the page is not registered, this will fail and the current Page will not be unloaded.
	 * @param page
	 * @return Null on success. Error message if it failed.
	 */
	String SetPage(IPage page);
}
