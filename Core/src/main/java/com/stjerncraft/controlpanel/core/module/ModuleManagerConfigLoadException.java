package com.stjerncraft.controlpanel.core.module;

@SuppressWarnings("serial")
public class ModuleManagerConfigLoadException extends Exception {
	public String field;
	public String reason;
	
	public ModuleManagerConfigLoadException(String field, String reason) {
		super("Error while loading Module Manager Config field " + field + ": " + reason);
		this.field = field;
		this.reason = reason;
	}
}
