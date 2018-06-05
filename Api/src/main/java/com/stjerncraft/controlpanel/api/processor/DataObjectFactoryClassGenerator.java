package com.stjerncraft.controlpanel.api.processor;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeSpec;
import com.stjerncraft.controlpanel.api.IDataObjectGenerated;

public class DataObjectFactoryClassGenerator {
	DataObjectProcessor dataObjects;
	
	public DataObjectFactoryClassGenerator(DataObjectProcessor dataObjects) {
		this.dataObjects = dataObjects;
	}

	/**
	 * Generate a Factory class for all the DataObjects currently registered in the DataObjectProcessor.
	 * @param filer
	 * @param factoryPath Path to class with DataObjectFactory annotation.
	 * @throws IOException
	 */
	public void generateFactoryClass(Filer filer, String factoryPath) throws IOException {
		String packageName = "";
		String className = factoryPath;
		int nameIndex = className.lastIndexOf(".");
		if(nameIndex != -1) {
			packageName = className.substring(0, nameIndex);
			className = className.substring(nameIndex+1);
		}
		
		TypeSpec generatedClass = TypeSpec.classBuilder(className + ApiStrings.DATAOBJECTFACTORYSUFFIX)
				.addModifiers(Modifier.PUBLIC)
				.addMethod(generateGetGeneratorMethod())
				.build();

			JavaFile javaFile = JavaFile.builder(packageName, generatedClass).build();
			javaFile.writeTo(filer);
	}
	
	/**
	 * Generate a method which will return the Generated class for the given DataObject class
	 * @return
	 */
	MethodSpec generateGetGeneratorMethod() {
		Builder method = MethodSpec.methodBuilder("getGenerator")
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
		return method.build();
	}
	
	/**
	 * Generate a method which will create an instance of the given DataObject class
	 * @return
	 */
	MethodSpec generateCreateInstanceMethod() {
		return null;
	}
	
	/**
	 * Generate a method which will serialize the given DataObject instance and return it as JSON.
	 * @return
	 */
	MethodSpec generateSerializeMethod() {
		return null;
	}
	
	/**
	 * Generate a method which will parse the given JSONArray, and return an instance of the DataObject.
	 * @return
	 */
	MethodSpec generateParseMethod() {
		return null;
	}
	
}
