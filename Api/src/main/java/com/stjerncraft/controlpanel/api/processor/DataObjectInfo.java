package com.stjerncraft.controlpanel.api.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class DataObjectInfo {
	String name;
	List<Field> fields; //Use list to retain order
	
	public DataObjectInfo(String name) {
		this.name = name;
		this.fields = new ArrayList<>();
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
		if(fields.contains(field))
			return;
		
		fields.add(field);
	}
	
	/**
	 * Sort the field by name.
	 * Use this if the fields do not guarantee an order when getting them.
	 * This happens for declared fields in DataObject.
	 * We need the order to be constant, so the serialize and deserialze correctly places the values.
	 */
	public void sortFieldsByName() {
		Collections.sort(fields, new Comparator<Field>() {

			@Override
			public int compare(Field o1, Field o2) {
				return o1.name.compareTo(o2.name);
			}
		});
	}
}
