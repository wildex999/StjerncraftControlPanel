package com.stjerncraft.controlpanel.core.client;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stjerncraft.controlpanel.agent.IRemoteClient;
import com.stjerncraft.controlpanel.agent.ISession;
import com.stjerncraft.controlpanel.agent.ISessionListener;
import com.stjerncraft.controlpanel.agent.ServiceProvider;
import com.stjerncraft.controlpanel.common.ServiceApi;
import com.stjerncraft.controlpanel.common.Version;
import com.stjerncraft.controlpanel.common.messages.Message;
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
import com.stjerncraft.controlpanel.common.messages.Messages;
import com.stjerncraft.controlpanel.core.Core;
import com.stjerncraft.controlpanel.core.server.CoreWebSocket;
import com.stjerncraft.controlpanel.core.util.ExceptionString;

public class ClientManager {
	private class MessageAction {
		public IRemoteClient client;
		public Message msg;
		public MessageAction(IRemoteClient c, Message m) { this.client = c; this.msg = m; }
	}
	
	private static final Logger logger = LoggerFactory.getLogger(CoreWebSocket.class);
	private final Core core;
	
	private final KeySetView<IRemoteClient, Boolean> clients;
	private final Queue<MessageAction> messageQueue;
	private Messages<IRemoteClient> messages;
	
	public ClientManager(Core core) {
		this.core = core;
		clients = ConcurrentHashMap.newKeySet();
		messageQueue = new ConcurrentLinkedQueue<>();
		
		//Setup message handlers on messages received from client
		messages = new Messages<>();
		messages.registerMessages();
		messages.setHandler(MessageVersion.class, (msg, s) -> checkVersion(msg, s));
		messages.setHandler(MessageStartSession.class, (msg, s) -> startSession(msg, s));
		messages.setHandler(MessageCallMethod.class, (msg, s) -> callMethod(msg, s));
		messages.setHandler(MessageCallSubscribe.class, (msg, s) -> callSubscribe(msg, s));
	}
	
	/**
	 * Add the client to the list of clients.
	 * This method is thread safe.
	 * @param client
	 */
	public void addClient(IRemoteClient client) {
		clients.add(client);
	}
	
	/**
	 * Remove the client from the list of clients.
	 * This will NOT call disconnect on the client!
	 * This method is thread safe
	 * @param client
	 * @return True if the client was removed, false if it was not found.
	 */
	public boolean removeClient(IRemoteClient client) {
		if(!clients.remove(client))
			return false;

		return true;
	}
	
	public Collection<IRemoteClient> getClients() {
		return clients;
	}
	
	/**
	 * Parse the received message data and add it to the message queue for handling in the main thread.
	 * This method is thread safe.
	 * @param client
	 */
	public void receiveMessage(IRemoteClient client, String message) {
		if(!client.isConnected())
			return;
		
		try {
			Message msg = messages.decode(message);
			
			//Handle the message on the main thread
			messageQueue.add(new MessageAction(client, msg));
		} catch(Exception e) {
			String err = "Exception while parsing message: " + ExceptionString.PrintException(e);
			logger.warn(err);
			client.disconnect(err);
		}
	}
	
	/**
	 * Handle the Queued messages.
	 * This should be run on the main thread
	 */
	public void handleMessages() {
		for(MessageAction action; (action = messageQueue.poll()) != null;) {
			IRemoteClient client = action.client;
			if(!clients.contains(client) || !client.isConnected())
				continue; //Ignore unhandled messages from ended sessions
			
			try {
				messages.handleMessage(action.msg, client);
			} catch(Exception e) {
				String err = "Exception while handling message: " + ExceptionString.PrintException(e);
				logger.warn(err);
				client.disconnect(err);
			}
		}
	}
	
	/**
	 * Encode the given message and sent it to the remote target of the given session.
	 * Will listen for success and failure, ending the session if the send fails.
	 * @param client The remote client to send message to
	 * @param msg The message to encode and send.
	 */
	private void sendMessage(IRemoteClient client, Message msg) {
		if(!clients.contains(client) || !client.isConnected())
			return;
		
		client.sendMessage(messages.encode(msg));
	}
	
	/**
	 * Check the version received from the client, and send back our own version.
	 * Disconnect the client on version mismatch.
	 * @param msg
	 * @param session
	 */
	private void checkVersion(MessageVersion msg, IRemoteClient client) {
		//TODO: Allow for mismatch on fix version?
		MessageVersion msgVersion = new MessageVersion(Version.Major, Version.Minor, Version.Fix);
		if(!msg.equals(msgVersion)) {
			String err = "Version mismatch: " + msg.versionMajor + "." + msg.versionMinor + "." + msg.versionFix + " != " + 
						Version.Major + "." + Version.Minor + "." + Version.Fix;
			logger.warn(err);
			client.disconnect(err);
			return;
		}
		
		sendMessage(client, msgVersion);
	}
	
