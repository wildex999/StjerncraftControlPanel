package com.stjerncraft.controlpanel.api.processor;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import org.json.JSONArray;
import org.json.JSONObject;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.stjerncraft.controlpanel.api.IServiceApiGenerated;
import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.api.Version;
import com.stjerncraft.controlpanel.api.exceptions.CallMethodException;

/**
 * Generates the API Class, which is responsible for handling.
 * It will setup and register the API when needed.
 * It will parse external method and event calls, and serialize the responses.
 * 
 * The goal is to turn a serialized JSON object into a native method call on the Service Provider implementation, 
 * without having to do reflection or writing special handling code manually.
 * A Service Manager should be able to just pass along strings to the right handler.
 * 
 * The API call is serialized into a JSON object, containing the method name and named arguments.
 * The arguments are serialized depending on their type, most going to either the native JSON type or strings.
 * Data Objects are serialized as inline JSON objects, containing it's own data, which is handled by the generated Data Object Class.
 */

class ServiceApiClassGenerator {
	
	protected DataObjectProcessor dataObjects;

	public ServiceApiClassGenerator(DataObjectProcessor dataObjects) {
		this.dataObjects = dataObjects;
	}
	
	public void generateClassForApi(Filer filer, ServiceApiInfo api) throws IOException {
		String packageName = "";
		String className = api.getName();
		int nameIndex = className.lastIndexOf(".");
		if(nameIndex != -1) {
			packageName = className.substring(0, nameIndex);
			className = className.substring(nameIndex+1);
		}
		
		//Generate API Methods
		MethodSpec getSerializerVersion = MethodSpec.methodBuilder("getSerializerVersion")
			.addModifiers(Modifier.PUBLIC)
			.returns(int.class)
			.addStatement("return $L", Version.SerializerVersion)
			.build();
		MethodSpec getApiVersion = MethodSpec.methodBuilder("getApiVersion")
			.addModifiers(Modifier.PUBLIC)
			.returns(int.class)
			.addStatement("return $L", api.getVersion())
			.build();
		MethodSpec getApiName = MethodSpec.methodBuilder("getApiName")
			.addModifiers(Modifier.PUBLIC)
			.returns(String.class)
			.addStatement("return $S", api.getName())
			.build();
		MethodSpec callMethod = MethodSpec.methodBuilder("callMethod")
			.addModifiers(Modifier.PUBLIC)
			.returns(String.class)
			.addParameter(IServiceProvider.class, "serviceProvider")
			.addParameter(String.class, "method")
			.addStatement("return callMethod(($L)serviceProvider, method)", api.name.replaceAll("\\$", "."))
			.build();
		MethodSpec callDirectMethod = generateCallMethod(api);
		
		TypeSpec generatedClass = TypeSpec.classBuilder(className + ApiStrings.APISUFFIX)
			.addModifiers(Modifier.PUBLIC)
			.addSuperinterface(IServiceApiGenerated.class)
			.addMethod(getSerializerVersion)
			.addMethod(getApiVersion)
			.addMethod(getApiName)
			.addMethod(callMethod)
			.addMethod(callDirectMethod)
			.build();
		
		JavaFile javaFile = JavaFile.builder(packageName, generatedClass).build();
		javaFile.writeTo(filer);
	}
	
	/**
	 * Generate callMethod. It will take in the JSON method call, deserialize it and then call it on the Service Provider.
	 * It will then take the return value from the called method, serialize it and then pass it back as a string.
	 * 
	 * A Method call is a JSON Object with one field. The field name is the method name, and contains an array of method arguments:
	 * {
	 *     "methodName": ["strArg", 10, {"field1": "strField"}, [1, 2, 3]]
	 * }
	 * 
	 * The return value will be a serialized JSONArray. The JSONArray will only contain a single entry(Either a value or an array) for now.
	 * 7 -> [7]
	 * [7, 5, 3] -> [[7, 5, 3]]
	 * "Str" -> ["Str"]
	 * Inst -> [{"field1", "field2", [], 1}]
	 */
	protected MethodSpec generateCallMethod(ServiceApiInfo api) {
		MethodSpec.Builder method = MethodSpec.methodBuilder("callMethod")
			.addModifiers(Modifier.PUBLIC)
			.returns(String.class)
			.addParameter(ClassName.bestGuess(api.name.replaceAll("\\$", ".")), "serviceProvider")
			.addParameter(String.class, "method");
				
				
		//Deserialize JSON
		method.addStatement("$T obj = new $T(method)", JSONObject.class, JSONObject.class)
			.beginControlFlow("if(obj.keySet().size() != $L)", 1)
			.addStatement("throw new $T(this, $S, $S + obj.keySet().size() + $S, method)", CallMethodException.class, "?", "Incorrect number of keys: ", ", expected 1")
			.endControlFlow()
				
			.addStatement("$T methodName = obj.keySet().iterator().next()", String.class)
			.addStatement("$T args = obj.getJSONArray(methodName)", JSONArray.class);
				
		//Method Switch
		method.beginControlFlow("switch(methodName)");
		for(Method apiMethod : api.methods) {
			method.addCode("case $S:\n", apiMethod.name)
			.beginControlFlow("") //Indent the case
			.beginControlFlow("if(args.length() != $L)", apiMethod.parameters.size())
			.addStatement("throw new $T(this, methodName, $S + args.length() + $S, method)", CallMethodException.class, "Incorrect number of arguments: ", ", expected " + apiMethod.parameters.size())
			.endControlFlow();

			//Parse arguments
			String[] varNames;
			try {
				varNames = Parse.parseVariables(apiMethod.parameters, "args", method, dataObjects, apiMethod.name);
			}
			catch(IllegalArgumentException e) {
				throw new IllegalArgumentException("Error while generating variable parser in callMethod for " + apiMethod + " while parsing api " + api, e);
			}
			
			String argsString = String.join(", ", varNames);

			//Call method & serialize return value
			String retVal = "ret_" + apiMethod.name; //Need unique name since we are inside a switch
			String retValJson = retVal + "_json";
			if(apiMethod.isReturnArray) {
				method.addStatement("$L[] $L = serviceProvider.$L($L)", apiMethod.returnType.getCanonicalName(), retVal, apiMethod.methodName, argsString);
			} else if(apiMethod.returnType != BaseType.Void.type) {
				method.addStatement("$L $L = serviceProvider.$L($L)", apiMethod.returnType.getCanonicalName(), retVal, apiMethod.methodName, argsString);
			} else {
				method.addStatement("serviceProvider.$L($L)", apiMethod.methodName, argsString);
				method.addStatement("return null");
			}
			
			//Serialize
			if(apiMethod.isReturnArray || apiMethod.returnType != BaseType.Void.type) {
				method.addStatement("$T $L = new $T()", JSONArray.class, retValJson, JSONArray.class);
				Serialize.serializeVariable(apiMethod.returnType, retVal, apiMethod.isReturnArray, method, dataObjects, retValJson);
				method.addStatement("return $L.toString()", retValJson);
			}
			method.endControlFlow();
			
		}
		method.endControlFlow();
		
		//TODO: Handle reaching this point(Unknown method)
		method.addStatement("throw new $T(this, methodName, $S, method)", CallMethodException.class, "Unknown method");
		
		return method.build();
	}
	
}
