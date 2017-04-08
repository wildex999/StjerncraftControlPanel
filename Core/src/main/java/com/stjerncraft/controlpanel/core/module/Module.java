package com.stjerncraft.controlpanel.core.module;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import com.google.common.base.Objects;
import com.google.common.io.Files;
import com.stjerncraft.controlpanel.api.annotation.DataObject;

@DataObject
public class Module {
	public String name;
	public int version;
	
	private ModuleConfig config;
	private Path location;
	
	//DataObject constructor
	public Module() {}
	
	public Module(String name, int version) {
		this.name = name;
		this.version = version;
	}
	
	/**
	 * Load the module at the given location.
	 * This will load the config.
	 * @param location The path to the root folder, containing the module config file.
	 * @throws IOException Thrown if it failed to load the config file.
	 */
	public void load(Path location) throws IOException, ModuleConfigLoadException {
		this.location = location.resolve("config.json");
		File configFile = this.location.toFile();

		String jsonStr = Files.toString(configFile, Charset.defaultCharset());
		config = ModuleConfig.load(jsonStr);
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
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Module))
			return false;
		
		if(obj == this)
			return true;
		
		Module other = (Module)obj;
		
		return Objects.equal(name, other.name) &&
				Objects.equal(version, other.version);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(name, version);
	}
}
