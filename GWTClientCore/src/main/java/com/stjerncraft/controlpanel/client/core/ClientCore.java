package com.stjerncraft.controlpanel.client.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.user.client.Timer;
import com.stjerncraft.controlpanel.api.client.ICallMethodReturnHandler;
import com.stjerncraft.controlpanel.api.client.IClientApiLibrary;
import com.stjerncraft.controlpanel.api.client.IClientSubscriptionHandler;
import com.stjerncraft.controlpanel.api.client.IServiceApiInfo;
import com.stjerncraft.controlpanel.api.client.IServiceProviderInfo;
import com.stjerncraft.controlpanel.api.client.ServiceProviderPriority;
import com.stjerncraft.controlpanel.client.api.GlobalClientCore;
import com.stjerncraft.controlpanel.client.api.IClientCoreApi;
import com.stjerncraft.controlpanel.common.api.CoreApiLibrary;
import com.stjerncraft.controlpanel.client.api.IClientModuleManager;
import com.stjerncraft.controlpanel.client.api.IClientServiceManager;
import com.stjerncraft.controlpanel.client.api.IServiceManagerEventHandler;
import com.stjerncraft.controlpanel.client.api.session.IClientSession;
import com.stjerncraft.controlpanel.client.api.session.ISessionListener;
import com.stjerncraft.controlpanel.client.api.session.SessionState;
import com.stjerncraft.controlpanel.client.messages.Messages;
import com.stjerncraft.controlpanel.client.websocket.IWebsocketListener;
import com.stjerncraft.controlpanel.client.websocket.WebSocket;
import com.stjerncraft.controlpanel.client.websocket.WebSocket.ReadyState;
import com.stjerncraft.controlpanel.common.Statics;
import com.stjerncraft.controlpanel.common.Version;
import com.stjerncraft.controlpanel.common.data.ServiceApiInfo;
import com.stjerncraft.controlpanel.common.data.ServiceProviderInfo;
import com.stjerncraft.controlpanel.common.messages.MessageCallMethod;
import com.stjerncraft.controlpanel.common.messages.MessageCallMethodReply;
import com.stjerncraft.controlpanel.common.messages.MessageCallSubscribe;
import com.stjerncraft.controlpanel.common.messages.MessageCallSubscribeReply;
import com.stjerncraft.controlpanel.common.messages.MessageEndSession;
import com.stjerncraft.controlpanel.common.messages.MessageEndSubscription;
import com.stjerncraft.controlpanel.common.messages.MessageSessionAccepted;
import com.stjerncraft.controlpanel.common.messages.MessageStartSession;
import com.stjerncraft.controlpanel.common.messages.MessageSubscriptionEvent;
import com.stjerncraft.controlpanel.common.messages.MessageVersion;

import jsinterop.annotations.JsType;

@JsType
public class ClientCore implements IClientCoreApi {
	static Logger logger = Logger.getLogger("ClientCore");
	
	private Map<Integer, ClientSession> pendingSessions; //Map of requestId to Pending Session
	private Map<Integer, ClientSession> sessions; //Map of Session ID to Session
	private Map<Integer, ClientSubscription> pendingSubscriptions; //Map of callId to Pending Subscription
	private Map<Integer, ClientSubscription> subscriptions; //Map of SubscriptionId to Subscription
	private Map<Integer, ICallMethodReturnHandler<String>> callMethodCallbacks; //Map of callId to return callback for callMethod.
	
	//Session that should be restarted once we get back a connection
	private List<ClientSession> sessionsToRestart;
	
	private String server;
	private WebSocket socket;
	private Messages messages;
	private ClientSession coreApiSession;
	private ClientModuleManager moduleManager;
	private ClientServiceManager serviceManager;
	
	private Timer reconnectTimer;
	private int reconnectTime = 5000; //How long to wait before trying to reconnect
	
