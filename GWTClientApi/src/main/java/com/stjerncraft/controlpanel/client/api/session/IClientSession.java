package com.stjerncraft.controlpanel.client.api.session;

import com.stjerncraft.controlpanel.common.data.IServiceApiInfo;
import com.stjerncraft.controlpanel.common.data.IServiceProviderInfo;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IClientSession {
	IServiceApiInfo getApi();
	IServiceProviderInfo getServiceProvider();
	SessionState getCurrentState();
	int getSessionId();
}
