package com.stjerncraft.controlpanel.api;

/**
 * The generated class for the Service API will implement this interface.
 * This will define the interaction surface between the Agent and the Service Provider implementation.
 */
public interface IServiceApiGenerated {
	public int getSerializerVersion();
	public int getApiVersion();
	public String getApiName();
	
	/**
	 * Call the given method with parameters on the service provider.
	 * @param serviceProvider Which Service Provider to make the method call on
	 * @param methodJson Method and parameters serialized as JSON.
	 * @return JSON Serialized return value.
	 */
	public String callMethod(IServiceProvider serviceProvider, String methodJson);
	
	/**
	 * Call the given event handler for Subscribing to an event.
	 * The context stored on the ServiceManager defines whether it's a subscribe or unsubscribe call.
	 * @param serviceProvider Service Provider to call the event handler on.
	 * @param eventMethodJson Method name and parameters serialized as JSON.
	 * @return An IUnsibscibeHandler, or null if the subscription was rejected.
	 */
	public IUnsubscribeHandler callEventHandler(IServiceProvider serviceProvider, String eventMethodJson, IEventSubscription subscription);
}
