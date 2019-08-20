package com.stjerncraft.controlpanel.client.api.webview.socket;

import com.stjerncraft.controlpanel.client.api.webview.ITemplate;
import com.stjerncraft.controlpanel.client.api.webview.IWidgetInstance;

import jsinterop.annotations.JsType;

/**
 * Defines a single Socket Instance in a Template, with it's own unique name for this Template.
 * This is provided to any Widget which registers to the Socket, allowing them to access the context and set content.
 * @param <T> A data type for the context, making it easy to access the values in GWT.
 */
@JsType(isNative=true)
public interface ISocketInstance extends ITemplate {
	/**
	 * Get the current Context data set for this socket.
	 * @return
	 */
	ISocketContext getContext();
	
	/**
	 * Set the Context data for this socket instance.
	 * Calling this will cause the current Widget to be Removed, and the selection process to be re-run.
	 * Essentially the Socket Context is static for the active Widget.
	 * @param value
	 */
	void setContext(ISocketContext value);
	
	/**
	 * Get the current Widget assigned to this Socket Instance.
	 * @return
	 */
	IWidgetInstance getWidget();
	
	/**
	 * Set the current Widget Instance assigned to this Socket Instance.
	 * This should only be called by the WebView.
	 * @param widget
	 */
	void setWidget(IWidgetInstance widget);
	
	/**
	 * Get the current raw HTML content.
	 */
	String getContent();
	
	/**
	 * Set the raw HTML content for this Socket Instance.
	 * 
	 * This content will then be parsed for child Sockets, which will cause onSocketAdded to be called on the Widget.
	 * @param rawHtml
	 */
	void setContent(String rawHtml);
	
	/**
	 * Get the name of this socket instance(Unique for a given Template).
	 * @return
	 */
	String getName();
	
	/**
	 * Get the Socket Definition of this Instance
	 * @return
	 */
	ISocketDefinition getDefinition();
	
	/**
	 * Get the ID of the DIV Element which the content of this Socket Instance is placed inside
	 * @return
	 */
	String getId();
	
	/**
	 * Called when this Socket Instance's Output has been inserted into the DOM
	 */
	void onInsert();
	
	/**
	 * Called when this Socket Instance's Output is being removed from the DOM
	 */
	void onRemove();
	
	/**
	 * Called when the Socket Instance is removed, and is no longer valid.
	 * This should propagate to all Child Sockets and Widgets.
	 */
	void onDestroy();
	
	/**
	 * Whether the Socket Instance is still valid.
	 * This returns false if the Content containing the socket has been removed or reloaded.
	 * @return
	 */
	boolean isValid();
}
