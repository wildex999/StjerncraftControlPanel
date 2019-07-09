package com.stjerncraft.controlpanel.api;

import java.util.function.Consumer;

/**
 * ClientCore takes care of sending method calls, and receiving responses, and handles Subscription Events.
 * 
 * It will take in Method JSON calls, and a Callback for the return value.
 * For Subscriptions, the Callback will be called multiple times.
 * 
 * The Client Library acts as a API Proxy for calls to the Client Core.
 */
public interface IClientCore {
	void callMethod(int sessionId, String jsonMethod, Consumer<String> returnCallback);
	void callSubscribe(int sessionId, String JsonMethod, Consumer<String> eventCallback);
	void callUnsubscribe(int subscriptionId);
}
