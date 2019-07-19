package com.stjerncraft.controlpanel.common.data;

import java.util.Arrays;

import com.stjerncraft.controlpanel.api.annotation.DataObject;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType
@DataObject
public class ServiceProviderInfo implements IServiceProviderInfo {
	private String uuid; //The unique id of the Service Provider
	private String agentUuid;  //The ID of the agent on which this Service Provider exists
	private ServiceApiInfo[] apis; //APIs implemented by this Service Provider
	
	public ServiceProviderInfo(String uuid, String agentUuid, ServiceApiInfo[] apis) {
		
		this.uuid = uuid;
		this.agentUuid = agentUuid;
		this.apis = apis;
	}
	
	@JsIgnore
	public ServiceProviderInfo() {
		this("", "", new ServiceApiInfo[] {});
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
	public ServiceApiInfo[] getApis() {
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
}
