package com.stjerncraft.controlpanel.api;

import org.json.JSONArray;

public interface IDataObjectGenerated<T> {
	public T parse(JSONArray jsonObj);
	public JSONArray serialize(T obj);
	public T create();
}
