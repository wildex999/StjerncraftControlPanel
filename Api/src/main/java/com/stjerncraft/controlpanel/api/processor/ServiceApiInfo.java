package com.stjerncraft.controlpanel.api.processor;

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
}
