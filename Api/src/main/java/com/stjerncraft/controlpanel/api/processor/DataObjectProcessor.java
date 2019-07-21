package com.stjerncraft.controlpanel.api.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import com.stjerncraft.controlpanel.api.annotation.DataObject;

public class DataObjectProcessor {
	
	private Map<String, DataObjectInfo> dataObjects = new HashMap<>();
	private ProcessingEnvironment procEnv;
	
	public DataObjectProcessor(ProcessingEnvironment procEnv) {
		this.procEnv = procEnv;
	}
	
	public Map<String, DataObjectInfo> getParsedDataObjects() {
		return dataObjects;
	}
	
	/**
	 * Discover all DataObjects, so they are known when Type checking in other parts of the Processor.
	 * @param env
	 */
	protected void preParseDataObjects(RoundEnvironment env) {
		for(TypeElement el : getDataObjects(env, procEnv)) {
			String name = el.getQualifiedName().toString();
			dataObjects.put(name, new DataObjectInfo(name));
		}
	}
	
	public void parseDataObjects(RoundEnvironment env) {
		//Do a pre-parse so they can reference each other
		preParseDataObjects(env);
		
		for(TypeElement el : getDataObjects(env, procEnv)) {
			if(!checkValidDataObject(el)) {
				dataObjects.remove(el.getQualifiedName().toString());
				continue;
			}
			
			DataObjectInfo dataObject = dataObjects.get(el.getQualifiedName().toString());
			assert dataObject != null;
			
			//Parse DataObject members
			for(Element member : procEnv.getElementUtils().getAllMembers(el)) {
				if(member.getKind() != ElementKind.FIELD || !member.getModifiers().contains(Modifier.PUBLIC))
					continue;			
				
				parseDataObjectField(dataObject, member);
			}
			
			//Fields from a class is not guaranteed to be in any order, so we have to enforce the same order on both serialize and deserialize.
			dataObject.sortFieldsByName();
		}
	}
	
	/**
	 * Parse and check the DataObject field.
	 */
	public void parseDataObjectField(DataObjectInfo obj, Element field) {
		
		//Must be a valid type
		TypeMirror fieldType = field.asType();
		if(!FieldCheck.isValidType(dataObjects, fieldType))
			throw new IllegalArgumentException("[" + obj.getName() + "] " + field.getSimpleName() + " does not have a valid Type: " + fieldType);
		
		//Store the new field
		obj.addField(new Field(field.getSimpleName().toString(), FieldCheck.isArray(fieldType), FieldCheck.getActualFieldType(fieldType, dataObjects)));
	}
	
	/**
	 * Check that the given DataObject is valid, and follows all the rules.
	 * @param dataObjectElement
	 * @return True if the object is valid and to be kept, false if the object is valid but to be ignored
	 */
	public boolean checkValidDataObject(TypeElement element) {
		
		//Must contain a public default constructor
		boolean hasPublicConstructor = hasPublicDefaultConstructor(procEnv, element);
		
		if(!hasPublicConstructor)
			throw new IllegalArgumentException("[" + element.getQualifiedName() + "] @DataObject must contain a public default constructor!");
		
		//Can't be generic
		if(!element.getTypeParameters().isEmpty())
			throw new IllegalArgumentException("[" + element.getQualifiedName() + "] @DataClass can not be generic!");
		
		return true;
	}
	
	protected static boolean hasPublicDefaultConstructor(ProcessingEnvironment procEnv, TypeElement element) {
		for(Element member : procEnv.getElementUtils().getAllMembers(element)) {
			if(member.getKind() == ElementKind.CONSTRUCTOR && member.getModifiers().contains(Modifier.PUBLIC)) {
				ExecutableType method = (ExecutableType)member.asType();
				
				if(method.getParameterTypes().size() > 0)
					continue;
				
				return true;
			}
		}
		
		return false;
	}

	
	protected static Set<TypeElement> getDataObjects(RoundEnvironment env, ProcessingEnvironment procEnv) {
		Set<TypeElement> elements = new HashSet<>();
		for(Element el : env.getElementsAnnotatedWith(DataObject.class)) {
			if(el.getModifiers().contains(Modifier.ABSTRACT))
				continue; //Allow abstract classes to be used to inheritance of the annotation
			if(el.getKind() != ElementKind.CLASS)
				throw new IllegalArgumentException("[" + el.getSimpleName() + "] The annotation " + DataObject.class.getCanonicalName() + " can only be placed on a class or interface! ");
			
			TypeElement element = (TypeElement)el;
			boolean hasPublicConstructor = hasPublicDefaultConstructor(procEnv, element);
			
			//Get only directly defined annotations
			boolean directAnnotation = false;
			List<? extends AnnotationMirror> annList = element.getAnnotationMirrors();
			for(AnnotationMirror ann : annList) {
				String annName = ((TypeElement)ann.getAnnotationType().asElement()).getQualifiedName().toString();
				if(annName.equals(DataObject.class.getCanonicalName())) {
					directAnnotation = true;
					break;
				}
			}
			
			//Ignore inherited DataObjects if set to do so
			if(!directAnnotation && !el.getAnnotation(DataObject.class).inherit()) {
				procEnv.getMessager().printMessage(Kind.NOTE, "Skipping DataObject, inherit disabled: " + element.getQualifiedName().toString());
				continue; 
			}
			
			//Ignore inherited DataObjects without public default constructor
			if(!hasPublicConstructor && !directAnnotation) {
				procEnv.getMessager().printMessage(Kind.NOTE, "Skipping DataObject, missing default constructor: " + element.getQualifiedName().toString());
				continue; 
			}
			
			
			elements.add(element);
		}
		
		return elements;
	}

}
