package com.stjerncraft.controlpanel.common;

import java.util.Objects;

import com.stjerncraft.controlpanel.common.data.ServiceApiInfo;


public class ServiceApi extends ServiceApiInfo {

	public ServiceApi() {}
	
	public ServiceApi(String name, int version) {
		super(name, version);
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
	
	@Override
	public String toString() {
		return "Name: " + name + ". Version: " + version;
	}
}
