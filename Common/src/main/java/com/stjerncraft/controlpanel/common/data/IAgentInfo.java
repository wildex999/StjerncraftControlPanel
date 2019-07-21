package com.stjerncraft.controlpanel.common.data;

import com.stjerncraft.controlpanel.api.client.IServiceProviderInfo;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IAgentInfo {
	String getUuid();
	String getName();
	IServiceProviderInfo[] getProviders();
}
