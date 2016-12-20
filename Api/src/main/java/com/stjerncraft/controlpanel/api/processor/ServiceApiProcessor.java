package com.stjerncraft.controlpanel.api.processor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.api.annotation.ServiceApi;

class ServiceApiProcessor {
	
	Map<String, ServiceApiInfo> apis = new HashMap<>();
	
	public List<String> ignoreMethodsFrom = Arrays.asList(Object.class.getName(), IServiceProvider.class.getName()); 
	
	private ProcessingEnvironment procEnv;
	private DataObjectProcessor dataObjectProc;
	
	public ServiceApiProcessor(ProcessingEnvironment procEnv, DataObjectProcessor dataObjectProc) {
		this.procEnv = procEnv;
		this.dataObjectProc = dataObjectProc;
	}
	
	public void parseServiceApis(RoundEnvironment env) {
		for(TypeElement el : getServiceApis(env)) {
			checkValidServiceApi(el);
			
			String name = el.getQualifiedName().toString();
			ServiceApiInfo serviceApi = apis.get(name);
			if(serviceApi == null) {
				int version = el.getAnnotation(ServiceApi.class).version();
				serviceApi = new ServiceApiInfo(name, version);
				apis.put(name, serviceApi);
			}
			
			//Parse public methods
			for(Element member : procEnv.getElementUtils().getAllMembers(el)) {
				if(member.getKind() != ElementKind.METHOD)
					continue;
				
				parseMethod(serviceApi, member);
			}
			
		}
	}
	
	/**
	 * Check that the given interface is a valid Service API.
	 * @param element
	 */
	public void checkValidServiceApi(TypeElement element) {
		//Must be an interface
		if(element.getKind() != ElementKind.INTERFACE)
			throw new IllegalArgumentException("[" + element.getSimpleName() + "] The Service API must be an interface!");
		
		//Must implement ServiceProvider
		TypeMirror serviceProviderInterface = procEnv.getElementUtils().getTypeElement(IServiceProvider.class.getCanonicalName()).asType();
		if(!procEnv.getTypeUtils().isAssignable(element.asType(), serviceProviderInterface))
			throw new IllegalArgumentException("[" + element.getSimpleName() + "] Must implement " + IServiceProvider.class.getCanonicalName());
		
		//Can't be generic
		if(!element.getTypeParameters().isEmpty())
			throw new IllegalArgumentException("[" + element.getQualifiedName() + "] Service API interface can not be generic!");
	}
	
	/**
	 * Parse the given method, and add it to the Service API if it's valid.
	 * @param api
	 * @param element
	 */
	protected void parseMethod(ServiceApiInfo api, Element element) {
		if(!element.getModifiers().contains(Modifier.PUBLIC) || ignoreMethodsFrom.contains(element.getEnclosingElement().toString()))
			return;
		
		ExecutableElement method = (ExecutableElement)element;
		Method newMethod = new Method();
		
		//Return type
		TypeMirror returnType = method.getReturnType();
		if(!FieldCheck.isValidType(dataObjectProc.getParsedDataObjects(), returnType))
			throw new IllegalArgumentException("[" + api.getName() + "] Method return type " + returnType + " is invalid for " + element.getSimpleName() + " from " + element.getEnclosingElement());

		newMethod.setReturnType(FieldCheck.getActualFieldType(returnType), FieldCheck.isArray(returnType));
		
		//Parameters
		for(VariableElement par : method.getParameters()) {
			TypeMirror parType = par.asType();
			if(!FieldCheck.isValidType(dataObjectProc.getParsedDataObjects(), parType)) {
				throw new IllegalAccessError("[" + api.getName() + "] Method parameter " + par.getSimpleName() + " has invalid type " + par.asType() + 
												" for " + element.getSimpleName() + " from " + element.getEnclosingElement());
			}
			
			Field parField = new Field(par.getSimpleName().toString(), FieldCheck.isArray(parType), FieldCheck.getActualFieldType(parType));
			newMethod.addParameter(parField);
		}
		
		api.addMethod(newMethod);
	}

	
	protected static Set<TypeElement> getServiceApis(RoundEnvironment env) {
		Set<TypeElement> elements = new HashSet<>();
		for(Element el : env.getElementsAnnotatedWith(ServiceApi.class))
			elements.add((TypeElement)el);
		
		return elements;
	}
}
