package com.stjerncraft.controlpanel.core.server.message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class MessageHandler {
	Map<String, Class<IMessage>> messageHandlers;
	ObjectMapper messageMapper;
	
	public MessageHandler() {
		messageHandlers = new HashMap<>();
		
		messageMapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Message.class, new MessageDataDeserializer(messageHandlers, messageMapper));
		//module.addSerializer(Message.class, new MessageDataSerializer(messageHandlers));
		messageMapper.registerModule(module);
	}
	
	/**
	 * Set the class to use when parsing the given message id.
	 * @param messageId
	 * @param messageClass
	 */
	public void setMessageClass(String messageId, Class<IMessage> messageClass) {
		messageHandlers.put(messageId, messageClass);
	}
	
	/**
	 * Parse the JSON object into a message, using the message handlers for the message data.
	 * @param message
	 * @return The message data
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public IMessage parseMessage(String message) throws JsonParseException, JsonMappingException, IOException {
		Message parsedMessage = messageMapper.readValue(message, Message.class);
		return parsedMessage.messageData;
	}
	
	/**
	 * Serialize the message to JSON.
	 * Note: The message class can not be generic.
	 * @param message
	 * @return
	 * @throws JsonProcessingException 
	 */
	public String serializeMessage(IMessage message) throws JsonProcessingException {
		Message newMessage = Message.newFrom(message);
		return messageMapper.writeValueAsString(newMessage);
	}
}
