package com.stjerncraft.controlpanel.module.core.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.typedarrays.shared.ArrayBuffer;

/**
 * Simple GWT WebSocket implementation
 * Based on https://developer.mozilla.org/en-US/docs/Web/API/WebSocket
 *
 *	Note: Events are not buffered, however the WebSocket should not have any events until 
 *        the current Javascript has finished executing. So there should be plenty of time to add listeners.
 *
 *	TODO: Handle JavaScript exceptions properly
 */
public class WebSocket {
	public enum ReadyState {
		CONNECTING,
		OPEN,
		CLOSING,
		CLOSED,
		UNKNOWN
	}
	
	public enum BinaryType {
		Blob,
		ArrayBuffer
	}
	
	private JavaScriptObject wsObject;
	private List<IWebsocketListener> listeners;
	
	private String url;
	private String[] protocols;
	
	/**
	 * Create and Open the WebSocket connection
	 * @param url
	 * @param protocols
	 */
	public WebSocket(String url, String... protocols) {
		listeners = new ArrayList<>();
		this.url = url;
		this.protocols = protocols;
		open();
	}
	
	/**
	 * Open the WebSocket connection if it's closed.
	 */
	public void open() {
		if(wsObject != null && getReadyState() != ReadyState.CLOSED)
			return;
		
		wsObject = create(url, protocols);
		setupInternalListeners(wsObject);
	}
	
	/**
	 * Add a listener to this WebSocket.
	 * @param listener
	 */
	public void addListener(IWebsocketListener listener) {
		if(listeners.contains(listener))
			return;
		listeners.add(listener);
	}
	
	/**
	 * Remove the listener
	 * @param listener
	 */
	public void removeListener(IWebsocketListener listener) {
		listeners.remove(listener);
	}
	
	public ReadyState getReadyState() {
		int state = getJSReadyState();
		switch(state) {
		case 0:
			return ReadyState.CONNECTING;
		case 1:
			return ReadyState.OPEN;
		case 2:
			return ReadyState.CLOSING;
		case 3:
			return ReadyState.CLOSED;
		}
		
		return ReadyState.UNKNOWN;
	}
	
	public native void close() /*-{
		this.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::wsObject.close();
	}-*/;
	
	public native void close(short code) /*-{
		this.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::wsObject.close(code);
	}-*/;
	
	public native void close(short code, String reason) /*-{
		this.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::wsObject.close(code, reason);
	}-*/;
	
	public native void send(String data) /*-{
		this.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::wsObject.send(data);
	}-*/;
	
	public native void send(ArrayBuffer data) /*-{
		this.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::wsObject.send(data);
	}-*/;
	
	/**
	 * Send data types like Blob and ArrayBufferView which has not yet been implemented here.
	 * @param data Data object recognized by Websocket send.
	 */
	public native void send(JavaScriptObject data) /*-{
		this.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::wsObject.send(data);
	}-*/;
	
	public native String getUrl() /*-{
		return this.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::wsObject.url;
	}-*/;
	
	public native String getProtocol() /*-{
		return this.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::wsObject.protocol;
	}-*/;
	
	public native Long getBufferedAmount() /*-{
		return this.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::wsObject.bufferedAmount;
	}-*/;
	
	public native String getExtensions() /*-{
		return this.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::wsObject.extensions;
	}-*/;
		
	private native int getJSReadyState() /*-{
		return this.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::wsObject.readyState;
	}-*/;
	
	private void onopen() {
		runOnListeners(listener -> listener.onOpen());
	}
	
	private void onclose(int code, String reason, boolean wasClean) {
		runOnListeners(listener -> listener.onClose(code, reason, wasClean));
	}
	
	private void onerror() {
		runOnListeners(listener -> listener.onError());
	}
	
	private void onmessage(String data) {
		runOnListeners(listener -> listener.onMessage(data));
	}
	
	private void onmessage(ArrayBuffer data) {
		runOnListeners(listener -> listener.onMessage(data));
	}
	
	private void onmessage(JavaScriptObject data) {
		runOnListeners(listener -> listener.onMessage(data));
	}
	
	/**
	 * Run the given handler on all registered listeners
	 * @param handler
	 */
	private void runOnListeners(Consumer<IWebsocketListener> handler) {
		for(IWebsocketListener listener : listeners)
			handler.accept(listener);
	}
	
	/**
	 * Setup the JavaScript listeners for Websocket events
	 */
	private native void setupInternalListeners(JavaScriptObject wsObj) /*-{
		var self = this;
		wsObj.onopen = function(event) {
			self.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::onopen()();
		}
		wsObj.onclose = function(event) {
			self.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::onclose(ILjava/lang/String;Z)(event.code, event.reason, event.wasClean);
		}
		wsObj.onerror = function(event) {
			self.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::onerror()();
		}
		wsObj.onmessage = function(event) {
			if(event.data instanceof ArrayBuffer)
				self.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::onmessage(Lcom/google/gwt/typedarrays/shared/ArrayBuffer;)(event.data);
			if(typeof(event.data) === "string")
				self.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::onmessage(Ljava/lang/String;)(event.data);
			else {
				//if(event.data instanceof Blob)
				//We just pass along anything else as a JavaScriptObject, including Blob for now.
				self.@com.stjerncraft.controlpanel.module.core.websocket.WebSocket::onmessage(Lcom/google/gwt/core/client/JavaScriptObject;)(event.data);
			}
			

		}
	}-*/;
	
	private static native JavaScriptObject create(String url, String... protocols) /*-{
		wsObj = new WebSocket(url, protocols);
		return wsObj;
	}-*/;
	
	@Override
	public String toString() {
		return getUrl();
	}
}
