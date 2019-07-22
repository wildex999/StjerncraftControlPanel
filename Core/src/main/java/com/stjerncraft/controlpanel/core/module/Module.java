package com.stjerncraft.controlpanel.core.module;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.io.Files;
import com.stjerncraft.controlpanel.common.data.ModuleInfo;


public class Module extends ModuleInfo {	
	private ModuleConfig config;
	private Path location; //Path to the module root.
	
	//DataObject constructor
	public Module() {}
	
	public Module(String name) {
		super(name, "");
	}
	
	public Path getSourceFile() {
		return Paths.get(location.toString(), name + ".nocache.js");
	}
	
	/**
	 * Load the module at the given location.
	 * This will load the config.
	 * @param location The path to the root folder, containing the module config file.
	 * @throws IOException Thrown if it failed to load the config file.
	 */
	public void load(Path location) throws IOException, ModuleConfigLoadException {
		this.location = location;
		File configFile = this.location.resolve("config.json").toFile();

		String jsonStr = Files.toString(configFile, Charset.defaultCharset());
		config = ModuleConfig.load(jsonStr);
		
		descriptiveName = config.descriptiveName;
	}
	
	/**
	 * Save the module configuration
	 * @throws IOException Thrown if it failed to write the config file.
	 */
	public void save() throws IOException {
		if(location == null)
			return;
		
		File configFile = location.toFile();
		Files.write(config.save(), configFile, Charset.defaultCharset());
	}
}
