package com.stjerncraft.controlpanel.api;


public interface IUser {
	
	String getUsername();
	String getPassword();
	
	String getDisplayName();
	//TODO: Permissions?
}
