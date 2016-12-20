package com.stjerncraft.controlpanel.api.processor;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import org.json.JSONObject;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.stjerncraft.controlpanel.api.IServiceApiGenerated;
import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.api.Version;

/**
 * Generates the API Class, which is responsible for handling.
 * It will setup and register the API when needed.
 * It will parse external method and event calls, and serialize the responses.
 * 
 * The goal is to turn a serialized JSON object into a native method call on the Service Provider implementation, 
 * without having to do reflection.
 * A Service Manager should be able to just pass along strings to the right handler.
 * 
 * The API call is serialized into a JSON object, containing the method name and named arguments.
 * The arguments are serialized depending on their type, most going to either the native JSON type or strings.
 * Data Objects are serialized as inline JSON objects, containing it's own data, which is parsed by the Data Object Class.
 * 
 * Example:
 * apiClass.callMethod(String[] serializedMethodCall)
 */

class ApiClassGenerator {
	
	protected DataObjectProcessor dataObjects;

	public ApiClassGenerator(DataObjectProcessor dataObjects) {
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
		
		
		TypeSpec generatedClass = TypeSpec.classBuilder(className + ApiStrings.APISUFFIX)
			.addModifiers(Modifier.PUBLIC)
			.addSuperinterface(IServiceApiGenerated.class)
			.addMethod(getSerializerVersion)
			.addMethod(getApiVersion)
			.addMethod(getApiName)
			.addMethod(generateCallMethod(api))
			.build();
		
		JavaFile javaFile = JavaFile.builder(packageName, generatedClass).build();
		javaFile.writeTo(filer);
	}
	
	protected MethodSpec generateCallMethod(ServiceApiInfo api) {
		MethodSpec.Builder method = MethodSpec.methodBuilder("callMethod")
				.addModifiers(Modifier.PUBLIC)
				.returns(String.class)
				.addParameter(IServiceProvider.class, "serviceProvider")
				.addParameter(JSONObject.class, "method")
				.addStatement("return \"\"");
		
		return method.build();
	}
	
}
