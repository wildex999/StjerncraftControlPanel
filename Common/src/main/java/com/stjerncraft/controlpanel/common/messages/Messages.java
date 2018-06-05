package com.stjerncraft.controlpanel.common.messages;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import com.stjerncraft.controlpanel.api.IDataObjectGenerated;
import com.stjerncraft.controlpanel.api.annotation.DataObjectFactory;

/**
 * Serialize, Deserialize and handle messages.
 */
@DataObjectFactory
public class Messages<C> {
	private HashMap<Class<? extends Message>, MessageHandler<? extends Message, C>> handlers;
	public Map<String, IDataObjectGenerated<Message>> parserMap;
	
	public Messages() {
		handlers = new HashMap<>();
		parserMap = new HashMap<>();
	}
	
	/**
	 * Register the common messages
	 */
	public void registerMessages() {
		register(MessageVersion.class);
		register(MessageStartSession.class);
		register(MessageEndSession.class);
	}
	
	/**
	 * Register a message so that it can be sent and received.
	 * If the message is not registered, it can not be Serialized or Deserialized.
	 * Note: The class name(Not including package) is the identifier, only one can exist with the same name.
	 * @param message Message whose class to register.
	 */
	@SuppressWarnings("unchecked")
	protected void register(Class<? extends Message> message) {
		IDataObjectGenerated<Message> msgGenerated = MessagesFactory.getGenerator(message);
		parserMap.put(message.getName(), msgGenerated);
	}
	
	/**
	 * Set the handler to call when receiving the given message.
	 * Note: Each message can only have one handler. Existing handlers are overwritten.
	 * @param message The message to handle
	 * @param handler The handler to handle the message.
	 */
	public <T extends Message> void setHandler(Class<T> message, MessageHandler<T, C> handler) {
		handlers.put(message, handler);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void handleMessage(Message msg, C context) {
		//unchecked and rawtypes: 
		//We know that the type of message and handler matches, as we checked during the handler registration.
		
		MessageHandler handler = handlers.get(msg.getClass());
		if(handler == null)
			throw new IllegalArgumentException("Got message without handler: " + msg);
		
		handler.handle(msg, context);
	}
	
	/**
	 * Decode a message string with the Message name first, followed by the message json
	 * @param msgData
	 */
	public Message decode(String msgData) {
		int nameEnd = msgData.indexOf('\n');
		if(nameEnd == -1)
			throw new IllegalArgumentException("Name of message not found: " + msgData);
		
		String name = msgData.substring(0, nameEnd);
		String json = msgData.substring(nameEnd + 1);
		
		JSONArray arr = new JSONArray(json);
		return deserialize(name, arr);
	}
	
	/**
	 * Encode the given message so it can be sent as a String and decoded on the other end.
	 * @param msg
	 * @return
	 */
	public String encode(Message msg) {
		String name = msg.getClass().getName() + '\n';
		String json = serialize(msg).toString();
		return name + json;
	}
	
	/**
	 * Serialize the given Message instance into a json string.
	 * @param message Instance to serialize
	 * @return JSONArray containing the message data
	 */
	protected JSONArray serialize(Message message) throws IllegalArgumentException {
		IDataObjectGenerated<Message> msgGenerated = parserMap.get(message.getClass().getName());
		if(msgGenerated == null)
			throw new IllegalArgumentException("Trying to serialize unregistered message: " + message);
		
		return msgGenerated.serialize(message);
	}
	
	/**
	 * Deserialize the given JSON data as a Message identified by the given class name.
	 * @param messageName Name of the message class to use when parsing the json.
	 * @param json JSONArray containing the message data.
	 * @return Instance of the deserialized message
	 */
	protected Message deserialize(String messageName, JSONArray json) throws IllegalArgumentException{
		IDataObjectGenerated<Message> msgGenerated = parserMap.get(messageName);
		if(msgGenerated == null)
			throw new IllegalArgumentException("Trying to parse unregistered message: " + messageName + " | json: " + json);
		
		return msgGenerated.parse(json);
	}
}
