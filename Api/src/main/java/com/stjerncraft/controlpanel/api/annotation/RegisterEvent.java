package com.stjerncraft.controlpanel.api.annotation;

/**
 * Events are sent out to all registered listeners
 *
 * Use on methods which will be called every time a client register to the given event.
 */

public @interface RegisterEvent {
	Class<?> eventType();
}
