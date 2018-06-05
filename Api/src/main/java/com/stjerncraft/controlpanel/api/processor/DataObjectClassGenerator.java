package com.stjerncraft.controlpanel.api.processor;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import org.json.JSONArray;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.stjerncraft.controlpanel.api.IDataObjectGenerated;

/**
 * Generate the Data Object Class handler class.
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
		ClassName dataObjectClass = ClassName.bestGuess(dataObject.getName());
		MethodSpec parse = generateParseMethod(dataObject);
		MethodSpec serialize = generateSerializeMethod(dataObject);
		
		MethodSpec parseInstance = MethodSpec.methodBuilder("parse")
				.addModifiers(Modifier.PUBLIC)
				.returns(dataObjectClass)
				.addParameter(JSONArray.class, "jsonObj")
				.addStatement("return parseJson(jsonObj)")
				.build();
		MethodSpec serializeInstance = MethodSpec.methodBuilder("serialize")
				.addModifiers(Modifier.PUBLIC)
				.returns(JSONArray.class)
				.addParameter(dataObjectClass, "obj")
				.addStatement("return serializeObject(obj)")
				.build();
		MethodSpec create = MethodSpec.methodBuilder("create")
				.addModifiers(Modifier.PUBLIC)
				.returns(dataObjectClass)
				.addStatement("return createInstance()")
				.build();
		
		MethodSpec createInstance = generateCreateInstanceMethod(dataObject);
		
		ParameterizedTypeName interfaceTyped = ParameterizedTypeName.get(ClassName.get(IDataObjectGenerated.class), ClassName.bestGuess(className));
		TypeSpec generatedClass = TypeSpec.classBuilder(className + ApiStrings.DATAOBJECTSUFFIX)
			.addModifiers(Modifier.PUBLIC)
			.addSuperinterface(interfaceTyped)
			.addMethod(parse)
			.addMethod(parseInstance)
			.addMethod(serialize)
			.addMethod(serializeInstance)
			.addMethod(createInstance)
			.addMethod(create)
			.build();

		JavaFile javaFile = JavaFile.builder(packageName, generatedClass).build();
		javaFile.writeTo(filer);
	}
	
	/**
	 * Generate parse method. It will take in the JSON Data Object, instantiate it and fill it with the values as deserialized from the JSON.

	 * A Data Object is a JSONArray of object values in the same order as defined in the Data Object class:
	 * ["strValue", 10, ["objValue", "strField", 1, 88.5], [1, 2, 3]]
	 */
	MethodSpec generateParseMethod(DataObjectInfo dataObject) {
		Builder method = MethodSpec.methodBuilder("parseJson")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(ClassName.bestGuess(dataObject.getName()))
				.addParameter(JSONArray.class, "jsonObj");
		
		ClassName name = ClassName.bestGuess(dataObject.getName());
		method.addStatement("$T inst = createInstance()", name);
		method.addStatement("if(jsonObj.length() == 0) return null");
		
		//Parse
		String[] varNames = Parse.parseVariables(dataObject.fields, "jsonObj", method, dataObjects, "");
		assert varNames.length == dataObject.fields.size();
		
		//Assign
		for(int i = 0; i < dataObject.fields.size(); i++) {
			Field var = dataObject.fields.get(i);
			method.addStatement("inst.$L = $L", var.name, varNames[i]);
		}
		
		method.addStatement("return inst");
		return method.build();
	}
	
	MethodSpec generateSerializeMethod(DataObjectInfo dataObject) {
		Builder method = MethodSpec.methodBuilder("serializeObject")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(JSONArray.class)
				.addParameter(ClassName.bestGuess(dataObject.getName()), "obj");
		
		method.addStatement("$T json = new $T()", JSONArray.class, JSONArray.class);
		method.addStatement("if(obj == null) return json");
		
		//Serialize
		for(Field field : dataObject.fields)
			Serialize.serializeVariable(field.fieldType, "obj." + field.name, field.isArray, method, dataObjects, "json");
		
		method.addStatement("return json");
		return method.build();
	}
	
	MethodSpec generateCreateInstanceMethod(DataObjectInfo dataObject) {
		Builder method = MethodSpec.methodBuilder("createInstance")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(ClassName.bestGuess(dataObject.getName()));

		method.addStatement("return new $T()", ClassName.bestGuess(dataObject.getName()));
		return method.build();
	}
}
