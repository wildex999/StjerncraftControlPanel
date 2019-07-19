package com.stjerncraft.controlpanel.client.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.google.gwt.user.client.Timer;
import com.stjerncraft.controlpanel.api.client.ICallMethodReturnHandler;
import com.stjerncraft.controlpanel.api.client.IClientSubscriptionHandler;
import com.stjerncraft.controlpanel.client.api.GlobalClientCore;
import com.stjerncraft.controlpanel.client.api.IClientCoreApi;
import com.stjerncraft.controlpanel.client.api.session.IClientSession;
import com.stjerncraft.controlpanel.client.api.session.ISessionListener;
import com.stjerncraft.controlpanel.client.api.session.SessionState;
import com.stjerncraft.controlpanel.client.messages.Messages;
import com.stjerncraft.controlpanel.client.session.ClientSession;
import com.stjerncraft.controlpanel.client.websocket.IWebsocketListener;
import com.stjerncraft.controlpanel.client.websocket.WebSocket;
import com.stjerncraft.controlpanel.client.websocket.WebSocket.ReadyState;
import com.stjerncraft.controlpanel.common.Statics;
import com.stjerncraft.controlpanel.common.Version;
import com.stjerncraft.controlpanel.common.api.CoreApiClient;
import com.stjerncraft.controlpanel.common.data.IServiceApiInfo;
import com.stjerncraft.controlpanel.common.data.IServiceProviderInfo;
import com.stjerncraft.controlpanel.common.data.ServiceApiInfo;
import com.stjerncraft.controlpanel.common.data.ServiceProviderInfo;
import com.stjerncraft.controlpanel.common.messages.MessageCallMethod;
import com.stjerncraft.controlpanel.common.messages.MessageCallMethodReply;
import com.stjerncraft.controlpanel.common.messages.MessageCallSubscribe;
import com.stjerncraft.controlpanel.common.messages.MessageCallSubscribeReply;
import com.stjerncraft.controlpanel.common.messages.MessageEndSubscription;
import com.stjerncraft.controlpanel.common.messages.MessageSessionAccepted;
import com.stjerncraft.controlpanel.common.messages.MessageSessionState;
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
	
	private String server;
	private WebSocket socket;
	private Messages messages;
	
	protected ClientSession coreApiSession;
	
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
		
		reconnectTimer = new Timer() {
			
			@Override
			public void run() {
				socket.open();
			}
		};
		
		ServiceApiInfo coreApi = new ServiceApiInfo(CoreApiClient.getApiName(), CoreApiClient.getApiVersion());
		ServiceProviderInfo coreServiceProvider = new ServiceProviderInfo(Statics.CORE_PROVIDER_UUID, Statics.CORE_AGENT_UUID, null);
		ClientCore self = this;
		
		coreApiSession = new ClientSession(coreApi, coreServiceProvider, new ISessionListener() {
			
			@Override
			public void onStarted(IClientSession session) {
				//Login user
				//Start Module Manager
				
				CoreApiClient coreClient = new CoreApiClient(self, session.getSessionId());
				coreClient.getAgents((agents) -> {
					logger.info("Got Agents: " + agents);
				});	
			}
			
			@Override
			public void onRejected(IClientSession session) {
				//Retry
				if(socket.getReadyState() == ReadyState.OPEN)
					startExistingSession(coreApiSession);
			}
			
			@Override
			public void onEnded(IClientSession session) {
				//Re-open Core Session if lost
				//TODO: Handle this better. Why was it lost? This should only happen if the connection is lost.
				if(socket.getReadyState() == ReadyState.OPEN)
					startExistingSession(coreApiSession);
			}
		});
		
		
		messages = new Messages();
		setupMessageHandlers();
		
		GlobalClientCore.set(this);
		
		connect();
	}
	
	/**
	 * Start a new session, which will be in a Pending state until accepted by the remote Service Provider.
	 * @param api The API for the session.
	 * @param serviceProvider The Service Provider this session is with.
	 * @param listener Optional Listener for events like onStarted, onEnded etc. 
	 * @return The new pending Session, or null if it failed.
	 */
	public IClientSession startSession(IServiceApiInfo api, IServiceProviderInfo serviceProvider, ISessionListener listener) {
		ClientSession newSession = new ClientSession(api, serviceProvider, listener);

		if(!startExistingSession(newSession))
			return null;
		
		return newSession;
	}
	
	/**
	 * Start Session which is not in the PENDING or ACTIVE state.
	 * Note: This will create a new Session ID, and will have no history with the previous 
	 * session other than re-using the same API and Service Provider.
	 * @param session
	 * @return True if the session is being restarted, false if it was already started or invalid.
	 */
	public boolean startExistingSession(ClientSession session) {
		SessionState sessionState = session.getCurrentState();
		if(sessionState == SessionState.PENDING || sessionState == SessionState.ACTIVE)
			return false;
		if(session.getApi() == null || session.getServiceProvider() == null)
			return false;
		
		int sessionRequestId = requestIdCounter++;
		logger.info(this + " Starting Session with Service Provider " + session.getServiceProvider().getUuid() + " for API " + session.getApi().getName());
		
		pendingSessions.put(sessionRequestId, session);
		messages.sendMessage(new MessageStartSession(sessionRequestId, session.getApi().getId(), session.getServiceProvider().getUuid()));
		
		//TODO: Handle timeout
		
		return true;
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
	
	public IClientSession getCoreSession() {
		return coreApiSession;
	}
	
	private void connect() {
		logger.info("Starting Client Core");
		
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
				
				//Reject Pending Sessions
				for(ClientSession session : pendingSessions.values()) {
					session.onRejected();
				}
				pendingSessions.clear();
				
				//Close Active Sessions
				for(ClientSession session : sessions.values()) {
					session.onEnded();
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
		
		messages.setHandler(MessageSessionState.class, (msg, ws) -> {
			logger.info("SessionState:_" + msg.sessionId + " | " + msg.started);
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
