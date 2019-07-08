package com.stjerncraft.controlpanel.api.processor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import com.stjerncraft.controlpanel.api.annotation.DataObjectFactory;

/**
 * Detect Service API's, and generate supporting code.
 */

@SupportedAnnotationTypes({Processor.serviceApi, Processor.dataObject, Processor.registerEvent})@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class Processor extends AbstractProcessor {
	protected static final String serviceApi = "com.stjerncraft.controlpanel.api.annotation.ServiceApi";
	protected static final String dataObject = "com.stjerncraft.controlpanel.api.annotation.DataObject";
	protected static final String registerEvent = "com.stjerncraft.controlpanel.api.annotation.RegisterEvent";

	protected DataObjectProcessor dataObjectProc;
	protected ServiceApiProcessor serviceApiProc;
	
	//Keep track of classes we have already generated
	Set<Object> generated = new HashSet<>();
	
	public static Messager msg;
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		
		msg = processingEnv.getMessager();
		dataObjectProc = new DataObjectProcessor(processingEnv);
		serviceApiProc = new ServiceApiProcessor(processingEnv, dataObjectProc);
	}
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
		//Parse DataObjects
		try {
			dataObjectProc.parseDataObjects(env);
		} catch(IllegalArgumentException e) {
			msg.printMessage(Kind.ERROR, "Failed to parse DataObjects: " + e.getMessage(), null);
			return true;
		}
		
		//Parse API
		try {
			serviceApiProc.parseServiceApis(env);
			//Parse Events in API
		} catch(IllegalArgumentException e) {
			msg.printMessage(Kind.ERROR, "Failed to parse Service APIs: " + e.getMessage(), null);
			return true;
		}
			
		
		//Generate Source files
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
		
		//For API:
		//- Generate Java Client Library for Async Method calls towards the API.
		// This library is just a wrapper which will proxy the call to a Client/Module Core, and then return a Promise object for the reply.
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
		try {
			for(ServiceApiInfo api : serviceApiProc.apis.values()) {
				if(generated.contains(api))
					continue;
				
				//Generate the Client Library
				
				
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
