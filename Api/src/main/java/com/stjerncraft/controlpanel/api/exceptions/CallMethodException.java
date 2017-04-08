package com.stjerncraft.controlpanel.api.exceptions;

import com.stjerncraft.controlpanel.api.IServiceApiGenerated;

@SuppressWarnings("serial")
public class CallMethodException extends RuntimeException {
	public String method;
	public String json;
	public String reason;
	public IServiceApiGenerated api;
	
	public CallMethodException(IServiceApiGenerated api, String method, String reason, String json) {
		super("Error while trying to call method " + method + " in api " + api.getApiName() + ":" + api.getApiVersion() +". " + reason + "\n" +
				"call: " + json);
		this.method = method;
		this.json = json;
		this.reason = reason;
		this.api = api;
	}
}
