package com.stjerncraft.controlpanel.api.processor;

import java.util.List;
import java.util.function.Function;

import org.json.JSONArray;

import com.squareup.javapoet.MethodSpec.Builder;

public class Parse {
	/**
	 * Generate the code parsing the given Variable from a JSONArray.
	 * @param arg The argument field containing the definition
	 * @param argArrayVar Variable name of the JSONArray containing all the arguments
	 * @param argArrayIndex Index of the variable to parse in the argArrayVar.
	 * @param target Method to add the parsing code to.
	 * @param dataObjects All known DataObject types.
	 * @param argVar The variable name to store the parsed value to
	 * @throws IllegalArgumentException If it fails to generate parser for the given Variable
	 */
	public static void parseVariable(Field arg, String argArrayVar, int argArrayIndex, Builder target, DataObjectProcessor dataObjects, String argVar) throws IllegalArgumentException {
		if(arg.fieldType == null)
			throw new IllegalArgumentException("Null fieldType at field: " + arg);
		String argClass = arg.fieldType.getCanonicalName();

		//Will return the string for parsing an argument value
		//Ex: "(short)$L.getInt($L)
		//Ex: "CustomDataClass.parse($L.getJSONObject($L))"
		Function<Void, String> getParseStr = (Void v) -> {
			String parseStr;
			if(arg.fieldType.isEnum)
				parseStr = argClass + ".valueOf($L.getString($L))";
			else if(dataObjects.getParsedDataObjects().containsKey(argClass))
				parseStr = argClass + ApiStrings.DATAOBJECTSUFFIX + ".parseJson($L.getJSONArray($L))";
			else {
				//Check base types
				FieldType ft = arg.fieldType;
				if(ft == BaseType.Boolean.type)
					parseStr = "$L.getBoolean($L)";
				else if(ft == BaseType.Byte.type || ft == BaseType.Character.type || 
						ft == BaseType.Short.type || ft == BaseType.Integer.type)
					parseStr = "(" + arg.fieldType.getPrimitiveName() + ")$L.getInt($L)"; //ex: (short)arg.getInt(index);
				else if(ft == BaseType.Double.type || ft == BaseType.Float.type)
					parseStr = "(" + arg.fieldType.getPrimitiveName() + ")$L.getDouble($L)";
				else if(ft == BaseType.Long.type)
					parseStr = "$L.getLong($L)";
				else if(ft == BaseType.String.type)
					parseStr = "$L.getString($L)";
				else
					throw new IllegalArgumentException("Unknown type for field " + arg);
			}
			return parseStr;
		};

		if(arg.isArray) {
			//For an array we have to parse each value in the array(TODO: Arrays of arrays? We would need to be recursive)
			String arrayName = argVar + "__TempArray"; //Can't have duplicates of name if inside switch
			target.addStatement("$T $L = $L.getJSONArray($L)", JSONArray.class, arrayName, argArrayVar, argArrayIndex)
			.addStatement("$L[] $L = new $L[$L.length()]", argClass, argVar, argClass, arrayName)
			.beginControlFlow("for(int index = 0; index < $L.length(); index++)", arrayName)
			.addStatement("$L[index] = " + getParseStr.apply(null), argVar, arrayName, "index")
			.endControlFlow();
		} else
			target.addStatement("$L $L = " + getParseStr.apply(null), argClass, argVar, argArrayVar, argArrayIndex);
	}
	
	/**
	 * Generate the code for parsing multiple variables.
	 * @param args Set of Fields describing the variables to parse.
	 * @param target The target to put the generated code into.
	 * @param dataObjects All known DataObject types.
	 * @param contextName Name to put into the variable name when storing the parsed value, used to avoid name conflicts when possible(In a switch for example)
	 * @return List of the variable names that the parsed values is stored into
	 */
	public static String[] parseVariables(List<Field> args, String argArrayVar, Builder target, DataObjectProcessor dataObjects, String contextName) {
		int argIndex = 0;
		String[] varNames = new String[args.size()];
		for(Field arg : args) {
			String argVar = "arg_" + contextName + "_" + arg.name; //Need unique name since we are inside a switch
			Parse.parseVariable(arg, argArrayVar, argIndex, target, dataObjects, argVar);
			varNames[argIndex] = argVar;
			argIndex++;
		}
		
		return varNames;
	}
}
