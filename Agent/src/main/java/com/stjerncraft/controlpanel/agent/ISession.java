package com.stjerncraft.controlpanel.agent;

import java.util.function.Consumer;

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
	
	public IRemoteClient getClient();
	public ServiceApi getServiceApi();
	public ServiceProvider<? extends ServiceApi> getServiceProvider();
	
	/**
	 * Call a method on the Service Provider, where the call has already been serialized to JSON
	 * @param serializedCall The serialized JSON, containing the method name and arguments.
	 * @param returnCallback Callback for the method return data. Can be null to ignore the return value.
	 * 	  For methods with Void return, the callback will still be called once the method has been called on the Service Provider.
	 */
	public void callMethod(String methodJson, Consumer<String> returnCallback);
	
	/**
	 * End the current session.
	 * Any outstanding calls might or might not still complete.
	 * Note: No registered return callbacks will be valid after this.
	 * @param reason Reason for ending the session. This can be logged or displayed somewhere depending on the situation.
	 */
	public void endSession(String reason);
}