package com.stjerncraft.controlpanel.client.api.webview;

import java.util.List;

import com.stjerncraft.controlpanel.client.api.module.IClientModule;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

import jsinterop.annotations.JsType;

/**
 * A handler which can provide content to Sockets.
 * Registered by Modules.
 */
@JsType(isNative=true)
public interface IWidget {
	/**
	 * Get the module this Widget is registered under
	 * @return
	 */
	IClientModule getModule();
	
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
	 * List of Styles to include as part of this Widget.
	 * Removed if the Widget module is unloaded.
	 * @return A list of urls, or null.
	 */
	List<String> getStylingUrls();
	
	/**
	 * List of Scripts to include as part of this Widget.
	 * Removed if the Widet module is unloaded.
	 * @return A list of urls, or null.
	 */
	List<String> getScriptUrls();
	
	/**
	 * Static content (scripts etc.) shared between all instances of the widget module.
	 * It is added to the Body under a <div> marked for this Widget. Removed if the Widget module is unloaded.
	 * @return String with HTML content, or null.
	 */
	String getStaticContent();
}
