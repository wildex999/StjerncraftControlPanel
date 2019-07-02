package com.stjerncraft.controlpanel.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a Data Object.
 * Data Objects can be used in the Api to group data, and serialize objects.
 * 
 * 
 * - Only public fields are considered part of the data object.
 * - All public field types must either be a primitive(int, Integer, String etc.), an Enum, or another Data Object.
 *   Can also be 1D Arrays of the same types.
 * - Methods are ignored.
 * - Must contain a public default constructor(parameterless)..
 *   An inherited class(Not containing @DataObject) will be ignored if missing a default constructor.
 * - Can not use generics(For now).
 * - Abstract classes are ignored(But the annotation is inherited)
 * 
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@Inherited
public @interface DataObject {
	boolean inherit() default true;
}
