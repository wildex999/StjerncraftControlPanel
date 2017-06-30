package com.stjerncraft.controlpanel.core.service;

import java.util.List;

import com.stjerncraft.controlpanel.api.EventAction;
import com.stjerncraft.controlpanel.api.IClient;
import com.stjerncraft.controlpanel.api.IEventSubscription;
import com.stjerncraft.controlpanel.api.IServiceManager;
import com.stjerncraft.controlpanel.api.IServiceProvider;

/**
 * Service Manager for local Service Providers
 */

public class LocalServiceManager implements IServiceManager {

	@Override
	public IClient getClient() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IEventSubscription getEventSubscription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventAction getEventAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends IServiceProvider> T getService(Class<T> apiInterfaceClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends IServiceProvider> List<T> getServices(Class<T> apiInterfaceClass) {
		// TODO Auto-generated method stub
		return null;
	}

}
