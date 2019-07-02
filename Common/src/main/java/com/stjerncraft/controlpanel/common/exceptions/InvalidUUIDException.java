package com.stjerncraft.controlpanel.common.exceptions;

/**
 * Exception thrown if the UUID is invalid, or in the infinitesimal chance of a UUID collision.
 */
@SuppressWarnings("serial")
public class InvalidUUIDException extends Exception {
	String uuid;
	boolean collision;
	
	public InvalidUUIDException(String uuid) {
		super("Invalid UUID: " + uuid);
		collision = false;
		this.uuid = uuid;
	}
	
	public InvalidUUIDException(String uuid, boolean collision) {
		super("UUID Collision(How did you manage that? The gods must hate you, or the dice is loaded!): " + uuid);
		this.collision = collision;
		this.uuid = uuid;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public boolean wasCollision() {
		return collision;
	}
}
