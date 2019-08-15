package com.stjerncraft.controlpanel.client.api.webview.socket;

import jsinterop.annotations.JsType;

/**
 * Runtime context set on a Socket, and provided to any Widget which is registered for the Socket.
 * A Page/Widget is responsible to setting the context for each Socket instance.
 * 
 * This can always be cast to any other native JsType implementing this interface.
 * However, as with any Javascript object, it might not contain the fields you expect.
 * 
 * All implementations MUST have:
 * @JsType(isNative=true)
 * As must any custom objects contained as part of the context, or else they will not work between Modules.
 */
@JsType(isNative=true)
public interface ISocketContext {}
