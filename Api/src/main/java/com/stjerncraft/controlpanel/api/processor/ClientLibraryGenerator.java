package com.stjerncraft.controlpanel.api.processor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Consumer;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
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
		String packageName = api.getName().toLowerCase();
		String className = "Client";
		
		TypeSpec.Builder builder = TypeSpec.classBuilder(className)
				.addField(IClientCore.class, "clientCore", Modifier.PRIVATE)
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
			.beginControlFlow("if(clientCore == null)")
			.addStatement("throw new $T($S)", RuntimeException.class, "Missing Client Core")
			.endControlFlow()
			.addStatement("this.clientCore = clientCore")
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
		if(method.getReturnType() != BaseType.Void.type)
		{
			TypeName returnType = ClassName.bestGuess(method.returnType.getCanonicalName());
			if(method.isReturnArray())
				returnType = ArrayTypeName.of(returnType);
			methodBuilder.addParameter(ParameterizedTypeName.get(ClassName.get(Consumer.class), returnType), "callback");
		}
		
		//Body
		//Serialize parameters
		//Send to Core
		
		/*method.beginControlFlow("switch(dataObjectClass.getCanonicalName())");
		for(DataObjectInfo dataObject : dataObjects.getParsedDataObjects().values()) {
			method.addCode("case $S: \n", dataObject.getName());
			method.addStatement("return new $T()", ClassName.bestGuess(dataObject.getName() + ApiStrings.DATAOBJECTSUFFIX));
		}
		method.endControlFlow();
		
		method.addStatement("return null");*/
		return methodBuilder.build();
	}
}
