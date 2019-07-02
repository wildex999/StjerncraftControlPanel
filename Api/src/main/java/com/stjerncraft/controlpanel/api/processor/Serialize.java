package com.stjerncraft.controlpanel.api.processor;

import java.util.function.Function;

import org.json.JSONArray;

import com.squareup.javapoet.MethodSpec.Builder;

public class Serialize {
	
	/**
	 * Serialize the given variable into an existing JSONArray.
	 * This will take care of calling the correct put, and creating arrays.
	 * @param type The type of the variable
	 * @param varName Name of the variable containing the field(Can be a statement, like: "obj.varName", or "var[0]")
	 * @param isArray True if the variable is a array of the given type
	 * @param target The target builder to write code to
	 * @param dataObjects
	 * @param jsonArrayVar The JSONArray to write into
	 */
	public static void serializeVariable(FieldType type, String varName, boolean isArray, Builder target, DataObjectProcessor dataObjects, String jsonArrayVar) {
		if(type == null)
			throw new IllegalArgumentException("Null fieldType at field: " + varName);
		String argClass = type.getCanonicalName();

		
		//Will return the string for serializing the value
		//Ex: "(int)$L
		//Ex: "CustomDataClass.serialize($L)"
		Function<Void, String> getSerializeStr = (Void v) -> {
			String serializeStr;
			if(type.isEnum)
				serializeStr = "$L.toString()";
			else if(dataObjects.getParsedDataObjects().containsKey(argClass))
				serializeStr = argClass + ApiStrings.DATAOBJECTSUFFIX + ".serializeObject($L)";
			else {
				//We need to cast classes(Float, Integer etc.) down to base type(float, int etc.) so it's not stored as an Object
				FieldType ft = type;
				if(ft == BaseType.Boolean.type)
					serializeStr = "(boolean)$L";
				else if(ft == BaseType.Byte.type || ft == BaseType.Character.type || 
						ft == BaseType.Short.type || ft == BaseType.Integer.type)
					serializeStr = "(int)$L";
				else if(ft == BaseType.Double.type || ft == BaseType.Float.type)
					serializeStr = "(double)$L";
				else if(ft == BaseType.Long.type)
					serializeStr = "(long)$L";
				else if(ft == BaseType.String.type)
					serializeStr = "$L";
				else
					throw new IllegalArgumentException("Unknown type for field " + varName);
			}
			return serializeStr;
		};
		
		if(isArray) {
			//For an array we have to serialize each value in the array(TODO: Array of arrays? We would need to be recursive)
			String arrayName = varName.replaceAll("\\.", "_") + "__TempArray"; //Can't have duplicates of name if inside switch
			target.addStatement("$T $L = new $T()", JSONArray.class, arrayName, JSONArray.class)
			.beginControlFlow("for(int index = 0; index < $L.length; index++)", varName)
			.addStatement("$L.put(" + getSerializeStr.apply(null) + ")", arrayName, varName + "[index]")
			.endControlFlow()
			.addStatement("$L.put($L)", jsonArrayVar, arrayName);
		} else
			target.addStatement("$L.put(" + getSerializeStr.apply(null) + ")", jsonArrayVar, varName);
	}
}
