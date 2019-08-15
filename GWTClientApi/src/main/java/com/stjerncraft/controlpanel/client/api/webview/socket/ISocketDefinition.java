package com.stjerncraft.controlpanel.client.api.webview.socket;

import java.util.Collection;

import com.stjerncraft.controlpanel.client.api.webview.IWidget;

import jsinterop.annotations.JsType;

/**
 * Defines a socket which Widgets can register themselves to as handlers.
 * A Socket Definition is defined by a unique name. Two different Sockets with the same name are treated as one!
 */
@JsType(isNative=true)
public interface ISocketDefinition {
	/**
	 * Get the name of this Socket Definition
	 */
	String getName();

	/**
	 * Register a Widget as a potential handler for this Socket
	 * @param widget
	 */
	void addWidget(IWidget widget);
	
	/**
	 * Remove a Widget as a handler for this Socket.
	 * This will force all Sockets using this widget to re-process and find a new Widget.
	 * @param widget
	 */
	void removeWidget(IWidget widget);
	
	/**
	 * List of Widgets registered to handle this Socket
	 * @return
	 */
	Collection<IWidget> getWidgets();
	
	/**
	 * Track instances of this Socket
	 * @param instance
	 */
	void addInstance(ISocketInstance instance);
	void removeInstance(ISocketInstance instance);
	
	/**
	 * All Socket Instances of this Definition
	 * @return
	 */
	Collection<ISocketInstance> getSocketInstances();
}
