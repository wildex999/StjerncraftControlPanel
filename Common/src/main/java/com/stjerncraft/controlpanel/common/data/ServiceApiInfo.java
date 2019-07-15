package com.stjerncraft.controlpanel.common.data;

import com.stjerncraft.controlpanel.api.annotation.DataObject;

/**
 * Information about a defined Service API, which is implemented by Service Providers.
 * An unique Service API is identified by its full name and version.
 */
@DataObject
public class ServiceApiInfo {
	public String name;
	public int version;
	
	public ServiceApiInfo() {}
	
	public ServiceApiInfo(String name, int version) {
		this.name = name;
		this.version = version;
	}
	
	/**
	 * Get the ID uniquely identifying this API(Name + Version)
	 * @return
	 */
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
		
		if(!(obj instanceof ServiceApiInfo))
			return false;
		
		ServiceApiInfo other = (ServiceApiInfo)obj;
		if(!other.name.equals(name))
			return false;
		
		if(other.version != version)
			return false;
		
		return true;
	}
}
