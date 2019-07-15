package org.json;

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * A very simple and incomplete implementation of the org.json.JSONArray for use with GWT.
 */
public class JSONArray {
	com.google.gwt.json.client.JSONArray arr;
	
	public JSONArray() {
		arr = new com.google.gwt.json.client.JSONArray();
	}
	
	public JSONArray(com.google.gwt.json.client.JSONArray arr) {
		this.arr = arr;
	}
	
	public JSONArray(String json) throws JSONException {
		JSONValue val = JSONParser.parseStrict(json);
		arr = val.isArray();
		if(arr == null)
			throw new JSONException("The given JSON is not a JSONArray: " + json);
	}
	
	public int length() {
		return arr.size();
	}
	
	public JSONArray getJSONArray(int index) throws JSONException {
		JSONValue value = arr.get(index);
		com.google.gwt.json.client.JSONArray newArr = value.isArray();
		if(newArr == null)
			throw new JSONException("JSONArray[" + index + "] is not a JSONArray.");
		
		return new JSONArray(newArr);
	}
	
	public String getString(int index) throws JSONException {
		JSONValue value = arr.get(index);
		JSONString str = value.isString();
		if(str == null)
			throw new JSONException("JSONArray[" + index + "] is not a String.");
		
		return str.stringValue();
	}
	
	public int getInt(int index) throws JSONException {
		JSONValue value = arr.get(index);
		JSONNumber number = value.isNumber();
		if(number == null) {
			//Try parsing string number
			JSONString str = value.isString();
			try {
				return Integer.parseInt(str.stringValue());
			} catch(Exception e) {
				throw new JSONException("JSONArray[" + index + "] is not a number.", e);
			}
		}
		
		return (int)number.doubleValue();
	}
	
	public double getDouble(int index) throws JSONException {
		JSONValue value = arr.get(index);
		JSONNumber number = value.isNumber();
		if(number == null) {
			//Try parsing string number
			JSONString str = value.isString();
			try {
				return Double.parseDouble(str.stringValue());
			} catch(Exception e) {
				throw new JSONException("JSONArray[" + index + "] is not a number.", e);
			}
		}
		
		return number.doubleValue();
	}
	
	public float getFloat(int index) throws JSONException {
		JSONValue value = arr.get(index);
		JSONNumber number = value.isNumber();
		if(number == null) {
			//Try parsing string number
			JSONString str = value.isString();
			try {
				return Float.parseFloat(str.stringValue());
			} catch(Exception e) {
				throw new JSONException("JSONArray[" + index + "] is not a number.", e);
			}
		}
		
		return (float)number.doubleValue();
	}
	
	public long getLong(int index) throws JSONException {
		JSONValue value = arr.get(index);
		JSONNumber number = value.isNumber();
		if(number == null) {
			//Try parsing string number
			JSONString str = value.isString();
			try {
				return Long.parseLong(str.stringValue());
			} catch(Exception e) {
				throw new JSONException("JSONArray[" + index + "] is not a number.", e);
			}
		}
		
		return (long)number.doubleValue();
	}
	
	public boolean getBoolean(int index) throws JSONException {
		JSONValue value = arr.get(index);
		JSONBoolean b = value.isBoolean();
		if(b == null) {
			//Try parsing string boolean
			JSONString str = value.isString();
			if(str != null) {
				String strValue = str.stringValue();
				if(strValue.equalsIgnoreCase("true"))
					return true;
				else if(strValue.equalsIgnoreCase("false"))
					return false;
			}
		} else
			return b.booleanValue();
		
		throw new JSONException("JSONArray[" + index + "] is not a boolean.");
	}
	
	public void put(JSONArray value) {
		if(value == null)
			arr.set(arr.size(), JSONNull.getInstance());
		else
			arr.set(arr.size(), value.arr);
	}
	
	public void put(int value) {
		JSONNumber nr = new JSONNumber(value);
		arr.set(arr.size(), nr);
	}
	
	public void put(Integer value) {
		if(value == null)
			arr.set(arr.size(), JSONNull.getInstance());
		else
			put((int)value);
	}
	
	public void put(long value) {
		JSONNumber nr = new JSONNumber(value);
		arr.set(arr.size(), nr);
	}
	
	public void put(Long value) {
		if(value == null)
			arr.set(arr.size(), JSONNull.getInstance());
		else
			put((long)value);
	}
	
	public void put(float value) {
		JSONNumber nr = new JSONNumber(value);
		arr.set(arr.size(), nr);
	}
	
	public void put(Float value) {
		if(value == null)
			arr.set(arr.size(), JSONNull.getInstance());
		else
			put((float)value);
	}
	
	public void put(double value) {
		JSONNumber nr = new JSONNumber(value);
		arr.set(arr.size(), nr);
	}
	
	public void put(Double value) {
		if(value == null)
			arr.set(arr.size(), JSONNull.getInstance());
		else
			put((double)value);
	}
	
	public void put(boolean value) {
		JSONBoolean b = JSONBoolean.getInstance(value);
		arr.set(arr.size(), b);
	}
	
	public void put(Boolean value) {
		if(value == null)
			arr.set(arr.size(), JSONNull.getInstance());
		else
			put((boolean)value);
	}
	
	public void put(String value) {
		if(value == null)
			arr.set(arr.size(), JSONNull.getInstance());
		else {
			JSONString str = new JSONString(value);
			arr.set(arr.size(), str);
		}
	}
	
	@Override
	public String toString() {
		return arr.toString();
	}
}
