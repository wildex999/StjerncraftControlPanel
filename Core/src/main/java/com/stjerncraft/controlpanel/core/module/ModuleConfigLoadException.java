package com.stjerncraft.controlpanel.core.module;

@SuppressWarnings("serial")
public class ModuleConfigLoadException extends Exception {
	public String moduleName;
	public String field;
	
	public ModuleConfigLoadException(String moduleName, String field, String reason) {
		super("Error while loading module '" + moduleName + "' field '" + field + "':" + reason);
		this.moduleName = moduleName;
		this.field = field;
	}
	
	
}
