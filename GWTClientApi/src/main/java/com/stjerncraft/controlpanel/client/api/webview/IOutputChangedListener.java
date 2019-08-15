package com.stjerncraft.controlpanel.client.api.webview;

import jsinterop.annotations.JsFunction;

/**
 * Handler which is notified when the output of a WebView Object might have changed, and needs to be re-fetched.
 */
@JsFunction
public interface IOutputChangedListener {
	void onOutputChanged(IContentOutput template);
}
