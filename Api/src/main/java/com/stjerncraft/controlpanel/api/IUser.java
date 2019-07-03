package com.stjerncraft.controlpanel.api;

/**
 * A known user with name, history and permissions.
 * The user might have multiple client connected, or might be limited to one.
 *
 */
public interface IUser {
	
	String getUsername();
	String getPassword();
	
	String getDisplayName();
	//TODO: Permissions?
}
