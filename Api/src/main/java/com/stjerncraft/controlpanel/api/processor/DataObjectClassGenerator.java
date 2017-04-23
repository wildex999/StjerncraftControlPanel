package com.stjerncraft.controlpanel.api.processor;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import org.json.JSONObject;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Generate the Data Object Class, which can serialize and deserialize it's content.
 * The generated class will handle the parsing and serialization of the Data Class.
 */

class DataObjectClassGenerator {
	DataObjectProcessor dataObjects;
	
	public DataObjectClassGenerator(DataObjectProcessor dataObjects) {
		this.dataObjects = dataObjects;
	}

	public void generateClassForDataObject(Filer filer, DataObjectInfo dataObject) throws IOException {
		String packageName = "";
		String className = dataObject.getName();
		int nameIndex = className.lastIndexOf(".");
		if(nameIndex != -1) {
			packageName = className.substring(0, nameIndex);
			className = className.substring(nameIndex+1);
		}
		
		//Generate Methods
		MethodSpec parse = MethodSpec.methodBuilder("parse")
			.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
			.returns(ClassName.bestGuess(dataObject.getName()))
			.addParameter(JSONObject.class, "jsonObj")
			//TODO PARSE
			.addStatement("return new $L()", dataObject.getName())
			.build();
		
		//TODO: Serialize method
		
		TypeSpec generatedClass = TypeSpec.classBuilder(className + ApiStrings.DATAOBJECTSUFFIX)
			.addModifiers(Modifier.PUBLIC)
			.addMethod(parse)
			.build();

		JavaFile javaFile = JavaFile.builder(packageName, generatedClass).build();
		javaFile.writeTo(filer);
	}
}
