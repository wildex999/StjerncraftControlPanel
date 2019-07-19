package com.stjerncraft.controlpanel.webview;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.stjerncraft.controlpanel.api.client.ICallMethodReturnHandler;
import com.stjerncraft.controlpanel.client.api.GlobalClientCore;
import com.stjerncraft.controlpanel.client.api.IClientCoreApi;
import com.stjerncraft.controlpanel.client.api.session.IClientSession;
import com.stjerncraft.controlpanel.client.api.session.ISessionListener;
import com.stjerncraft.controlpanel.common.Statics;
import com.stjerncraft.controlpanel.common.api.CoreApiClient;
import com.stjerncraft.controlpanel.common.data.AgentInfo;
import com.stjerncraft.controlpanel.common.data.ServiceApiInfo;
import com.stjerncraft.controlpanel.common.data.ServiceProviderInfo;

public class Main implements EntryPoint {
	static Logger logger = Logger.getLogger("WebView");
	IClientCoreApi clientCore;
	String vg;
	
	@Override
	public void onModuleLoad() {
		clientCore = GlobalClientCore.get();
		if(clientCore == null) {
			logger.severe("No Client Core found");
			return;
		}
		logger.info("Found Client Core: " + clientCore);
		vg = "loL";
		
		ServiceApiInfo coreApi = new ServiceApiInfo(CoreApiClient.getApiName(), CoreApiClient.getApiVersion());
		ServiceProviderInfo coreServiceProvider = new ServiceProviderInfo(Statics.CORE_PROVIDER_UUID, Statics.CORE_AGENT_UUID, null);
		clientCore.startSession(coreApi, coreServiceProvider, new ISessionListener() {
			
			@Override
			public void onStarted(IClientSession session) {
				logger.info("Got Session Accepted 2");
				logger.info("Test: " + session.getCurrentState());
				logger.info("Test2: " + vg);
				
				CoreApiClient client = new CoreApiClient(clientCore, session.getSessionId());
				client.getAgents(agents -> {
						logger.info("Got Agents 2:" + agents.length + " | " + agents[0].name);
				}); 
				
				logger.info("Test3");
			}
			
			@Override
			public void onRejected(IClientSession session) {
				logger.info("Rejected 2");
			}
			
			@Override
			public void onEnded(IClientSession session) {
				logger.info("Ended 2");
			}
		});
		
	}

}
