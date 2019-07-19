package com.stjerncraft.controlpanel.common;

import java.util.Objects;

import com.stjerncraft.controlpanel.common.data.ServiceApiInfo;

public class ServiceApi extends ServiceApiInfo {

	public ServiceApi() {
		this("", 0);
	}
	
	public ServiceApi(String name, int version) {
		super(name, version);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ServiceApi))
			return false;
		
		ServiceApi other = (ServiceApi)obj;
		
		if(!Objects.equals(getName(), other.getName()))
			return false;
		
		if(getVersion() != other.getVersion())
			return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		return "Name: " + getName() + ". Version: " + getVersion();
	}
}
