package com.stjerncraft.controlpanel.agent.local;

import java.util.UUID;

import com.stjerncraft.controlpanel.agent.ServiceApi;
import com.stjerncraft.controlpanel.api.IServiceApiGenerated;
import com.stjerncraft.controlpanel.api.IServiceProvider;

public class LocalServiceApi extends ServiceApi {

	protected Class<? extends IServiceProvider> apiClass;
	protected IServiceApiGenerated generatedApi;
	
	public LocalServiceApi(Class<? extends IServiceProvider> apiClass, IServiceApiGenerated generatedApi) {
		super(generatedApi.getApiName(), generatedApi.getApiVersion(), UUID.randomUUID().toString());
		this.apiClass = apiClass;
		this.generatedApi = generatedApi;
	}
	
	public Class<? extends IServiceProvider> getApiClass() {
		return apiClass;
	}
	
	public IServiceApiGenerated getGeneratedApi() {
		return generatedApi;
	}

}
