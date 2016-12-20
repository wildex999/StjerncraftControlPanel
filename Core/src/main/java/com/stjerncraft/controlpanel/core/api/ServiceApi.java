package com.stjerncraft.controlpanel.core.api;

import java.util.Objects;

/**
 * Information about a defined Service API, which is implemented Service Providers.
 * A unique Service API is identified by it's full name and version.
 */

public class ServiceApi {
	final protected String name;
	final protected int version;
	
	public ServiceApi(String name, int version) {
		this.name = name;
		this.version = version;
	}
	
	public String getName() {
		return name;
	}
	
	public int getVersion() {
		return version;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ServiceApi))
			return false;
		
		ServiceApi other = (ServiceApi)obj;
		
		if(!Objects.equals(name, other.name))
			return false;
		
		if(version != other.version)
			return false;
		
		return true;
	}
}
