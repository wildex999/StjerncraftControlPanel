package com.stjerncraft.controlpanel.agent;

public interface IAgentListener {
	void onApiAdded(ServiceApi api) throws Exception;
	void onApiRemoved(ServiceApi api);
	void onProviderAdded(ServiceProvider<? extends ServiceApi> provider) throws Exception;
	void onProviderRemoved(ServiceProvider<? extends ServiceApi> provider);
}
