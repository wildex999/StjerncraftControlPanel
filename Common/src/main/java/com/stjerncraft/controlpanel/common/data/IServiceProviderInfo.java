package com.stjerncraft.controlpanel.common.data;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IServiceProviderInfo {
	String getUuid();
	String getAgentUuid();
	IServiceApiInfo[] getApis();
}
