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
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

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
		//For API:
		//- Generate js library
		//- Generate proxy API, which will be given per user and include things like user data:
		// So an user of a API would do ServiceManager.getApi(ApiInterface). This would return the generated class: ApiInterfaceProxy implements ApiInterface.
		// This proxy would simply relay the method calls, after setting the correct user: myMethod(int nr) { serviceManager.setUser(user); apiImplementation.myMethod(nr); }
		ServiceApiClassGenerator apiClassGenerator = new ServiceApiClassGenerator(dataObjectProc);
		try {
			for(ServiceApiInfo api : serviceApiProc.apis.values()) {
				if(generated.contains(api))
					continue;
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
