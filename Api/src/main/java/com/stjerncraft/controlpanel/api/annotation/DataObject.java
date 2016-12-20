package com.stjerncraft.controlpanel.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a Data Object.
 * Data Objects can be used in the Api to group data.
 * 
 * 
 * - Only public fields are considered part of the data object.
 * - All public field types must either be a primitive(int, Integer, String etc.) or another Data Object.
 *   Can also be 1D Arrays of the same types.
 * - Methods are ignored.
 * - Must contain a public default constructor(parameterless).
 * - Can not be abstract.
 * - Can not use generics.
 * 
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface DataObject {

	
}
