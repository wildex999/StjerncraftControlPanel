package com.stjerncraft.controlpanel.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a interface as a Service API.
 * The API name will become the fully qualified path to the interface with this annotation.
 * 
 * - Must be an interface.
 * - Must extend ServiceProvider.
 * - Can not be generic(For now).
 * - Only public methods are considered part of the API.
 * - Argument and return types must be either primitives(int, Double, String etc...) or a Data Object.
 *   Can also be a 1D array of the given types.
 *
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ServiceApi {
	int version();
}
