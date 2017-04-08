package com.stjerncraft.controlpanel.core.module;

import com.google.common.base.Strings;
import com.google.gson.Gson;

/**
 * Configuration for loaded modules.
 * Usually read from a required json config file in the module directory.
 * This will define things like the module info, author, permissions etc.
 */
public class ModuleConfig {
	public String descriptiveName; //This is the name shown to the user. Not used as a identifier.
	public String description; //A description of the module
	
	public ModuleConfig() {
		
	}
	
	
	public String save() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public static ModuleConfig load(String configJson) throws ModuleConfigLoadException {
		Gson gson = new Gson();
		ModuleConfig config = gson.fromJson(configJson, ModuleConfig.class);
		config.verify();
		
		return config;
	}
	
	/**
	 * Verify whether the config is valid.
	 */
	private void verify() throws ModuleConfigLoadException {
		if(Strings.isNullOrEmpty(descriptiveName))
			throw new ModuleConfigLoadException("", "descriptiveName", "Required field is missing.");
	}
}
