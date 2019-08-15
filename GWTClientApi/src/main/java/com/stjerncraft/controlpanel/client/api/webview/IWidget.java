package com.stjerncraft.controlpanel.client.api.webview;

import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

import jsinterop.annotations.JsType;

/**
 * A handler which can provide content to Sockets.
 * Registered by Modules.
 */
@JsType(isNative=true)
public interface IWidget {
	/**
	 * Whether this Widget should be used for the given Socket Instance.
	 * This allows a Widget to reject being used for a given Socket Instance if it chooses to.
	 * @return
	 */
	boolean canUseForSocket(ISocketInstance socket);
	
	/**
	 * Create a new instance of the Widget for use with a Socket.
	 * This is called if useForSocket returns true.
	 * @param socket The Socket instance the new Widget instance will be used on.
	 * @return
	 */
	IWidgetInstance createInstance(ISocketInstance socket);
	
	/**
	 * Get content which are common between all Widget instances, added to the end of the document, outside the <body>.
	 * This is where you can add for example Styling and Scripts which are used by each instance.
	 * @return
	 */
	String getStaticContent();
}
