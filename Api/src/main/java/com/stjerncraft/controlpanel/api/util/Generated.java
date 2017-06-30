package com.stjerncraft.controlpanel.api.util;

import com.stjerncraft.controlpanel.api.IServiceApiGenerated;
import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.api.processor.ApiStrings;

public class Generated {
	public static IServiceApiGenerated getGeneratedApi(Class<? extends IServiceProvider> apiInterfaceClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> clazz = Class.forName(apiInterfaceClass.getCanonicalName() + ApiStrings.APISUFFIX);
		return (IServiceApiGenerated)clazz.newInstance();
	}
}
