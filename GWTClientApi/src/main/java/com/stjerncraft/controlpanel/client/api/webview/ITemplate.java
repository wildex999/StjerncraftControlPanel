package com.stjerncraft.controlpanel.client.api.webview;

import java.util.List;

import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

/**
 * A Template contains HTML and Sockets.
 * The Sockets are processed and their content plugged into the HTML.
 */
public interface ITemplate {
	
	/**
	 * Get all the Socket Instances registered for this Template
	 * @return
	 */
	List<ISocketInstance<?>> getSockets();
	
	/**
	 * Get the fully processed HTML output to put into the Browser.
	 * @return
	 */
	String getOutput();
}