	protected int requestIdCounter = 0; //Client ID for StartSession calls
	protected int callIdCounter = 0; //Call ID for CallMethod calls.
	
	public ClientCore(String server) {
		this.server = server;
		pendingSessions = new HashMap<>();
		sessions = new HashMap<>();
		callMethodCallbacks = new HashMap<>();
		pendingSubscriptions = new HashMap<>();
		subscriptions = new HashMap<>();
		sessionsToRestart = new ArrayList<ClientSession>();
		
		reconnectTimer = new Timer() {
			
			@Override
			public void run() {
				socket.open();
			}
		};
		
		logger.info("Starting Client Core...");
		GlobalClientCore.set(this);
		
		ServiceApiInfo coreApi = new ServiceApiInfo(CoreApiLibrary.getName(), CoreApiLibrary.getVersion());
		ServiceProviderInfo coreServiceProvider = new ServiceProviderInfo(Statics.CORE_PROVIDER_UUID, Statics.CORE_AGENT_UUID, ServiceProviderPriority.NORMAL, null);
		
		coreApiSession = new ClientSession(coreApi, coreServiceProvider, new ISessionListener() {
			
			@Override
			public void onStarted(IClientSession session) {
				logger.info("Core Session Accepted!");
				//Load WebView?
			}
			
			@Override
			public void onRejected(IClientSession session) {
				logger.info("Core Session Rejected!");
				
				//Retry
				if(socket.getReadyState() == ReadyState.OPEN)
					startExistingSession(coreApiSession);
			}
			
			@Override
			public void onEnded(IClientSession session) {
				logger.info("Core Session Ended!");
				
				//Re-open Core Session if lost
				//TODO: Handle this better. Why was it lost? This should only happen if the connection is lost.
				if(socket.getReadyState() == ReadyState.OPEN)
					startExistingSession(coreApiSession);
			}
		});
		
		messages = new Messages();
		setupMessageHandlers();
		
		moduleManager = new ClientModuleManager(this);
		
		serviceManager = new ClientServiceManager(this);
		serviceManager.setup();
		serviceManager.addEventHandler(new IServiceManagerEventHandler() {
			
			@Override
			public void onFullUpdate(IClientServiceManager serviceManager) {
				//We should have received a Module Manager Service Provider at this point
				moduleManager.setup();
			}
		});
		
		connect();
	}
	
	/**
	 * Start a new session, which will be in a Pending state until accepted by the remote Service Provider.
	 * @param api The API for the session.
	 * @param serviceProvider The Service Provider this session is with.
	 * @param listener Optional Listener for events like onStarted, onEnded etc. 
	 * @return The new pending Session, or null if it failed.
	 */
	public IClientSession startSessionRaw(IServiceApiInfo api, IServiceProviderInfo serviceProvider, ISessionListener listener) {
		ClientSession newSession = new ClientSession(api, serviceProvider, listener);

		//TODO: Verify that the given ServiceProvider actually implements the API(ServiceManager should have a full list of supported API's)?
		
		if(!startExistingSession(newSession))
			return null;
		
		return newSession;
	}
	
	@Override
	public <T extends IClientApiLibrary> IClientSession startSessionSpecific(T apiLibrary, IServiceProviderInfo serviceProvider, ISessionListener listener) {
		ServiceApiInfo apiInfo = new ServiceApiInfo(apiLibrary.getApiName(), apiLibrary.getApiVersion());
		IClientSession session = startSessionRaw(apiInfo,  serviceProvider, listener);
		if(session == null)
			return null;
		
		apiLibrary.setSession(session);
		return session;
	}
	
