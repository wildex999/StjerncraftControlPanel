package com.stjerncraft.controlpanel.api.processor;

import java.util.Objects;

public class FieldType {
	public String name;
	public String[] classPaths;
	
	FieldType(String name, String... classPaths) {
		this.name = name;
		this.classPaths = classPaths;
		
	}
	
	public String getCanonicalName() {
		return this.classPaths[0];
	}
	
	public String getPrimitiveName() {
		return this.classPaths[1];
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(!(obj instanceof FieldType))
			return false;
		
		//If class path matches, they are the same type
		FieldType other = (FieldType)obj;
		return Objects.equals(getCanonicalName(), other.getCanonicalName());
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getCanonicalName());
	}
	
}
