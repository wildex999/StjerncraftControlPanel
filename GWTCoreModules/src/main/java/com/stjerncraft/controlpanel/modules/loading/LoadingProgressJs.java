package com.stjerncraft.controlpanel.modules.loading;

import com.stjerncraft.controlpanel.client.api.JsUtils;

import jsinterop.annotations.JsType;

@JsType(isNative=true, namespace = JsUtils.NamespaceLocal)
public class LoadingProgressJs {
	public static native void setProgress(String divId, int loadedModules, int countModules, String latestModule);
}
