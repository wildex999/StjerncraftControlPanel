package com.stjerncraft.controlpanel.client.api.webview;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IPage {
	String getName(); //i.e Core.UserLogin
	String getFilePath();
}
