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
	 * @param type
	 * @return
	 */
	public static FieldType getActualFieldType(TypeMirror type) {
		if(FieldCheck.isArray(type))
			return FieldType.getType(FieldCheck.getArrayComponentType(type).toString());
		else
			return FieldType.getType(type.toString());
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
		if(FieldType.getType(typeStr) != null) //String, Integer, Float...
			return true;
		if(dataObjects.containsKey(typeStr)) //This or another DataObject
			return true;
		
		return false;
	}
}
