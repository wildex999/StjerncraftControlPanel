package com.stjerncraft.controlpanel.api.processor;

import java.util.HashSet;
import java.util.Set;

/**
 * A Method exists in a Service API, and describes the methods which can be called on it.
 */

class Method {
	protected String name;
	protected Set<Field> parameters = new HashSet<Field>();
	
	protected FieldType returnType;
	protected boolean isReturnArray;

	public Method(String name) {
		this.name = name;
	}
	
	public void addParameter(Field par) {
		parameters.add(par);
	}
	
	public void setReturnType(FieldType type, boolean isArray) {
		returnType = type;
		isReturnArray = isArray;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Method))
			return false;
		Method other = (Method)obj;
		
		if(!name.equals(other.name))
			return false;
		if(!parameters.equals(other.parameters))
			return false;
		if(returnType != other.returnType)
			return false;
		if(isReturnArray != other.isReturnArray)
			return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		return "Method: " + returnType + (isReturnArray ? "[]" : "") + " " + name + "(" + parameters + ")";
	}
}
