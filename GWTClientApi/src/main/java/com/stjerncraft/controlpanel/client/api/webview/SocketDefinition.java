package com.stjerncraft.controlpanel.client.api.webview;

import jsinterop.annotations.JsType;

/**
 * Provides a point where Module Widgets can register and inject themselves into a HTML page.
 */
@JsType(isNative=true)
public class SocketDefinition<T> {
	String name;
	T context;
}
