package com.stjerncraft.controlpanel.api.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class DataObjectInfo {
	String name;
	Set<Field> fields;
	
	public DataObjectInfo(String name) {
		this.name = name;
		this.fields = new HashSet<>();
	}
	
	public String getName() {
		return name;
	}
	
	public List<Field> getFields() {
		return new ArrayList<>(fields);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof DataObjectInfo))
			return false;
		
		DataObjectInfo other = (DataObjectInfo)obj;
		if(!name.equals(other.name))
			return false;
		
		if(!fields.equals(other.fields))
			return false;
		
		return true;
	}
	
	public void addField(Field field) {
		fields.add(field);
	}
}
