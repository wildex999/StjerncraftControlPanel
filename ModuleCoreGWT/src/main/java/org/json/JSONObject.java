package org.json;

import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * A basic GWT version of org.json.JSONObject
 */
public class JSONObject {
	com.google.gwt.json.client.JSONObject obj;

	public JSONObject(String json) throws JSONException {
		JSONValue value = JSONParser.parseStrict(json);
		obj = value.isObject();
		
		if(obj == null)
			throw new JSONException("The given JSON is not a JSONObject: " + json);
	}
	
	@Override
	public String toString() {
		return obj.toString();
	}
}
