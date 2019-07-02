package com.stjerncraft.controlpanel.common.data;

import com.stjerncraft.controlpanel.api.annotation.DataObject;

@DataObject
public class AgentInfo {
	public String uuid;
	public String name;
	public ServiceProviderInfo[] providers; //Providers registered with this Agent
	
	public AgentInfo(String uuid, String name, ServiceProviderInfo[] providers) {
		this.uuid = uuid;
		this.name = name;
		this.providers = providers;
	}
	
	public AgentInfo() {}
}
