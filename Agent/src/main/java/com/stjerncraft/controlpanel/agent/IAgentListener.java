package com.stjerncraft.controlpanel.agent;

import com.stjerncraft.controlpanel.common.ServiceApi;
import com.stjerncraft.controlpanel.common.exceptions.InvalidUUIDException;

public interface IAgentListener {
	void onApiAdded(ServiceApi api) throws InvalidUUIDException;
	void onApiRemoved(ServiceApi api);
	void onProviderAdded(ServiceProvider<? extends ServiceApi> provider) throws InvalidUUIDException;
	void onProviderRemoved(ServiceProvider<? extends ServiceApi> provider);
}
