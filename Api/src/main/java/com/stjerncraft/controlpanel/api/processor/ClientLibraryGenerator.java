package com.stjerncraft.controlpanel.api.processor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import org.json.JSONArray;
import org.json.JSONObject;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.stjerncraft.controlpanel.api.IClientCore;
import com.stjerncraft.controlpanel.api.exceptions.CallMethodException;

/**
 * Generates the API Client Library for use by GWT Clients.
 * Provides Async Methods for the API calls, returning a Future instead of the return value.
 */
public class ClientLibraryGenerator {
DataObjectProcessor dataObjects;
	
	public ClientLibraryGenerator(DataObjectProcessor dataObjects) {
		this.dataObjects = dataObjects;
	}

	/**
	 * Generate a Client Library class for each Service API
	 * @param filer
	 * @throws IOException
	 */
	public void generateClientLibrary(Filer filer, ServiceApiInfo api) throws IOException {
		String packageName = "";
		String className = api.getName();
		int nameIndex = className.lastIndexOf(".");
		if(nameIndex != -1) {
			packageName = className.substring(0, nameIndex);
			className = className.substring(nameIndex+1);
		}
		
		TypeSpec.Builder builder = TypeSpec.classBuilder(className + ApiStrings.APICLIENTLIBRARYSUFFIX)
				.addField(IClientCore.class, "clientCore", Modifier.PRIVATE)
				.addField(int.class, "sessionId", Modifier.PRIVATE)
				.addModifiers(Modifier.PUBLIC)
				.addMethod(generateConstructor());
		
		Collection<Method> methods = api.getMethods();
		for(Method method : methods) {
			MethodSpec generatedMethod = generateMethod(method);
			builder.addMethod(generatedMethod);
		}
		 
		TypeSpec generatedClass = builder.build();
		JavaFile javaFile = JavaFile.builder(packageName, generatedClass).build();
		javaFile.writeTo(filer);
	}
	
	MethodSpec generateConstructor() {
		return MethodSpec.constructorBuilder()
			.addModifiers(Modifier.PUBLIC)
			.addParameter(IClientCore.class, "clientCore")
			.addParameter(int.class, "sessionId")
			.beginControlFlow("if(clientCore == null)")
			.addStatement("throw new $T($S)", RuntimeException.class, "Missing Client Core")
			.endControlFlow()
			.addStatement("this.clientCore = clientCore")
			.addStatement("this.sessionId = sessionId")
			.build();
	}
	
	/**
	 * Generate the API methods to proxy for the Client Core
	 * @return
	 */
	MethodSpec generateMethod(Method method) {
		MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getName())
				.addModifiers(Modifier.PUBLIC)
				.returns(void.class);
		
		//Parameters
		for(Field par : method.getParameters()) {
			TypeName type = ClassName.bestGuess(par.fieldType.getCanonicalName());
			if(par.isArray)
				type = ArrayTypeName.of(type);
			
			methodBuilder.addParameter(type, par.name);
		}
		
		//Callback if applicable
		String callback;
		if(method.getReturnType() != BaseType.Void.type)
		{
			TypeName returnType = ClassName.bestGuess(method.returnType.getCanonicalName());
			if(method.isReturnArray())
				returnType = ArrayTypeName.of(returnType);
			methodBuilder.addParameter(ParameterizedTypeName.get(ClassName.get(Consumer.class), returnType), "callback");
			
			//Create Callback proxy, deserializing the result
			callback = "callbackProxy";
			
			CodeBlock.Builder callbackBuilder = CodeBlock.builder();
			callbackBuilder.addStatement("$T returnJSON = new $T(value)", JSONArray.class, JSONArray.class);
			Field returnField = new Field("", method.isReturnArray(), method.getReturnType());
			Parse.parseVariable(returnField, "returnJSON", 0, callbackBuilder, dataObjects, "returnValue");
			callbackBuilder.addStatement("callback.accept(returnValue)");
			
			methodBuilder.addStatement("Consumer<String> $L = (String value) -> { $L }", callback, callbackBuilder.build().toString());
			
		} else
			callback = "null";
		
		//Serialize parameters into JSON method object
		String parArray = "methodParameters";
		methodBuilder.addStatement("$T $L = new $T()", JSONArray.class, parArray, JSONArray.class);
		for(Field par : method.getParameters())
		{
			Serialize.serializeVariable(par.fieldType, par.name, par.isArray, methodBuilder, dataObjects, parArray);
		}
		String methodObj = "methodObject";
		methodBuilder.addStatement("$T $L = new $T()", JSONObject.class, methodObj, JSONObject.class);
		methodBuilder.addStatement("$L.put($S, $L)", methodObj, method.getName(), parArray);
		
		//Send to Core
		methodBuilder.addStatement("clientCore.callMethod(sessionId, $L.toString(), $L)", methodObj, callback);

		return methodBuilder.build();
	}
}
