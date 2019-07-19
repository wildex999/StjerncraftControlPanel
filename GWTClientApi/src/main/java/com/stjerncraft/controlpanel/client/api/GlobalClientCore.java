package com.stjerncraft.controlpanel.client.api;

/**
 * Helper class for getting an instance of the global ClientCore.
 */
public class GlobalClientCore {
	public native static void set(IClientCoreApi clientCore) /*-{
		$wnd.StjerncraftClientCore = clientCore;
	}-*/;
	
	public native static IClientCoreApi get() /*-{
		return $wnd.StjerncraftClientCore;
	}-*/;
}
