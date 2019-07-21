package com.stjerncraft.controlpanel.client.api.session;

import com.stjerncraft.controlpanel.api.client.ISession;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IClientSession extends ISession {
	SessionState getCurrentState();
	
	boolean addListener(ISessionListener listener);
	boolean removeListener(ISessionListener listener);
}
