package com.stjerncraft.controlpanel.common.util;

public class UUID {
	public static boolean isUuidValid(String uuid) {
		if(uuid == null || uuid.trim().length() != 36) //36 with dashes
			return false;
		return true;
	}
}
