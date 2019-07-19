package com.stjerncraft.controlpanel.api.client;

import jsinterop.annotations.JsType;

/**
 * Handler for the Return value of CallMethod.
 */
@JsType(isNative=true)
public interface ICallMethodReturnHandler<T> {
	void onReturnValue(T value);
}
