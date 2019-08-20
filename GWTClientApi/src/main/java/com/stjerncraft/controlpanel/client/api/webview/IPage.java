package com.stjerncraft.controlpanel.client.api.webview;

import jsinterop.annotations.JsType;

/**
 * The Root Widget. There can only ever be ONE Page active at once.
 * Placed inside a special root "Page" Socket, and provides the base Sockets for the Page layout, 
 * which everything else builds on top of.
 * 
 * The Page is never destroyed, but is inserted/removed over the lifetime of the Control Panel as the current page changes.
 */
@JsType(isNative=true)
public interface IPage extends IWidget, IWidgetInstance {
}
