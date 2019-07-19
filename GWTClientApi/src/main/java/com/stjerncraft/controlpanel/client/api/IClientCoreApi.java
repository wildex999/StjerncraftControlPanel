package com.stjerncraft.controlpanel.client.api;

import com.stjerncraft.controlpanel.api.client.IClientCore;
import com.stjerncraft.controlpanel.client.api.session.IClientSession;
import com.stjerncraft.controlpanel.client.api.session.ISessionListener;
import com.stjerncraft.controlpanel.common.data.IServiceApiInfo;
import com.stjerncraft.controlpanel.common.data.IServiceProviderInfo;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IClientCoreApi extends IClientCore {
	IClientSession getCoreSession();
	IClientSession startSession(IServiceApiInfo api, IServiceProviderInfo serviceProvider, ISessionListener listener);
}
