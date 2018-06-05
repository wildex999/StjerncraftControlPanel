package com.stjerncraft.controlpanel.core.server;

import java.io.IOException;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stjerncraft.controlpanel.agent.ISession;
import com.stjerncraft.controlpanel.common.Version;
import com.stjerncraft.controlpanel.common.messages.Message;
import com.stjerncraft.controlpanel.common.messages.MessageEndSession;
import com.stjerncraft.controlpanel.common.messages.MessageStartSession;
import com.stjerncraft.controlpanel.common.messages.MessageVersion;
import com.stjerncraft.controlpanel.common.messages.Messages;
import com.stjerncraft.controlpanel.core.util.ExceptionString;

@WebSocket
public class CoreWebSocket {
	private class MessageAction {
		public Session session;
		public Message msg;
		public MessageAction(Session s, Message m) { this.session = s; this.msg = m; }
	}
	
	private static final Logger logger = LoggerFactory.getLogger(CoreWebSocket.class);
	private final Set<Session> sessions = Collections.newSetFromMap(new ConcurrentHashMap<Session, Boolean>());
	private final Queue<MessageAction> messageQueue = new ConcurrentLinkedQueue<>();
	private HTTPServer server;
	private Messages<Session> messages;
	
	public static final int WS_CODE_PROTOCOL_ERROR = 1002;
	public static final int WS_CODE_INTERNAL_ERROR = 1011;
	public static final int WS_CODE_UNSUPPORTED = 1003;
	
	public CoreWebSocket(HTTPServer server) {
		this.server = server;
		
		//Setup message handlers
		messages = new Messages<>();
		messages.registerMessages();
		messages.setHandler(MessageVersion.class, (msg, s) -> checkVersion(msg, s));
		messages.setHandler(MessageStartSession.class, (msg, s) -> startSession(msg, s));
		
	}
	
	@OnWebSocketConnect
	public void onConnect(Session session) {
		//TODO: Disconnect the user if we do not get a version message in x seconds. 
		//TODO: The websocket has a 30 second timeout, but we might need a specific check to protect against unknown messages?
		logger.info("WebSocket Connect from " + session.getRemoteAddress());
		sessions.add(session);
	}
	
	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		logger.info("WebSocket Disconnect from " + session.getRemoteAddress() + ": " + statusCode + "(" + reason + ")");
		//TODO: End all Service Sessions for this client
		//TODO: Must be thread safe
		sessions.remove(session);
	}
	
	@OnWebSocketMessage
	public void message(Session session, String message) throws IOException {
		try {
			Message msg = messages.decode(message);
			
			//Handle the message on the main thread
			messageQueue.add(new MessageAction(session, msg));
		} catch(Exception e) {
			String err = "Exception while parsing message: " + ExceptionString.PrintException(e);
			logger.warn(err);
			session.close(WS_CODE_INTERNAL_ERROR, err);
		}
		//Parse and handle the message
		/*IMessageHandler parsedMessage;
		try {
			parsedMessage = messageHandler.parseMessage(message);
		} catch(Exception e) {
			
			logger.warn("Failed to parse client message: " + ExceptionString.PrintException(e));
			session.close(WS_CODE_PROTOCOL_ERROR, "Error while parsing message");
			return;
		}
		
		try {
			parsedMessage.handle(session, server);
		} catch(Exception e) {
			logger.error("Error while handling client message: " + ExceptionString.PrintException(e));
			session.close(WS_CODE_INTERNAL_ERROR, "Error while handling message");
		}*/
	}
	
	/**
	 * Handle the Queued messages.
	 * This should be run on the main thread
	 */
	public void handleMessages() {
		for(MessageAction action : messageQueue) {
			Session session = action.session;
			if(!sessions.contains(session))
				continue; //Ignore unhandled messages from ended sessions
			
			try {
				messages.handleMessage(action.msg, session);
			} catch(Exception e) {
				String err = "Exception while handling message: " + ExceptionString.PrintException(e);
				logger.warn(err);
				session.close(WS_CODE_INTERNAL_ERROR, err);
			}
		}
	}
	
	/**
	 * Encode the given message and sent it to the remote target of the given session.
	 * Will listen for success and failure, ending the session if the send fails.
	 * @param session An existing session with a remote target.
	 * @param msg The message to encode and send.
	 */
	public void sendMessage(Session session, Message msg) {
		sendMessage(session, messages.encode(msg));
	}
	
	/**
	 * End the given session, removing it from the Core and sending a EndSession message to the Client.
	 * @param client
	 * @param session
	 * @param reason
	 */
	public void endSession(WebSocketClient client, ISession session, String reason) {
		if(!server.getCore().endSession(session.getSessionId(), reason))
			return;
		
		MessageEndSession msg = new MessageEndSession(session.getSessionId(), reason);
		sendMessage(client.getSocketSession(), msg);
	}
	
	/**
	 * Send a string to the remote target of the given session.
	 * Will listen for success and failure, ending the session if the send fails.
	 * @param session
	 * @param msg
	 */
	private void sendMessage(Session session, String msg) {
		//TODO: Reuse a single WriteCallback per session? Avoid the instantiation for every message sent
		session.getRemote().sendString(msg, new WriteCallback() {
			
			@Override
			public void writeSuccess() {
				//TODO: Count bytes sent for statistics
			}
			
			@Override
			public void writeFailed(Throwable x) {
				session.close(WS_CODE_INTERNAL_ERROR, "Failed to send data: " + x.getMessage());
			}
		});
	}
	
	/**
	 * Check the version received from the client, and send back our own version.
	 * Disconnect the client on version mismatch.
	 * @param msg
	 * @param session
	 */
	private void checkVersion(MessageVersion msg, Session session) {
		//TODO: Allow for mismatch on fix version?
		MessageVersion msgVersion = new MessageVersion(Version.Major, Version.Minor, Version.Fix);
		if(!msg.equals(msgVersion)) {
			String err = "Version mismatch: " + msg.versionMajor + "." + msg.versionMinor + "." + msg.versionFix + " != " + 
						Version.Major + "." + Version.Minor + "." + Version.Fix;
			session.close(WS_CODE_UNSUPPORTED, err);
			return;
		}
		
		sendMessage(session, msgVersion);
	}
	
	private void startSession(MessageStartSession msg, Session session) {
		
	}
}
