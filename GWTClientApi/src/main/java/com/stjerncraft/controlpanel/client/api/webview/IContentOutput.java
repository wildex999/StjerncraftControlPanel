package com.stjerncraft.controlpanel.client.api.webview;

import jsinterop.annotations.JsType;

/**
 * Provides a String output, and allows listeners to listen for a change
 */
@JsType(isNative=true)
public interface IContentOutput {
	/**
	 * Listener which is informed if the output might have potentially changed, and getOutput should be called.
	 * @param listener
	 */
	void addOutputChangedListener(IOutputChangedListener listener);
	void removeOutputChangedListener(IOutputChangedListener listener);
	
	/**
	 * Get the output of this WebView object.
	 * @return
	 */
	String getOutput();
}
