package com.stjerncraft.controlpanel.common.data;

import java.util.Objects;

import com.stjerncraft.controlpanel.api.annotation.DataObject;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType
@DataObject
public class ModuleInfo implements IModuleInfo {
	public String name;
	public int version;
	public String path;
	
	//DataObject constructor
	@JsIgnore
	public ModuleInfo() {
		this("", 0);
	}
	
	public ModuleInfo(String name, int version) {
		this.name = name;
		this.version = version;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public int getVersion() {
		return version;
	}
	
	@Override
	public String getId() {
		return name + "V" + version;
	}
	
	@Override
	public String getFilePath() {
		return path;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		//JsInterop can't do instanceof check
		try {
			IModuleInfo other = (IModuleInfo)obj;
			if(!other.getName().equals(name))
				return false;
			if(other.getVersion() != version)
				return false;
		} catch(Exception e) {
			return false;
		}

		return true;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, version);
	}
}
