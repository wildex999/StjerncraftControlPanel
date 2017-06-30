package com.stjerncraft.controlpanel.core.server.message;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class MessageDataSerializer extends JsonSerializer<IMessage> {
	private Map<String, Class<IMessage>> handlerMap;

	public MessageDataSerializer(Map<String, Class<IMessage>> handlerMap) {
		this.handlerMap = handlerMap;
	}
	
	@Override
	public void serialize(IMessage value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		// TODO Auto-generated method stub
		
	}

}
