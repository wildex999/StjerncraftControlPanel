package com.stjerncraft.controlpanel.core.user;

import com.stjerncraft.controlpanel.api.IUser;
import com.stjerncraft.controlpanel.common.data.UserInfo;
import com.stjerncraft.controlpanel.core.permission.UserPermissions;

/**
 * A known user with name, history and permissions.
 * The user might have multiple client connected, or might be limited to one.
 *
 */
public class User extends UserInfo implements IUser {
	private UserManagerService manager;
	private UserPermissions permissions;
	
	public User(UserManagerService userManager) {
		manager = userManager;
	}
	
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}
}
