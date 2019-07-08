package com.stjerncraft.controlpanel.api.processor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class ServiceApiInfo {
	protected String name;
	protected int version;
	protected Set<Method> methods;
	
	public ServiceApiInfo(String name, int version) {
		this.name = name;
		this.version = version;
		methods = new HashSet<>();
	}
	
	public String getName() {
		return name;
	}
	
	public int getVersion() {
		return version;
	}
	
	public void addMethod(Method method) {
		methods.add(method);
	}
	
	public Method getMethodWithName(String simpleName) {
		for(Method method : methods) {
			if(method.name.equals(simpleName))
				return method;
		}
		
		return null;
	}
	
	public Collection<Method> getMethods() {
		return methods;
	}
	
	@Override
	public String toString() {
		return "ServiceApiInfo: " + name + " | " + version;
	}
}
