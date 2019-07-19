package com.stjerncraft.controlpanel.client.api.session;

import jsinterop.annotations.JsType;

@JsType
public enum SessionState {
	IDLE, //Not yet requested
	PENDING, //Waiting for answer
	REJECTED, //Was Rejected
	ACTIVE, //Currently active
	INACTIVE //Was Active earlier, but has now been ended.
}
