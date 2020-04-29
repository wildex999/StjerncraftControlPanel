package com.stjerncraft.controlpanel.common.api;

import com.stjerncraft.controlpanel.api.annotation.ServiceApi;
import com.stjerncraft.controlpanel.common.data.AuthenticationToken;
import com.stjerncraft.controlpanel.common.data.UserInfo;

@ServiceApi(version=1)
public interface UserManagerApi {
	/**
	 * Create a new User.
	 * Permission: core.users.create
	 * @param username
	 * @param hashedPassword
	 * @return CreateUserResult
	 */
	void createUser(String username, String hashedPassword);
	
	/**
	 * 
	 * Permission: core.users.read
	 * @param username
	 * @return The found user, or null if it does not exist.
	 */
	UserInfo getUserInfo(String username);
	
	/**
	 * 
	 * Permission: core.users.login
	 * @param username
	 * @param hashedPassword
	 */
	void loginUser(String username, String hashedPassword);
	
	/**
	 * Generate an Authentication Token for a logged in user.
	 * Permission: core.users.generatetoken
	 * @param user
	 * @param type
	 * @return
	 */
	AuthenticationToken generateAuthenticationToken(String username, AuthenticationToken.Type type);
	
	/**
	 * Check whether the Token is valid for the User
	 * Permission: core.users.validatetoken
	 * @param user
	 * @param token
	 * @return
	 */
	boolean validateAuthenticationToken(String username, AuthenticationToken token);
}
