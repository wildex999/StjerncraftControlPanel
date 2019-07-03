package com.stjerncraft.controlpanel.api.processor;

import java.util.ArrayList;
import java.util.List;

/**
 * A Method exists in a Service API, and describes the methods which can be called on it.
 */

class Method {
	protected String name;
	protected String methodName; //The actual name of the method which is called. Might differ from name when multiple methods of the same name exists
	protected List<Field> parameters = new ArrayList<Field>(); //Use list to retain order
	
	protected FieldType returnType;
	protected boolean isReturnArray;
	
	//EventHandler
	protected boolean isEventHandler;
	protected FieldType eventDataType;
	

	public Method(String name) {
		this(name, name);
	}
	
	public Method(String name, String methodName) {
		this.name = name;
		this.methodName = methodName;
		this.isEventHandler = false;
	}
	
	public void addParameter(Field par) {
		if(parameters.contains(par))
			return;
		
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
		if(isEventHandler != other.isEventHandler)
			return false;
		if(eventDataType != other.eventDataType)
			return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		return "Method: " + returnType + (isReturnArray ? "[]" : "") + " " + name + "(" + parameters + ")";
	}
}