	@Override
	public <T extends IClientApiLibrary> IClientSession startSession(T apiLibrary, ISessionListener listener) {
		//Find Service Provider in the ServiceManager
		ServiceApiInfo apiInfo = new ServiceApiInfo(apiLibrary.getApiName(), apiLibrary.getApiVersion());
		IServiceProviderInfo serviceProvider = serviceManager.getBestProviderForApi(apiInfo);
		if(serviceProvider == null) {
			logger.warning("Failed to start session with API " + apiInfo.getId() + ", no Service Provider found!");
			return null;
		}
		
		IClientSession session = startSessionRaw(apiInfo, serviceProvider, listener);
		if(session == null)
			return null;
		
		apiLibrary.setSession(session);
		return session;
	}
	
	/**
	 * Start Session which is not in the PENDING or ACTIVE state.
	 * Note: This will create a new Session ID, and will have no history with the previous 
	 * session other than re-using the same API and Service Provider.
	 * @param session
	 * @return True if the session is being restarted, false if it was already started or invalid.
	 */
	public boolean startExistingSession(IClientSession session) {
		if(session == null)
			return false;
		
		SessionState sessionState = session.getCurrentState();
		if(sessionState == SessionState.PENDING || sessionState == SessionState.ACTIVE)
			return false;
		if(session.getApi() == null || session.getServiceProvider() == null)
			return false;
		if(socket == null || socket.getReadyState() != ReadyState.OPEN) {
			//Add it to the list of Sessions to try when we have a Connection
			logger.warning("Deferring StartSession due to no valid connection to Server");
			sessionsToRestart.add((ClientSession)session);
			return true;
		}
			
		int sessionRequestId = requestIdCounter++;
		logger.info(this + " Starting Session with Service Provider " + session.getServiceProvider().getUuid() + " for API " + session.getApi().getName());
		
		ClientSession clientSession = (ClientSession)session;
		pendingSessions.put(sessionRequestId, clientSession);
		messages.sendMessage(new MessageStartSession(sessionRequestId, session.getApi().getId(), session.getServiceProvider().getUuid()));
		clientSession.onPending();
		
		//TODO: Handle timeout
		
		return true;
	}
	
	@Override
	public void endSession(IClientSession session) {
		ClientSession clientSession = sessions.remove(session.getSessionId());
		
		if(clientSession == null)
			return;
		
		clientSession.onEnded(SessionEndedReason.ClientEnded);
	}
	
	@Override
	public int callMethod(int sessionId, String jsonMethod, ICallMethodReturnHandler<String> returnCallback) {
		int callId = callIdCounter++;
		
		//TODO: Verify that they're not providing a callback for Void methods? Maybe force a "reply" even for Void return calls?
		//TODO: Add some timeout on the return value? Right now the Service Provider is always forced to instantly return on call, so we can force a timeout.
		if(returnCallback != null) {
			callMethodCallbacks.put(callId, returnCallback);
		}
		
		messages.sendMessage(new MessageCallMethod(sessionId, callId, jsonMethod));
		return callId;
	}

	@Override
	public int callSubscribe(int sessionId, String JsonMethod, IClientSubscriptionHandler<String> handler) {
		ClientSession session = sessions.get(sessionId);
		if(session == null || session.getCurrentState() != SessionState.ACTIVE) {
			logger.warning("Failed to start Subscription, session " + sessionId + " is missing or in an invalid state!");
			return -1;
		}
		
		int callId = callIdCounter++;
		
		ClientSubscription newSubscription = new ClientSubscription(session, -1, handler);
		pendingSubscriptions.put(callId, newSubscription);
		
		messages.sendMessage(new MessageCallSubscribe(sessionId, callId, JsonMethod));
		return callId;
	}

	@Override
	public void callUnsubscribe(int subscriptionId) {
		//TODO: unsibscribe pending?
		ClientSubscription sub = subscriptions.remove(subscriptionId);
		if(sub == null)
			return;

		messages.sendMessage(new MessageEndSubscription(sub.getSession().getSessionId(), sub.getSubscriptionId()));
	}
	
	@Override
	public IClientModuleManager getModuleManager() {
		return moduleManager;
	}
	
