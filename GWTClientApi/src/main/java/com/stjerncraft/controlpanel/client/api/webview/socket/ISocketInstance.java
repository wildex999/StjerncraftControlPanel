package com.stjerncraft.controlpanel.client.api.webview.socket;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface ISocketInstance<T extends ISocketContext> {
	T getContext();
}
