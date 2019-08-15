package com.stjerncraft.controlpanel.client.api.webview;

import jsinterop.annotations.JsType;

/**
 * The Root WebView object. There can only ever be ONE Page active at once.
 * It is not itself placed inside a Socket, but provides the base Sockets for the Page layout, 
 * which everything else builds on top of.
 */
@JsType(isNative=true)
public interface IPage extends ITemplate {
	/**
	 * Called by the WebView when the page has been set as current, but before getOutput is called.
	 */
	void onPageLoaded();
	
	/**
	 * Called by the WebView when another page is being set as current. Called before the page is removed.
	 */
	void onPageUnloading();
}
