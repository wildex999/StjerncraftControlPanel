package com.stjerncraft.controlpanel.agent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.stjerncraft.controlpanel.api.IEventSubscription;
import com.stjerncraft.controlpanel.common.ServiceApi;

/**
 * A session between a Client and a Service Provider.
 * Only the Client and Agent is aware of the session, which is used to keep track of method calls, events, and other data.
 */
public interface ISession {
	/**
	 * Get the ID of the session.
	 * This is an unique ID provided by the remote Agent when starting the session.
	 * @return
	 */
	public int getSessionId();
	
	public IRemoteClient getRemoteClient();
	public ServiceApi getServiceApi();
	public ServiceProvider<? extends ServiceApi> getServiceProvider();
	
	public boolean hasStarted();
	
	public void addListener(ISessionListener listener);
	public void removeListener(ISessionListener listener);
	
	/**
	 * Call a method on the Service Provider, where the call has already been serialized to JSON
	 * @param serializedCall The serialized JSON, containing the method name and arguments.
	 * @param returnCallback Callback for the method return data. Can be null to ignore the return value.
	 * 	  For methods with Void return, the callback will still be called once the method has been called on the Service Provider.
	 *    Note: Callback must be called on the main thread, and does not need to be thread safe!
	 */
	public void callMethod(String methodJson, Consumer<String> returnCallback);
	
	/**
	 * Subscribe to an Event.
	 * @param methodJson Call to method marked as EventHandler.
	 * @param subscribeCallback Client Callback which is called with the Subscribe response.
	 * @param eventCallback Client Callback called whenever there is a new Event to send to the client, with the subscriptionId & serialized JSON value.
	 * @param unsubscribeCallback Client Callback which is called when ending the Subscription
	 *     Provides the event subscriptionId if subscription was accepted, null if not.
	 */
	public void callSubscribe(String methodJson, Consumer<Integer> subscribeCallback, BiConsumer<Integer, String> eventCallback, Consumer<Integer> unsubscribeCallback);
	
	/**
	 * End an existing subscription to an Event.
	 * @param subscription The subscription to end.
	 */
	public void callUnsubscribe(IEventSubscription subscription);
	
	/**
	 * Called by the Agent when the session has started.
	 * Until this is called the session is not usable, but can still be ended.
	 */
	public void startSession();
	
	/**
	 * End the current session.
	 * Any outstanding calls might or might not still complete.
	 * Note: No registered return callbacks will be valid after this.
	 * @param reason Reason for ending the session. This can be logged or displayed somewhere depending on the situation.
	 */
	public void endSession(String reason);
}
