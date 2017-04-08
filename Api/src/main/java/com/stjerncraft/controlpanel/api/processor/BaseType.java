package com.stjerncraft.controlpanel.api.processor;

import com.stjerncraft.controlpanel.api.processor.FieldType;

public enum BaseType {
	Boolean("Boolean", Boolean.class.getCanonicalName(), "boolean"), //Name, class path, primitive name
	Byte("Byte", Byte.class.getCanonicalName(), "byte"),
	Character("Character", Character.class.getCanonicalName(), "char"),
	Short("Short", Short.class.getCanonicalName(), "short"),
	Integer("Integer", Integer.class.getCanonicalName(), "int"),
	Long("Long", Long.class.getCanonicalName(), "long"),
	Float("Float", Float.class.getCanonicalName(), "float"),
	Double("Double", Double.class.getCanonicalName(), "double"),
	String("String", String.class.getCanonicalName(), "String"),
	Void("Void", Void.class.getCanonicalName(), "void");
	
	public FieldType type;
	
	BaseType(String name, String... classPaths) {
		type = new FieldType(name, classPaths);
	}
	
	/**
	 * Get the base type with the given classPath.
	 * @param classPath Full qualified name of the type to check for
	 * @return Null if no valid type with the given classPath exists.
	 */
	public static BaseType getType(String classPath) {
		for(BaseType type : values()) {
			for(String cp : type.type.classPaths) {
				if(cp.equals(classPath))
					return type;
			}
		}
		return null;
	}
}
