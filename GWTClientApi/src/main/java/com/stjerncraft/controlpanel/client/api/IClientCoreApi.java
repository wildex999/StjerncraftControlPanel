package com.stjerncraft.controlpanel.client.api;

import com.stjerncraft.controlpanel.api.client.IClientApiLibrary;
import com.stjerncraft.controlpanel.api.client.IClientCore;
import com.stjerncraft.controlpanel.api.client.IServiceApiInfo;
import com.stjerncraft.controlpanel.api.client.IServiceProviderInfo;
import com.stjerncraft.controlpanel.client.api.session.IClientSession;
import com.stjerncraft.controlpanel.client.api.session.ISessionListener;

import jsinterop.annotations.JsType;

@JsType(isNative=true)
public interface IClientCoreApi extends IClientCore {	
	/**
	 * Start a Session with the provided API and Service Provider, without providing a ClientApiLibrary.
	 * @param api
	 * @param serviceProvider
	 * @param listener
	 * @return
	 */
	IClientSession startSessionRaw(IServiceApiInfo api, IServiceProviderInfo serviceProvider, ISessionListener listener);
	
	/**
	 * Start a Session using the given API on the specified Service Provider
	 * @param <T> An instance of the generated ClientApiLibrary for the ServiceApi to use.
	 * @param apiLibrary
	 * @param serviceProvider
	 * @param listener
	 * @return
	 */
	<T extends IClientApiLibrary> IClientSession startSessionSpecific(T apiLibrary, IServiceProviderInfo serviceProvider, ISessionListener listener);
	
	/**
	 * Start a session with the highest priority ServiceProvider supporting the given API.
	 * If no ServiceProvider is found, null is returned instead of a session.
	 * @param <T> An instance of the generated ClientApiLibrary for the ServiceApi to use.
	 * @param api
	 * @param listener
	 * @return Null if no valid ServiceProvider is found.
	 */
	<T extends IClientApiLibrary> IClientSession startSession(T apiLibrary, ISessionListener listener);
	
	/**
	 * End the Session, stopping any active Subscriptions.
	 * Any pending CallMethod returns are ignored.
	 */
	void endSession(IClientSession session);
	
	IClientModuleManager getModuleManager();
	//IClientServiceManager getServiceManager();
}
