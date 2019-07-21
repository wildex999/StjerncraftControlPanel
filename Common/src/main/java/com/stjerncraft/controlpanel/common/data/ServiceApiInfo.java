package com.stjerncraft.controlpanel.common.data;

import java.util.Objects;

import com.stjerncraft.controlpanel.api.annotation.DataObject;
import com.stjerncraft.controlpanel.api.client.IServiceApiInfo;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;


/**
 * Information about a defined Service API, which is implemented by Service Providers.
 * An unique Service API is identified by its full name and version.
 */

@JsType
@DataObject
public class ServiceApiInfo implements IServiceApiInfo {
	public String name;
	public int version;
	
	@JsIgnore
	public ServiceApiInfo() {
		this("", 0);
	}
	
	public ServiceApiInfo(String name, int version) {
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
		return getId(name, version);
	}
	
	/**
	 * Get the ID uniquely identifying this API(Name + Version)
	 * @return
	 */
	public static String getId(String name, int version) {
		return name + "V" + version;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		//JsInterop can't do instanceof check
		try {
			IServiceApiInfo other = (IServiceApiInfo)obj;
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
