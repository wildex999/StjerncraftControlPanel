package com.stjerncraft.controlpanel.api.annotation;

/**
 * Methods tagged with this are considered Event Handlers.
 * Event Handlers are called by the client to either register or unregister for an event.
 * Any arguments provided can be used to specify and filter the type of events to get, and the Event Handler
 * can also use these to decide whether to allow the client to receive any events.
 * 
 * The method must have a boolean return type. Return 'true' to indicate the event subscription was accepted, 'false' to deny. 
 * The return value is ignored if the client is unsubscribing.
 * 
 * When any EventHandler method is called, it is possible to get IEventSubscription and EventAction from the IServiceManager.
 * 
 * Note: You can have multiple EventHandlers. 
 * It is also not necessary for them to really be event, you can also look at them as async methods.
 * For example, a single-shot async method could simply unsubscribe the client after providing the requested data.
 * 
 */

public @interface EventHandler {
	/**
	 * The type of the event data. See ServiceApi and DataObject for the rules on data types.
	 */
	Class<?> eventType();
}
