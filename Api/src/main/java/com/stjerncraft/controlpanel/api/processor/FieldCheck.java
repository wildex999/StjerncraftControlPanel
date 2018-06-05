package com.stjerncraft.controlpanel.api.processor;

import java.util.Map;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

class FieldCheck {

	/**
	 * Get the type of the array
	 * @param type
	 * @return Null if the type is not an array
	 */
	public static TypeMirror getArrayComponentType(TypeMirror type) {
		if(!isArray(type))
			return null;
		
		ArrayType array = (ArrayType)type;
		return array.getComponentType();
	}
	
	/**
	 * Check whether the given type is a array
	 * @param type
	 * @return
	 */
	public static boolean isArray(TypeMirror type) {
		return type.getKind() == TypeKind.ARRAY;
	}
	
	/**
	 * Get the FieldType for the given field, also for arrays.
	 * @param type TypeMirror whose FieldType to figure out.
	 * @param dataObjects A map of all known DataObjects. Can be null to ignore DataObjects.
	 * @return Null if it was not able to determine a valid FieldType for the given type.
	 */
	public static FieldType getActualFieldType(TypeMirror type, Map<String, DataObjectInfo> dataObjects) {
		FieldType fieldType = null;
		if(FieldCheck.isArray(type))
			type = FieldCheck.getArrayComponentType(type);
		
		BaseType baseType = BaseType.getType(type.toString());
		if(baseType == null) {
			//Might be a DataObject
			DataObjectInfo dataObject = dataObjects.get(type.toString());
			if(dataObject != null)
				fieldType = new FieldType(dataObject.name, dataObject.name);
		} else
			fieldType = baseType.type;
		
		
		if(fieldType == null) 
			return null;
		else
			return fieldType;
	}
	
	public static boolean isValidType(Map<String, DataObjectInfo> dataObjects, TypeMirror type) {
		if(isArray(type)) {
			//We only allow 1D arrays
			TypeMirror arrayType = getArrayComponentType(type);
			if(isArray(arrayType))
				return false;
			
			return isValidType(dataObjects, arrayType);
		}
		
		String typeStr = type.toString(); //Full path to type: com.obj.Object
		
		if(type.getKind().isPrimitive()) //int, long, double...
			return true;
		if(BaseType.getType(typeStr) != null) //String, Integer, Float...
			return true;
		if(dataObjects.containsKey(typeStr)) //This or another DataObject
			return true;
		
		return false;
	}
}
