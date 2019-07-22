package com.stjerncraft.controlpanel.common.data;

import java.util.Objects;

import com.stjerncraft.controlpanel.api.annotation.DataObject;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType
@DataObject
public class ModuleInfo implements IModuleInfo {
	public String name;
	public String descriptiveName;
	
	//DataObject constructor
	@JsIgnore
	public ModuleInfo() {
		this("", "");
	}
	
	public ModuleInfo(String name, String descriptiveName) {
		this.name = name;
		this.descriptiveName = descriptiveName;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getDescriptiveName() {
		return descriptiveName;
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
		} catch(Exception e) {
			return false;
		}

		return true;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
