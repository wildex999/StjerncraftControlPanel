package com.stjerncraft.controlpanel.api.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;

import com.stjerncraft.controlpanel.api.annotation.DataObject;

class DataObjectProcessor {
	
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
		for(TypeElement el : getDataObjects(env))
			dataObjects.put(el.getQualifiedName().toString(), null);
	}
	
	public void parseDataObjects(RoundEnvironment env) {
		//Do a pre-parse so they can reference each other
		preParseDataObjects(env);
		
		for(TypeElement el : getDataObjects(env)) {
			checkValidDataObject(el);
			
			DataObjectInfo dataObject = dataObjects.get(el.getQualifiedName().toString());
			if(dataObject == null) {
				String name = el.getQualifiedName().toString();
				dataObject = new DataObjectInfo(name);
				dataObjects.put(name, dataObject);
			}
			
			//Parse DataObject members
			for(Element member : procEnv.getElementUtils().getAllMembers(el)) {
				if(member.getKind() != ElementKind.FIELD || !member.getModifiers().contains(Modifier.PUBLIC))
					continue;			
				
				parseDataObjectField(dataObject, member);
			}
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
		obj.addField(new Field(field.getSimpleName().toString(), FieldCheck.isArray(fieldType), FieldCheck.getActualFieldType(fieldType)));
	}
	
	/**
	 * Check that the given DataObject is valid, and follows all the rules.
	 * @param dataObjectElement
	 */
	public void checkValidDataObject(TypeElement element) {
		
		//Must contain a public default constructor
		boolean hasPublicConstructor = false;
		for(Element member : procEnv.getElementUtils().getAllMembers(element)) {
			if(member.getKind() == ElementKind.CONSTRUCTOR && member.getModifiers().contains(Modifier.PUBLIC)) {
				ExecutableType method = (ExecutableType)member.asType();
				
				if(method.getParameterTypes().size() > 0)
					continue;
				
				hasPublicConstructor = true;
				break;
			}
		}
		if(!hasPublicConstructor)
			throw new IllegalArgumentException("[" + element.getQualifiedName() + "] @DataObject must contain a public default constructor!");
		
		//Can't be abstract
		if(element.getModifiers().contains(Modifier.ABSTRACT))
			throw new IllegalArgumentException("[" + element.getQualifiedName() + "] @DataObject can not be abstract!");
		
		//Can't be generic
		if(!element.getTypeParameters().isEmpty())
			throw new IllegalArgumentException("[" + element.getQualifiedName() + "] @DataClass can not be generic!");
	}

	
	protected static Set<TypeElement> getDataObjects(RoundEnvironment env) {
		Set<TypeElement> elements = new HashSet<>();
		for(Element el : env.getElementsAnnotatedWith(DataObject.class)) {
			if(el.getKind() != ElementKind.CLASS)
				throw new IllegalArgumentException("[" + el.getSimpleName() + "] The annotation " + DataObject.class.getCanonicalName() + " can only be placed on a class! ");
			elements.add((TypeElement)el);
		}
		
		return elements;
	}

}
