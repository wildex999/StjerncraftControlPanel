package com.stjerncraft.controlpanel.common.data;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IModuleInfo {
	String getName();
	int getVersion();
	String getId();
	
	/**
	 * Get the Path to the module script file.
	 * @return
	 */
	String getFilePath();
}
