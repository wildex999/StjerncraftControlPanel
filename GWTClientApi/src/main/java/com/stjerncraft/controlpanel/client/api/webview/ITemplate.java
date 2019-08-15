package com.stjerncraft.controlpanel.client.api.webview;

import java.util.Map;

import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketContext;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

import jsinterop.annotations.JsType;

/**
 * A Template takes in HTML with Socket tags, processes it to output the HTML with sockets content injected.
 */
@JsType(isNative=true)
public interface ITemplate extends IContentOutput {
	
	/**
	 * Get all the Socket Instances registered for this Template
	 * @return
	 */
	Map<String, ISocketInstance> getSockets();
	
	/**
	 * Get the specific named socket on this Template
	 * @param instanceName
	 * @return Null if no socket instance with that name exists
	 */
	ISocketInstance getSocket(String instanceName);
	
	/**
	 * Set a new Context for the name Socket Instance.
	 * @param instanceName
	 * @param context
	 * @return True if successful, or false if no instance with the given name was found.
	 */
	boolean setSocketContext(String instanceName, ISocketContext context);
}
