package com.stjerncraft.controlpanel.agent;

import java.util.Objects;

/**
 * Information about a defined Service API, which is implemented by Service Providers.
 * An unique Service API is identified by its full name and version.
 */
public class ServiceApi {
	final protected String name;
	final protected int version;
	final protected String uuid;
	
	public ServiceApi(String name, int version, String uuid) {
		this.name = name;
		this.version = version;
		this.uuid = uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public int getVersion() {
		return version;
	}
	
	public String getUuid() {
		return uuid;
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
