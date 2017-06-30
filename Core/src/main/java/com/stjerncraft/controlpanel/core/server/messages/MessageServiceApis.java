package com.stjerncraft.controlpanel.core.server.messages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.websocket.api.Session;

import com.stjerncraft.controlpanel.core.api.ServiceApi;
import com.stjerncraft.controlpanel.core.server.HTTPServer;
import com.stjerncraft.controlpanel.core.server.message.IMessage;

/**
 * Message containing a list of Service Api's
 */
public class MessageServiceApis implements IMessage {
	class ServiceApiInfo {
		public String name;
		public int version;
	}
	
	List<ServiceApiInfo> apiList;
	
	//Receive constructor
	public MessageServiceApis() {}
	
	//Send constructor
	public MessageServiceApis(List<ServiceApi> apiList) {
		this.apiList = new ArrayList<>();
		for(ServiceApi api : apiList) {
			ServiceApiInfo apiInfo = new ServiceApiInfo();
			apiInfo.name = api.getName();
			apiInfo.version = api.getVersion();
			this.apiList.add(apiInfo);
		}
	}
	
	@Override
	public String getMessageId() {
		return "ServiceApis";
	}

	@Override
	public void handle(Session wsSession, HTTPServer server) {
		//Data message, no handling
	}

}
