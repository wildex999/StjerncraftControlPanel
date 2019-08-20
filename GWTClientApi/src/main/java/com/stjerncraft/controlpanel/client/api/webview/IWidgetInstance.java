package com.stjerncraft.controlpanel.client.api.webview;

import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

import jsinterop.annotations.JsType;

/**
 * An instance of a given Widget.
 * This is created for each Socket Instance which the Widget is chosen for.
 * 
 * The output provided is raw HTML, which will then be processed by the Webview to get the Sockets.
 * Any Context set on the socket should be used when generating the HTML.
 */
@JsType(isNative=true)
public interface IWidgetInstance {		
	/**
	 * Set the Socket Instance which this Widget Instance is supplying content to.
	 * This is where it should get the context and set the content on the Socket.
	 * @param socket
	 */
	void onSetup(ISocketInstance socket);
	
	/**
	 * A Socket Instance has been added to the Socket Instance this Widget is bound to.
	 * This usually happens after calling setContent on the Socket Instance, which will process the raw HTML to find Sockets.
	 * It can also happen that new Socket Instances are added at runtime in certain conditions(Like MultiSockets).
	 * 
	 * This is where the Context of the child socket should be set.
	 */
	void onSocketAdded(ISocketInstance childSocket);
	
	/**
	 * A Socket Instance has been removed from the Socket Instance this Widget is bound to.
	 * This usually happens after calling setContent on the Socket Instance, which will cause any old Socket Instances to be removed.
	 * It can also happen that Socket Instances are removed at runtime in certain conditions(Like MultiSockets)
	 */
	void onSocketRemoved(ISocketInstance childSocket);
	
	/**
	 * Called when the content of this Widget Instance has been placed into the DOM.
	 * Any Javascript to update the element should be called here.
	 * 
	 * Note on default WebView implementation: 
	 * Any dynamic update which does not involve adding, removing or changing a Socket should be done directly on the DOM.
	 * Setting the Socket content again after this is expensive. It will cause the rawHTML to be re-processed, all Sockets/Widgets to be re-created,
	 * and all the content set on innerHTML. This will break any references and event listeners on the existing elements in Widget.
	 */
	void onInsert(String divId);
	
	/**
	 * The Inserted element is being removed from the DOM.
	 * If any changes where made outside the widget instance, they should be cleaned up here.
	 * onInserted might still be called again after this, until onDestroy is called.
	 */
	void onRemove();
	
	/**
	 * The Widget Instance is being destroyed, and should cleanup.
	 * onInserted or getOutput will no longer be called on this Widget Instance, and all references should be removed.
	 */
	void onDestroy();
}
