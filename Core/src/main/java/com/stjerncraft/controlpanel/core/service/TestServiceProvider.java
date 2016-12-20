package com.stjerncraft.controlpanel.core.service;

import com.stjerncraft.controlpanel.api.annotation.ServiceApi;

@ServiceApi(version=1)
public interface TestServiceProvider extends com.stjerncraft.controlpanel.api.IServiceProvider {
	
	public String test();
	void test2();
}
