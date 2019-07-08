package com.stjerncraft.controlpanel.api.processor;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

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
		String packageName = api.getName();
		String className = "Client";
		
		TypeSpec.Builder builder = TypeSpec.classBuilder(className)
				.addModifiers(Modifier.PUBLIC);
		
		Collection<Method> methods = api.getMethods();
		for(Method method : methods) {
			MethodSpec generatedMethod = generateMethod(method);
			builder.addMethod(generatedMethod);
		}
		 
		TypeSpec generatedClass = builder.build();
		JavaFile javaFile = JavaFile.builder(packageName, generatedClass).build();
		javaFile.writeTo(filer);
	}
	
	/**
	 * Generate a method which will return the Generated class for the given DataObject class
	 * @return
	 */
	MethodSpec generateMethod(Method method) {
		/*Builder method = MethodSpec.methodBuilder("getGenerator")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(ClassName.get(IDataObjectGenerated.class))
				.addParameter(Class.class, "dataObjectClass");

		method.beginControlFlow("switch(dataObjectClass.getCanonicalName())");
		for(DataObjectInfo dataObject : dataObjects.getParsedDataObjects().values()) {
			method.addCode("case $S: \n", dataObject.getName());
			method.addStatement("return new $T()", ClassName.bestGuess(dataObject.getName() + ApiStrings.DATAOBJECTSUFFIX));
		}
		method.endControlFlow();
		
		method.addStatement("return null");
		return method.build();*/
		return null;
	}
}
