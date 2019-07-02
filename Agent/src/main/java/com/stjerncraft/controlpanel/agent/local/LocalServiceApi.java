package com.stjerncraft.controlpanel.agent.local;

import com.stjerncraft.controlpanel.api.IServiceApiGenerated;
import com.stjerncraft.controlpanel.common.ServiceApi;

public class LocalServiceApi extends ServiceApi {

	protected Class<?> apiInterface;
	protected IServiceApiGenerated generatedApi;
	
	public LocalServiceApi(Class<?> apiInterface, IServiceApiGenerated generatedApi) {
		super(generatedApi.getApiName(), generatedApi.getApiVersion());
		this.apiInterface = apiInterface;
		this.generatedApi = generatedApi;
	}
	
	public Class<?> getApiInterface() {
		return apiInterface;
	}
	
	public IServiceApiGenerated getGeneratedApi() {
		return generatedApi;
	}

}
