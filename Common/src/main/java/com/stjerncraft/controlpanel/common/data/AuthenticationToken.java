package com.stjerncraft.controlpanel.common.data;

import com.stjerncraft.controlpanel.api.annotation.DataObject;

@DataObject
public class AuthenticationToken {
	public enum Type {
		Session, //Short living token for a single session.
		RememberMe //Long living token for multiple sessions(RememberMe).
	}
	
	public String token;
	public Type type;
	public String creationDate; //Date & Time this token was created. Used to manage validity lifetime.
}
