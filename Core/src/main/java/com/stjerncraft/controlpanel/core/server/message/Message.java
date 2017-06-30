package com.stjerncraft.controlpanel.core.server.message;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Message between client and server, usually sent over a websocket.
 * The message contains a messageID and message content. 
 * The messageID is used to determine the handler for the message content.
 * The message is serialized as a JSON object, reflecting this class.
 */
@JsonSerialize(using = MessageDataSerializer.class)
public class Message {
	public String messageId;
	public IMessage messageData;
	
	/**
	 * Create a new message for the given message data
	 * @param data
	 * @return
	 */
	public static Message newFrom(IMessage data) {
		if(data == null)
			return null;
		
		Message newMessage = new Message();
		newMessage.messageData = data;
		newMessage.messageId = data.getMessageId();
		
		return newMessage;
	}
}
