package org.json;

import java.util.Set;

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
	
	public JSONObject() throws JSONException {
		obj = new com.google.gwt.json.client.JSONObject();
	}
	
	public JSONObject put(String key, JSONArray value) throws JSONException {
		if (key == null) {
            throw new NullPointerException("Null key.");
        }
		
        obj.put(key, value.arr);
        
        return this;
	}
	
    public Set<String> keySet() {
    	return obj.keySet();
    }

    public int length() {
    	return obj.size();
    }
    
    /**
     * Get the JSONArray value associated with a key.
     *
     * @param key
     *            A key string.
     * @return A JSONArray which is the value.
     * @throws JSONException
     *             if the key is not found or if the value is not a JSONArray.
     */
    public JSONArray getJSONArray(String key) throws JSONException {
    	JSONValue value = obj.get(key);
    	if(value == null)
    	{
    		 throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
    	}
    	
    	com.google.gwt.json.client.JSONArray array = value.isArray();
    	if(array == null)
    	{
    		throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
    	}
    	
    	return new JSONArray(array);
    }
	
	@Override
	public String toString() {
		return obj.toString();
	}
}
