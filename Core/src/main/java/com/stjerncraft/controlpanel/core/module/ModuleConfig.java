package com.stjerncraft.controlpanel.core.module;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

/**
 * Configuration for loaded modules.
 * Usually read from a required json config file in the module directory.
 * This will define things like the module info, author, permissions etc.
 */
public class ModuleConfig {
	private static ObjectMapper mapper = new ObjectMapper();
	
	public String descriptiveName; //This is the name shown to the user. Not used as a identifier.
	public String description; //A description of the module
	
	public ModuleConfig() {
		
	}
	
	
	public String save() throws JsonProcessingException {
		return mapper.writeValueAsString(this);
	}
	
	public static ModuleConfig load(String configJson) throws ModuleConfigLoadException, JsonParseException, JsonMappingException, IOException {
		ModuleConfig config = mapper.readValue(configJson, ModuleConfig.class);
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
