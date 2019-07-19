package com.stjerncraft.controlpanel.api.client;

import jsinterop.annotations.JsType;

/**
 * ClientCore takes care of sending method calls, and receiving responses, and handles Subscription Events.
 * 
 * It will take in Method JSON calls, and a Callback for the return value.
 * For Subscriptions, the Callback will be called multiple times.
 * 
 * The Client Library acts as a API Proxy for calls to the Client Core.
 */
@JsType(isNative=true)
public interface IClientCore {
	/**
	 * Send a Method Call JSON to the Service Provider.
	 * @param sessionId The ID Of the session with the Service Provider.
	 * @param jsonMethod A Serialized JSON object with the method call.
	 * @param returnCallback The callback which will be called with the return value. Must be null for void callback methods.
	 */
	int callMethod(int sessionId, String jsonMethod, ICallMethodReturnHandler<String> returnCallback);
	
	/**
	 * Subscribe to an Event on the Service Provider.
	 * @param sessionId The ID of the session with the Service Provider.
	 * @param JsonMethod A Serialized JSON object with the method call.
	 * @param handler The handler which will be called for the different events. The Value will always be the serialized JSON object. 
	 * @return A callId which will be provided on the OnSubscribed for this call. -1 Is returned if it fails to Subscribe at this point.
	 */
	int callSubscribe(int sessionId, String JsonMethod, IClientSubscriptionHandler<String> handler);
	
	/**
	 * Unsubscribe from the given Subscription.
	 * @param subscriptionId The ID of the Subscription
	 */
	void callUnsubscribe(int subscriptionId);
}
