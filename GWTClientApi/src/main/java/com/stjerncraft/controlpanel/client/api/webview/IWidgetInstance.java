package com.stjerncraft.controlpanel.client.api.webview;

import com.google.gwt.dom.client.Element;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

/**
 * An instance of a given Widget.
 * This is created for each Socket Instance which the Widget is chosen for.
 * 
 * The output provided is raw HTML, which will then be processed by the Webview to get the Sockets.
 * Any Context set on the socket should be used when generating the HTML.
 */
public interface IWidgetInstance extends IContentOutput {	
	/**
	 * Set the Socket Instances created after processing the output of this Widgets Instance.
	 * This is where any Socket Context should be set, and can be called multiple times if the output is re-processed.
	 * 
	 * After this call, Widgets will be created for the sockets.
	 * Changing the Context after this point might cause the Socket's Widget to be re-created.
	 */
	void setInlineSockets(ISocketInstance[] sockets);
	
	/**
	 * Called when the content of this Widget Instance has been placed into the DOM.
	 * Any Javascript to update the element should be called here.
	 * 
	 * Note: Any dynamic update which does not involve adding, removing or changing a Socket should be done directly on the DOM.
	 * Forcing a new call to getContent and inserting it again will be slow, as it will just set the innerHTML,
	 * and force all child Widgets to also be re-inserted. This will break any event listeners on the existing elements.
	 */
	void onInserted(Element domElement);
	
	/**
	 * The Inserted element has been removed from the DOM.
	 * If any changes where made outside the widget instance, they should be cleaned up here.
	 * onInserted might still be called again after this, until onDestroy is called.
	 */
	void onRemoved();
	
	/**
	 * The Widget Instance is being destroyed, and should cleanup.
	 * onInserted or getOutput will no longer be called on this Widget Instance, and all references should be removed.
	 */
	void onDestroy();
}
