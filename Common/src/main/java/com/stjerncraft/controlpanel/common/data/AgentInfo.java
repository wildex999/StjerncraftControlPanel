package com.stjerncraft.controlpanel.common.data;

import com.stjerncraft.controlpanel.api.annotation.DataObject;
import com.stjerncraft.controlpanel.api.client.IServiceProviderInfo;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType
@DataObject
public class AgentInfo implements IAgentInfo {
	public String uuid;
	public String name;
	public ServiceProviderInfo[] providers; //Providers registered with this Agent
	
	public AgentInfo(String uuid, String name, ServiceProviderInfo[] providers) {
		this.uuid = uuid;
		this.name = name;
		this.providers = providers;
	}
	
	@JsIgnore
	public AgentInfo() {
		this("", "", new ServiceProviderInfo[] {});
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IServiceProviderInfo[] getProviders() {
		return providers;
	}
}
