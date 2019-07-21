package com.stjerncraft.controlpanel.common.data;

import java.util.Arrays;
import java.util.Objects;

import com.stjerncraft.controlpanel.api.annotation.DataObject;
import com.stjerncraft.controlpanel.api.client.IServiceApiInfo;
import com.stjerncraft.controlpanel.api.client.IServiceProviderInfo;
import com.stjerncraft.controlpanel.api.client.ServiceProviderPriority;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType
@DataObject
public class ServiceProviderInfo implements IServiceProviderInfo {
	public String uuid; //The unique id of the Service Provider
	public String agentUuid;  //The ID of the agent on which this Service Provider exists
	public ServiceProviderPriority priority; //TODO: Use system like HIGHEST, HIGH, NORMAL, LOW, LOWEST
	public ServiceApiInfo[] apis; //APIs implemented by this Service Provider
	
	public ServiceProviderInfo(String uuid, String agentUuid, ServiceProviderPriority priority, ServiceApiInfo[] apis) {
		
		this.uuid = uuid;
		this.agentUuid = agentUuid;
		this.priority = priority;
		this.apis = apis;
	}
	
	@JsIgnore
	public ServiceProviderInfo() {
		this("", "", ServiceProviderPriority.NORMAL, new ServiceApiInfo[] {});
	}
	
	@Override
	public String getUuid() {
		return uuid;
	}
	
	@Override
	public String getAgentUuid() {
		return agentUuid;
	}
	
	@Override
	public ServiceProviderPriority getPriority() {
		return priority;
	}
	
	@Override
	public IServiceApiInfo[] getApis() {
		return apis;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		
		//JsInterop can't do instanceof check
		try {
			IServiceProviderInfo other = (IServiceProviderInfo)obj;
			
			if(!other.getUuid().equals(uuid))
				return false;
			
			if(!other.getAgentUuid().equals(agentUuid))
				return false;
			
			if(!Arrays.equals(other.getApis(), apis))
				return false;
		} catch(Exception e) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(uuid, agentUuid, apis);
	}
}
