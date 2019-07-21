package com.stjerncraft.controlpanel.api.processor;

/**
 * A Field consists of a name and type, and is used to describe variables in a DataObject, Method and Event.
 */

public class Field {
	public FieldType fieldType;
	public boolean isArray;
	public String name;
	
	public Field(String name, boolean isArray, FieldType type) {
		this.fieldType = type;
		this.isArray = isArray;
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Field))
			return false;
		
		Field other = (Field)obj;
		if(fieldType != other.fieldType)
			return false;
		if(isArray != other.isArray)
			return false;
		if(!name.equals(other.name))
			return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		return fieldType + (isArray ? "[]" : "") + " " + name;
	}
}
