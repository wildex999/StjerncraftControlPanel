package com.stjerncraft.controlpanel.client.api.module;

import com.google.gwt.core.client.GWT;

import jsinterop.annotations.JsType;

@JsType
public abstract class BaseClientModule implements IClientModule {

	@Override
	public String getName() {
		//The name it is registered as in the Core is the real name, which might differ from the GWT Module name.
		//So we try to get it from the path we loaded the Module from, using the GWT module name as a fallback.
		String[] path = GWT.getModuleBaseURL().split("/");
		if(path.length == 0) {
			return GWT.getModuleName();
		}
		
		return path[path.length-1];
	}

}
