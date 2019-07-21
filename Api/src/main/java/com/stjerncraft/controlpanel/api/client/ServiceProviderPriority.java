package com.stjerncraft.controlpanel.api.client;

import jsinterop.annotations.JsType;

@JsType
public enum ServiceProviderPriority {
	HIGHEST,  //Should override ALL other implementations
	HIGH, //Should override normal priority implementations.
	NORMAL, //Default normal priority
	LOW, //Only use if there is no implementation of normal priority
	LOWEST //Only use as a backup if there exists nothing else
}
