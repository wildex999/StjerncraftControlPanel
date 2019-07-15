package com.stjerncraft.controlpanel.module.core.session;

public enum SessionState {
	IDLE, //Not yet requested
	PENDING, //Waiting for answer
	REJECTED, //Was Rejected
	ACTIVE, //Currently active
	INACTIVE //Was Active earlier, but has now been ended.
}
