package com.stjerncraft.controlpanel.client.api.webview.socket;

import com.stjerncraft.controlpanel.client.api.webview.ITemplate;

import jsinterop.annotations.JsType;

/**
 * Defines a single Socket Instance in a Template, with it's own unique name for this Template.
 * This is provided to any Widget which registers to the Socket, allowing them to access the context.
 * @param <T> A data type for the context, making it easy to access the values in GWT.
 */
@JsType(isNative=true)
public interface ISocketInstance extends ITemplate {
	ISocketContext getContext();
	
	void setContext(ISocketContext value);
	
	/**
	 * Get the name of this socket instance(Unique for a given Template).
	 * @return
	 */
	String getName();
	
	/**
	 * Get the Socket this Instance is inserted into
	 * @return
	 */
	ISocketDefinition getCurrentSocket();
}
