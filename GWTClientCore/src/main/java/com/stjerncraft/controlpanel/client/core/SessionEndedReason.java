package com.stjerncraft.controlpanel.client.core;

import jsinterop.annotations.JsType;

@JsType
public enum SessionEndedReason {
	RemoteEnded, //The Remote Server/Agent/ServiceProvider ended the session.
	ClientEnded, //The Local Client/Core ended the session.
	Disconnect //The session was lost due to a disconnect
}
