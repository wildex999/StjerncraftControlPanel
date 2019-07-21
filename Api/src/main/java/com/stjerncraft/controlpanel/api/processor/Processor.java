package com.stjerncraft.controlpanel.api.processor;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import com.stjerncraft.controlpanel.api.annotation.DataObject;
import com.stjerncraft.controlpanel.api.annotation.DataObjectFactory;
import com.stjerncraft.controlpanel.api.annotation.EventHandler;
import com.stjerncraft.controlpanel.api.annotation.ServiceApi;

/**
 * Detect Service API's, and generate supporting code.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Processor extends AbstractProcessor {

	protected DataObjectProcessor dataObjectProc;
	protected ServiceApiProcessor serviceApiProc;
	
	protected String processorName = "Core";
	
	//Keep track of classes we have already generated
	protected Set<Object> generated = new HashSet<>();
	
	public static Messager msg;
	
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return new HashSet<String>(Arrays.asList(
			ServiceApi.class.getCanonicalName(),
			DataObject.class.getCanonicalName(),
			EventHandler.class.getCanonicalName()
		));
	}
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		
		msg = processingEnv.getMessager();
		dataObjectProc = new DataObjectProcessor(processingEnv);
		serviceApiProc = new ServiceApiProcessor(processingEnv, dataObjectProc);
	}
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
		msg.printMessage(Kind.NOTE, "Running " + processorName + " Processor...");
		
		if(!parseDataObjects(env))
			return true;
		
		if(!parseApi(env))
			return true;
		
		//Generate Source files
		if(!generateDataObjects(env))
			return true;
		if(!generateApis(env))
			return true;
		
		return true;
	}
	
	protected boolean parseDataObjects(RoundEnvironment env) {
		try {
			dataObjectProc.parseDataObjects(env);
		} catch(IllegalArgumentException e) {
			msg.printMessage(Kind.ERROR, "Failed to parse DataObjects: " + e.getMessage(), null);
			return false;
		}
		
		return true;
	}
	
	protected boolean parseApi(RoundEnvironment env) {
		try {
			serviceApiProc.parseServiceApis(env);
		} catch(IllegalArgumentException e) {
			msg.printMessage(Kind.ERROR, "Failed to parse Service APIs: " + e.getMessage(), null);
			return false;
		}
		
		return true;
	}
	
	protected boolean generateDataObjects(RoundEnvironment env) {
		//For DataObjects:
		//- Generate class for parsing and serializing DataObjects
		DataObjectClassGenerator dataObjectClassGenerator = new DataObjectClassGenerator(dataObjectProc);
		try {
			for(DataObjectInfo dataObj : dataObjectProc.getParsedDataObjects().values()) {
				if(generated.contains(dataObj))
					continue;
				dataObjectClassGenerator.generateClassForDataObject(processingEnv.getFiler(), dataObj);
				msg.printMessage(Kind.NOTE, "Generated DataObject: " + dataObj.getName());
				generated.add(dataObj);
			}
		} catch(IOException e) {
			msg.printMessage(Kind.ERROR, "Failed to write DataObject Class: " + e.getMessage());
		}
		
		//Generators for DataObjects generated by this API
		DataObjectFactoryClassGenerator dataObjectFactoryGenerator = new DataObjectFactoryClassGenerator(dataObjectProc);
		for(Element el : env.getElementsAnnotatedWith(DataObjectFactory.class)) {
			if(el.getKind() != ElementKind.CLASS && el.getKind() != ElementKind.INTERFACE)
				throw new IllegalArgumentException("[" + el.getSimpleName() + "] The annotation " + DataObjectFactory.class.getCanonicalName() + " can only be placed on a class or interface! ");
			try {
				dataObjectFactoryGenerator.generateFactoryClass(processingEnv.getFiler(), ((TypeElement)el).getQualifiedName().toString());
				msg.printMessage(Kind.NOTE, "Generated DataObjectFactory at " + ((TypeElement)el).getQualifiedName().toString());
			} catch (IOException e) {
				msg.printMessage(Kind.ERROR, "Failed to write DataObjectFactory Class: " + e.getMessage());
			}
		}
		
		return true;
	}
	
	protected boolean generateApis(RoundEnvironment env) {
		//For API:
		//
		//- Generate Java Client API for Async Method calls towards the API.
		// This library is just a wrapper which will proxy the call to a Client/Module Core, and give the return value to a callback
		//
		//- TODO: Generate js/ts library for users not using Java
		//
		//- Generate the Server/Agent side API class, which does the actual calls on the Service Provider.
		// This will take in a JSON Method call and deserialize it, perform the actual call, and then serialize the return value.
		//
		//- TODO: Generate proxy API, which will be given per user and include things like user data:
		// So an user of a API would do ServiceManager.getApi(ApiInterface). This would return the generated class: ApiInterfaceProxy implements ApiInterface.
		// This proxy would simply relay the method calls, after setting the correct user: myMethod(int nr) { serviceManager.setUser(user); apiImplementation.myMethod(nr); }
		// Note: Do we need this? ServiceManager will set User before remote calls. All local calls be considered User = System unless explicitly set?
		
		ServiceApiClassGenerator apiClassGenerator = new ServiceApiClassGenerator(dataObjectProc);
		ClientApiLibraryGenerator clientLibraryGenerator = new ClientApiLibraryGenerator(dataObjectProc);
		try {
			for(ServiceApiInfo api : serviceApiProc.apis.values()) {
				if(generated.contains(api))
					continue;
				
				//Generate the Client Library
				clientLibraryGenerator.generateClientLibrary(processingEnv.getFiler(), api);
				
				//TODO: Generate JS/TS Client Library, which will proxy calls through the Client/Module Core.
				
				//Generate the Service API class, which will do the actual translation from JSON to a Method call and back.
				apiClassGenerator.generateClassForApi(processingEnv.getFiler(), api);
				
				msg.printMessage(Kind.NOTE, "Generated Service API: " + api.getName());
				generated.add(api);
			}
		} catch (IOException e) {
			msg.printMessage(Kind.ERROR, "Failed to write API Class: " + e.getMessage());
		}
		
		return true;
	}
}
