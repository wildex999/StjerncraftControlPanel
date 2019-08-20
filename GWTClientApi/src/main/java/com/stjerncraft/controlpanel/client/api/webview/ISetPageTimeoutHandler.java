package com.stjerncraft.controlpanel.client.api.webview;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface ISetPageTimeoutHandler {
	/**
	 * Page failed to load, timed out before page became available.
	 * @param pageName
	 */
	public void onTimeout(String pageName);
}
