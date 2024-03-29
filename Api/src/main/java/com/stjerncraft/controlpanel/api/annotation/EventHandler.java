package com.stjerncraft.controlpanel.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods tagged with this are considered Event Handlers.
 * Event Handlers are called by the client to register for an event.
 * Any arguments provided can be used to specify and filter the type of events to get, and the Event Handler
 * can also use these to decide whether to allow the client to receive specific events.

 * The method must have a return type of IUnsubscribeHandler, which is a handler that will be called when the Subscription is ended.
 * If the Subscribe request is not accepted, return null. 
 * 
 * When any EventHandler method is called, it is possible to get the IEventSubscription and EventAction from the IServiceManager.
 * 
 * Note: You can have multiple EventHandlers per API. 
 * It is also not necessary for them to really be events, you can also look at them as async methods.
 * For example, a single-shot async method could simply unsubscribe the client after providing the requested data.
 * 
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface EventHandler {
	/**
	 * The DataObject which is sent to the client for each event.
	 */
	Class<?> eventData();
}