	/**
	 * Start a new Session between the Client and Agent using the given Service Provider and API.
	 * @param msg
	 * @param client
	 */
	private void startSession(MessageStartSession msg, IRemoteClient client) {
		ServiceProvider<? extends ServiceApi> provider = core.getServiceProvider(msg.serviceProviderId);
		ServiceApi api = core.getServiceApi(msg.apiId);
		
		ISession newSession = core.startSession(client, provider, api);
		
		//Send the session accepted update
		int sessionId = newSession != null ? newSession.getSessionId() : -1;
		MessageSessionAccepted accepted = new MessageSessionAccepted(msg.requestId, sessionId, newSession != null);
		sendMessage(client, accepted);
		
		if(newSession == null)
			return;
		
		//Listen for changes to the Session state, and keep the client updated
		sendSessionState(client, newSession);
		newSession.addListener(new ISessionListener() {
			@Override
			public void onSessionStarted() {
				sendSessionState(client, newSession);
			}
			
			@Override
			public void onSessionEnded(String reason) {
				MessageEndSession msg = new MessageEndSession(newSession.getSessionId(), reason);
				sendMessage(client, msg);
			}
		});
	}
	
	/**
	 * Call a method 
	 * @param msg
	 * @param client
	 */
	private void callMethod(MessageCallMethod msg, IRemoteClient client) {
		ISession session = core.getSession(msg.sessionId);
		if(session == null)
		{
			//The session might have been ended before we received this message, ignore it for now.
			logger.warn("Trying to call method on unknown/ended session");
			//TODO: We might need to be more strict here, and have a session history we can check against?
			return;
		}
		
		if(session.getRemoteClient() != client)
		{
			//Trying to use a session which doesn't belong to the client.
			//We see that as a possible invalid state/bad intent, and disconnect the client.
			client.disconnect("callMethod on invalid session");
			return;
		}
		
		session.callMethod(msg.methodJson, (returnJson) -> {
			if(!client.isConnected())
				return;
			
			//Pass on the return value to the client
			MessageCallMethodReply replyMsg = new MessageCallMethodReply(msg.sessionId, msg.callId, returnJson);
			sendMessage(client, replyMsg);
		});
	}
	
	/**
	 * Call a method 
	 * @param msg
	 * @param client
	 */
	private void callSubscribe(MessageCallSubscribe msg, IRemoteClient client) {
		ISession session = core.getSession(msg.sessionId);
		if(session == null)
		{
			//The session might have been ended before we received this message, ignore it for now.
			logger.warn("Trying to subscribe on unknown/ended session");
			//TODO: We might need to be more strict here, and have a session history we can check against?
			return;
		}
		
		if(session.getRemoteClient() != client)
		{
			//Trying to use a session which doesn't belong to the client.
			//We see that as a possible invalid state/bad intent, and disconnect the client.
			client.disconnect("callSubscribe on invalid session");
			return;
		}
		
		Consumer<Integer> subscribeCallback = subscriptionId -> {
			if(!client.isConnected())
				return;
			
			int subId = subscriptionId == null ? 0 : subscriptionId;
			
			MessageCallSubscribeReply replyMsg = new MessageCallSubscribeReply(msg.sessionId, msg.callId, subscriptionId != null, subId);
			sendMessage(client, replyMsg);
		};
		
		Consumer<Integer> unsubscribeCallback = subscriptionId -> {
			if(!client.isConnected())
				return;
			
			MessageEndSubscription unsubMsg = new MessageEndSubscription(msg.sessionId, subscriptionId);
			sendMessage(client, unsubMsg);
		};
		
		BiConsumer<Integer, String> eventCallback = (subscriptionId, eventJson) -> {
			if(!client.isConnected())
				return;
			
			MessageSubscriptionEvent eventMsg = new MessageSubscriptionEvent(msg.sessionId, subscriptionId, eventJson);
			sendMessage(client, eventMsg);
		};
		
		session.callSubscribe(msg.methodJson, subscribeCallback, eventCallback, unsubscribeCallback);
	}
	
	/**
	 * Send a SessionState update to the Client for the given session.
	 * @param client
	 * @param session
	 */
	private void sendSessionState(IRemoteClient client, ISession session) {
		
	}
}
