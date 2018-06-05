package com.stjerncraft.controlpanel.common.messages;

public interface MessageHandler<T extends Message, C> {
	public void handle(T message, C context);
}
