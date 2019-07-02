package com.stjerncraft.controlpanel.common.data;

import com.stjerncraft.controlpanel.api.annotation.DataObject;

@DataObject
public class ServiceProviderInfo {
	public String uuid; //The unique id of the Service Provider
	public String agentUuid;  //The ID of the agent on which this Service Provider exists
	public ServiceApiInfo[] apis; //APIs implemented by this Service Provider
	
	public ServiceProviderInfo(String uuid, String agentUuid, ServiceApiInfo[] apis) {
		this.uuid = uuid;
		this.agentUuid = agentUuid;
		this.apis = apis;
	}
	
	public ServiceProviderInfo() {}
}
