package com.stjerncraft.controlpanel.api.processor;

enum FieldType {
	Boolean("Boolean", Boolean.class.getCanonicalName(), "boolean"), //Name, class path, primitive name
	Byte("Byte", Byte.class.getCanonicalName(), "byte"),
	Character("Character", Character.class.getCanonicalName(), "char"),
	Short("Short", Short.class.getCanonicalName(), "short"),
	Integer("Integer", Integer.class.getCanonicalName(), "int"),
	Long("Long", Long.class.getCanonicalName(), "long"),
	Float("Float", Float.class.getCanonicalName(), "float"),
	Double("Double", Double.class.getCanonicalName(), "double"),
	String("String", String.class.getCanonicalName()),
	Void("Void", Void.class.getCanonicalName(), "void");
	
	
	public String name;
	public String[] classPaths;
	
	FieldType(String name, String... classPaths) {
		this.name = name;
		this.classPaths = classPaths;
		
	}
	
	/**
	 * Get the type with the given classPath.
	 * @param classPath Full qualified name of the type to check for
	 * @return Null if no valid type with the given classPath exists.
	 */
	public static FieldType getType(String classPath) {
		for(FieldType type : values()) {
			for(String cp : type.classPaths) {
				if(cp.equals(classPath))
					return type;
			}
		}
		return null;
	}
}
