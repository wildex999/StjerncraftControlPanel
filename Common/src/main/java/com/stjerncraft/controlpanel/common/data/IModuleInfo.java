package com.stjerncraft.controlpanel.common.data;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IModuleInfo {
	/**
	 * Get the Simple name for this module.
	 * This must be unique, and is used as the path for loading the module.
	 * @return
	 */
	String getName();
	
	/**
	 * Get the Descriptive name for this Module, which is displayed to the user.
	 * @return
	 */
	String getDescriptiveName();
}