	public IClientSession getCoreSession() {
		return coreApiSession;
	}
	
	private void connect() {
		logger.info("Connecting to Server...");
		
		socket = new WebSocket("ws://" + server + "/ws");
		messages.setSocket(socket);
		socket.addListener(new IWebsocketListener() {
			@Override
			public void onOpen() {
				messages.sendMessage(new MessageVersion(Version.Major, Version.Minor, Version.Fix));
			}
			
			@Override
			public void onClose(int code, String reason, boolean wasClean) {
				logger.warning("Socket closed with code " + code + ": " + reason);
				
				//TODO: Inform listeners about disconnect
				
				//Retry Pending Sessions on reconnect
				for(ClientSession session : pendingSessions.values()) {
					sessionsToRestart.add(session);
				}
				pendingSessions.clear();
				
				//Close Active Sessions, adding them to the retry list on reconnect
				for(ClientSession session : sessions.values()) {
					session.onEnded(SessionEndedReason.Disconnect);
					sessionsToRestart.add(session);
				}
				sessions.clear();
				
				//Any replies are "invalid" at this point
				callMethodCallbacks.clear();
				
				//Reconnect
				logger.info("Attempting to reconnect...");
				reconnectTimer.schedule(reconnectTime);
			}
		});
	}
	
	private void setupMessageHandlers() {
		messages.setHandler(MessageVersion.class, (msg, ws) -> {
			//TODO: Verify version from server
			logger.info("GOT VERSION: " + msg.versionMajor + "." + msg.versionMinor + "." + msg.versionFix + " on Socket: " + ws);
			startExistingSession(coreApiSession);
			
			//Restart any interrupted Sessions due to disconnect
			for(ClientSession session : sessionsToRestart) {
				startExistingSession(session);
			}
			sessionsToRestart.clear();
		});
		
		messages.setHandler(MessageSessionAccepted.class, (msg, ws) -> {
			ClientSession pendingSession = pendingSessions.remove(msg.requestId);
			if(pendingSession == null) {
				logger.warning("Got acceptance for unknown Session with Request ID: " + msg.requestId);
				return;
			}
			
			if(!msg.accepted) {
				pendingSession.onRejected();
				return;
			}
			
			sessions.put(msg.sessionId, pendingSession);
			pendingSession.onStarted(msg.sessionId);
			
		});
	
		messages.setHandler(MessageEndSession.class, (msg, ws) -> {
			ClientSession session = sessions.remove(msg.sessionId);
			if(session == null)
				return;
			
			session.onEnded(SessionEndedReason.RemoteEnded);
		});
		
		messages.setHandler(MessageCallMethodReply.class, (msg, ws) -> {
			logger.info("Got CallMethod Reply for " + msg.callId + ": " + msg.returnJson);
			
			ICallMethodReturnHandler<String> callback = callMethodCallbacks.remove(msg.callId);
			if(callback == null)
				return;
			
			callback.onReturnValue(msg.returnJson);
		});
		
		messages.setHandler(MessageCallSubscribeReply.class, (msg, ws) -> {
			ClientSubscription sub = pendingSubscriptions.remove(msg.callId);
			if(sub == null) {
				logger.warning("Got acceptance for unknown Subscription with Request ID: " + msg.callId);
				return;
			}
			
			if(msg.accepted)
				subscriptions.put(msg.subscriptionId, sub);
			sub.getHandler().OnSubscribed(msg.subscriptionId, msg.callId, msg.accepted);
		});
		
		messages.setHandler(MessageSubscriptionEvent.class, (msg, ws) -> {
			logger.info("Got SubscriptionEvent Reply for " + msg.subscriptionId + ": " + msg.valueJson);
			
			ClientSubscription sub = subscriptions.remove(msg.subscriptionId);
			if(sub == null)
				return;
			
			sub.getHandler().OnEvent(msg.subscriptionId, msg.valueJson);
		});
	}
}
