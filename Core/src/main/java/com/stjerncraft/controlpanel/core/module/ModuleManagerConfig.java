package com.stjerncraft.controlpanel.core.module;

import java.util.List;

import com.google.common.base.Strings;
import com.google.gson.Gson;

/**
 * The Module Manager Config is stored as a JSON object.
 * It provides the relative location to the root folder for all the modules.
 * It also defines the list of activte modules.
 */
public class ModuleManagerConfig {	
	public String location;
	public List<String> active;
	
	public ModuleManagerConfig() {
	}
	
	/**
	 * Save the Module Manager Config to a JSON string
	 * @return
	 */
	public String save() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	/**
	 * Load a Module Manager Config from the given JSON string
	 * @param jsonStr
	 * @return
	 * @throws ModuleManagerConfigLoadException 
	 */
	public static ModuleManagerConfig load(String jsonStr) throws ModuleManagerConfigLoadException {
		Gson gson = new Gson();
		ModuleManagerConfig config =  gson.fromJson(jsonStr, ModuleManagerConfig.class);
		config.verify();
		return config;
	}
	
	private void verify() throws ModuleManagerConfigLoadException {
		if(Strings.isNullOrEmpty(location))
			throw new ModuleManagerConfigLoadException("descriptiveName", "Required field is missing.");
	}
}
