package com.stjerncraft.controlpanel.module.core.websocket;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.typedarrays.shared.ArrayBuffer;

/**
 * Handle events on a WebSocket
 */
public interface IWebsocketListener {
	default void onOpen() {};
	default void onClose(int code, String reason, boolean wasClean) {};
	default void onMessage(String data) {};
	default void onMessage(ArrayBuffer data) {};
	/**
	 * Javascript data types like Blob and ArrayBufferView which is not yet implemented in GWT
	 * @param data
	 */
	default void onMessage(JavaScriptObject data) {};
	default void onError() {};
}
