package com.stjerncraft.controlpanel.core.server.message;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("serial")
public class MessageDataDeserializer extends StdDeserializer<Message> {
	private ObjectMapper mapper;
	private Map<String, Class<IMessage>> handlerMap;
	
	public MessageDataDeserializer(Map<String, Class<IMessage>> handlerMap, ObjectMapper mapper) {
		super(Message.class);
		this.handlerMap = handlerMap;
		this.mapper = mapper;
	}
	
	@Override
	public Message deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		Message message = new Message();
		JsonNode root = mapper.readTree(p);
		message.messageId = root.get("messageId").asText();
		
		Class<IMessage> dataClass = handlerMap.get(message.messageId);
		if(dataClass == null)
			throw new IOException("No known handler for message with id: " + message.messageId);
		
		message.messageData = mapper.treeToValue(root.get("messageData"), dataClass);
		
		return message;
	}

}
