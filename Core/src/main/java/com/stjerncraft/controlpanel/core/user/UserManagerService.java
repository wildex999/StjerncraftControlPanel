package com.stjerncraft.controlpanel.core.user;

import com.stjerncraft.controlpanel.api.IServiceManager;
import com.stjerncraft.controlpanel.api.IServiceProvider;
import com.stjerncraft.controlpanel.api.IUser;
import com.stjerncraft.controlpanel.common.api.UserManagerApi;
import com.stjerncraft.controlpanel.common.data.AuthenticationToken;
import com.stjerncraft.controlpanel.common.data.AuthenticationToken.Type;

/**
 * Client API for accessing the User Manager
 */
public class UserManagerService implements UserManagerApi, IServiceProvider {

	@Override
	public void onRegister(IServiceManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnregister() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createUser(String username, String hashedPassword) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public User getUserInfo(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loginUser(String username, String hashedPassword) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AuthenticationToken generateAuthenticationToken(String username, Type type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validateAuthenticationToken(String username, AuthenticationToken token) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * A Client authenticated with the given User.
	 * The user should be fetched into the Memory from the Database.
	 */
	public void OnUserJoined() {
		
	}
	
	/**
	 * A Client either disconnected or de-authenticated with the User.
	 * If no other clients are authenticated as the User, unload the User from memory.
	 */
	public void OnUserLeft() {
		
	}
}
