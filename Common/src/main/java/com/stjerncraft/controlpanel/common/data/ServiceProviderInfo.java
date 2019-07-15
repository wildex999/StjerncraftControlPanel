package com.stjerncraft.controlpanel.common.data;

import java.util.Arrays;

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
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		
		if(!(obj instanceof ServiceProviderInfo))
			return false;
		
		ServiceProviderInfo other = (ServiceProviderInfo)obj;
		
		if(!other.uuid.equals(uuid))
			return false;
		
		if(!other.agentUuid.equals(agentUuid))
			return false;
		
		if(!Arrays.equals(other.apis, apis))
			return false;
		
		return true;
	}
}
