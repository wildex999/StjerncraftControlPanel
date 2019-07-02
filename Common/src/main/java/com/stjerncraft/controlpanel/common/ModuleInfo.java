package com.stjerncraft.controlpanel.common;

import com.stjerncraft.controlpanel.api.annotation.DataObject;

@DataObject
public class ModuleInfo {
	public String name;
	public int version;
	
	//DataObject constructor
	public ModuleInfo() {}
	
	public ModuleInfo(String name, int version) {
		this.name = name;
		this.version = version;
	}
	
	public String getId() {
		return name + "V" + version;
	}
}
