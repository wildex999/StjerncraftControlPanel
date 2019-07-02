package com.stjerncraft.controlpanel.api.util;

import java.util.HashMap;

import com.stjerncraft.controlpanel.api.IDataObjectGenerated;
import com.stjerncraft.controlpanel.api.IServiceApiGenerated;
import com.stjerncraft.controlpanel.api.processor.ApiStrings;

/**
 * Helper class for getting the generated classes for interacting with the Service API and DataObject.
 * Some raw and unchecked code here to avoid casting the DataObject types before using them(We know that what we put in is what we get out)
 */
@SuppressWarnings("rawtypes")
public class Generated {
	static HashMap<Class<?>, IServiceApiGenerated> apiMap = new HashMap<>();
	static HashMap<Class<?>, IDataObjectGenerated> dataObjectMap = new HashMap<>();
	
	public static IServiceApiGenerated getGeneratedApi(Class<?> apiInterfaceClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		IServiceApiGenerated apiInstance = apiMap.get(apiInterfaceClass);
		if(apiInstance != null)
			return apiInstance;
		
		Class<?> clazz = Class.forName(apiInterfaceClass.getCanonicalName() + ApiStrings.APISUFFIX);
		apiInstance = (IServiceApiGenerated)clazz.newInstance();
		apiMap.put(apiInterfaceClass, apiInstance);
		return apiInstance;
	}

	@SuppressWarnings("unchecked")
	public static <T> IDataObjectGenerated<T> getGeneratedDataObject(Class<T> dataObjectClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		IDataObjectGenerated<T> dataObjectInstance = dataObjectMap.get(dataObjectClass);
		if(dataObjectInstance != null)
			return dataObjectInstance;
		
		Class<?> clazz = Class.forName(dataObjectClass.getCanonicalName() + ApiStrings.DATAOBJECTSUFFIX);
		dataObjectInstance = (IDataObjectGenerated<T>)clazz.newInstance();
		dataObjectMap.put(dataObjectClass, dataObjectInstance);
		return dataObjectInstance;
	}
}
