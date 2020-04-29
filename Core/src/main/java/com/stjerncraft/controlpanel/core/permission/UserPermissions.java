package com.stjerncraft.controlpanel.core.permission;

import java.util.Map;
import java.util.Set;

/**
 * User permissions which can be queried for specific permission.
 * A user can belong to one or more groups, from which the user can inherit permissions.
 * Specific permissions can also be set on the user itself, which will override anything gained from the groups.
 * 
 * Note: A group giving a permission will always have priority over a group removing a permission. Groups can not be used to deny permissions given elsewhere.
 * 
 * TODO: Cache permission lookups and results into a HashMap, especially if called often and contains many nodes.
 */
public class UserPermissions {
	private Set<Group> groups;
	private Map<String, PermissionNode> userPermissions; //User specific permissions
	private Map<String, PermissionNode> permissionGraph; //Permission graph including groups and user permissions.
	
	public UserPermissions() {
		
	}
	
	public boolean hasPermission(String permission) {
		return true;
	}
	
	/**
	 * Rebuild the permissions graph, combining group permissions and specific user permissions.
	 * This is to make lookup easier, avoiding having to lookup the permission on each group and the user itself.
	 * 
	 * Whenever the permissions in a group changes, this has to be re-built.
	 */
	private void rebuildPermissions() {
		
	}
}
