package com.stjerncraft.controlpanel.core.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

/**
 * The Module Manager Config is stored as a JSON object.
 * It provides the relative location to the root folder for all the modules.
 * It also defines the list of active modules.
 */
public class ModuleManagerConfig {	
	private static ObjectMapper mapper = new ObjectMapper();
	public String location;
	public List<String> active;
	
	public ModuleManagerConfig() {
	}
	
	/**
	 * Save the Module Manager Config to a JSON string
	 * @return
	 * @throws JsonProcessingException 
	 */
	public String save() throws JsonProcessingException {
		return mapper.writeValueAsString(this);
	}
	
	/**
	 * Load a Module Manager Config from the given JSON string
	 * @param jsonStr
	 * @return
	 * @throws ModuleManagerConfigLoadException 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static ModuleManagerConfig load(String jsonStr) throws ModuleManagerConfigLoadException, JsonParseException, JsonMappingException, IOException {
		ModuleManagerConfig config = mapper.readValue(jsonStr, ModuleManagerConfig.class);
		config.verify();
		return config;
	}
	
	public static ModuleManagerConfig createDefault() {
		ModuleManagerConfig newConfig = new ModuleManagerConfig();
		newConfig.location = "modules";
		newConfig.active = new ArrayList<>();
		
		return newConfig;
	}
	
	private void verify() throws ModuleManagerConfigLoadException {
		if(Strings.isNullOrEmpty(location))
			throw new ModuleManagerConfigLoadException("descriptiveName", "Required field is missing.");
	}
}
