package com.stjerncraft.controlpanel.core.service;

import com.stjerncraft.controlpanel.api.IServiceApiGenerated;
import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.core.api.ServiceApi;

public class LocalServiceApi extends ServiceApi {

	protected Class<? extends IServiceProvider> apiClass;
	protected IServiceApiGenerated generatedApi;
	
	public LocalServiceApi(Class<? extends IServiceProvider> apiClass, IServiceApiGenerated generatedApi) {
		super(generatedApi.getApiName(), generatedApi.getApiVersion());
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
