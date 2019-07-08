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
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.api.annotation.EventHandler;
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
	
	/**
	 * Find and parse all Service APIs, adding them to the apis map.
	 * @param env
	 */
	public void parseServiceApis(RoundEnvironment env) {
		for(TypeElement el : getServiceApis(env)) {
			checkValidServiceApi(el);
			
			if(el.getEnclosingElement() != null && el.getEnclosingElement().getKind() != ElementKind.PACKAGE)
				throw new IllegalArgumentException("[" + el.getQualifiedName() + "] The Service API must be a root element inside a package, not an inner element!");
			
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
				
				Method newMethod = parseMethod(serviceApi, member);
				if(newMethod != null)
					serviceApi.addMethod(newMethod);
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
		
		//Can't be generic
		if(!element.getTypeParameters().isEmpty())
			throw new IllegalArgumentException("[" + element.getQualifiedName() + "] Service API interface can not be generic!");
	}
	
	/**
	 * Parse the given method, and add it to the Service API if it's valid.
	 * @param api
	 * @param element
	 */
	protected Method parseMethod(ServiceApiInfo api, Element element) {
		if(!element.getModifiers().contains(Modifier.PUBLIC) || ignoreMethodsFrom.contains(element.getEnclosingElement().toString()))
			return null;
		
		ExecutableElement method = (ExecutableElement)element;
		
		//TODO: Compress the name somehow? Sort the methods by their signature name so we can append a number instead?)
		Method newMethod = new Method(getMethodSignature(method), method.getSimpleName().toString());
		//procEnv.getMessager().printMessage(Kind.WARNING, "TEST: " + newMethod);
		
		//Return type
		TypeMirror returnType = method.getReturnType();
		FieldType returnFieldType = FieldCheck.getActualFieldType(returnType, dataObjectProc.getParsedDataObjects());
		if(!FieldCheck.isValidType(dataObjectProc.getParsedDataObjects(), returnType) || returnFieldType == null)
			throw new IllegalArgumentException("[" + api.getName() + "] Method return type " + returnType + " is invalid for " + newMethod.name + " from " + element.getEnclosingElement());

		newMethod.setReturnType(returnFieldType, FieldCheck.isArray(returnType));
		
		//Parameters
		for(VariableElement par : method.getParameters()) {
			TypeMirror parType = par.asType();
			FieldType parFieldType = FieldCheck.getActualFieldType(parType, dataObjectProc.getParsedDataObjects());
			if(!FieldCheck.isValidType(dataObjectProc.getParsedDataObjects(), parType) || parFieldType == null || parFieldType == BaseType.Void.type) {
				throw new IllegalArgumentException("[" + api.getName() + "] Method parameter " + par.getSimpleName() + " has invalid type " + par.asType() + 
												" for " + element.getSimpleName() + " from " + element.getEnclosingElement());
			}
			Field parField = new Field(par.getSimpleName().toString(), FieldCheck.isArray(parType), parFieldType);
			newMethod.addParameter(parField);
		}
		
		//Special handling for Event Handler Methods
		EventHandler eventHandler = element.getAnnotation(EventHandler.class);
		if(eventHandler != null)
		{
			newMethod.isEventHandler = true;
			
			//A known Data type is required to generate the serialize & deserialize
			//Getting the class directly doesn't work, but the exception gives us the TypeMirror for the class.
			try { eventHandler.eventData(); } catch(MirroredTypeException e)
			{
				FieldType type = FieldCheck.getActualFieldType(e.getTypeMirror(), dataObjectProc.getParsedDataObjects());
				if(type == null)
					throw new IllegalArgumentException("[" + api.getName() + "] Unknown Data Type " + e.getTypeMirror() + " for Event Handler " + newMethod.name + " from " + element.getEnclosingElement());
				
				newMethod.eventDataType = type;
			}
			
			if(newMethod.eventDataType == null)
				throw new IllegalArgumentException("[" + api.getName() + "] Missing Data Type for Event Handler " + newMethod.name + " from " + element.getEnclosingElement());
			
			//Must return a boolean to indicate success/failure
			if(returnType.getKind() != TypeKind.BOOLEAN)
				throw new IllegalArgumentException("[" + api.getName() + "] Return type must be boolean for Event Handler " + newMethod.name + " from " + element.getEnclosingElement());
		}
		
		return newMethod;
	}
	
	/**
	 * Get the full signature name of the method(Excluding return type).
	 * @return
	 */
	protected static String getMethodSignature(ExecutableElement method) {
		String simpleName = method.getSimpleName().toString();
		StringBuilder sb = new StringBuilder(simpleName);
		
		
		List<? extends VariableElement> parameters = method.getParameters();
		if(parameters.size() > 0)
			sb.append("$");
		
		//getParameters always returns in declaration order, so this is safe
		for(VariableElement par : parameters) {
			String parName = par.asType().toString();
			switch(parName) {
			case "java.lang.String":
				parName = "Str";
				break;
			case "java.lang.Byte":
				parName = "Byt";
				break;
			case "java.lang.Integer":
				parName = "Int";
				break;
			case "java.lang.Float":
				parName = "Flo";
				break;
			case "java.lang.Double":
				parName = "Dou";
				break;
			case "java.lang.Boolean":
				parName = "Boo";
				break;
			case "byte":
				parName = "B";
				break;
			case "char":
				parName = "C";
				break;
			case "double":
				parName = "D";
				break;
			case "float":
				parName = "F";
				break;
			case "int":
				parName = "I";
				break;
			case "long":
				parName = "J";
				break;
			case "short":
				parName = "S";
				break;
			case "void":
				parName = "V";
				break;
			case "boolean":
				parName = "Z";
				break;
			}
			
			sb.append(parName.replaceAll("\\.", "_").replaceAll("\\[\\]", "A"));
			sb.append("$");
		}
		
		return sb.toString();
	}
	
	protected static Set<TypeElement> getServiceApis(RoundEnvironment env) {
		Set<TypeElement> elements = new HashSet<>();
		for(Element el : env.getElementsAnnotatedWith(ServiceApi.class))
			elements.add((TypeElement)el);
		
		return elements;
	}
}
