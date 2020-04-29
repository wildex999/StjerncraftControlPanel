package com.stjerncraft.controlpanel.core.permission;

import java.util.HashMap;
import java.util.Map;

/**
 * A Permission Node, which is part of a Permission Graph.
 */
public class PermissionNode {
	public enum Type {
		Add,
		Remove
	}
	
	private String name;
	private Type type;
	private boolean isValid = false;
	private Map<String, PermissionNode> nodes;
	
	public PermissionNode(String name, Type type) {
		this.name = name;
		this.type = type;
		
		nodes = new HashMap<String, PermissionNode>();
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public PermissionNode getPermission(String nodeName) {
		return nodes.get(nodeName);
	}
	
	public void addPermission(PermissionNode node) {
		nodes.put(node.getName(), node);
	}
	
	/**
	 * Invalidate this Permission node.
	 * This should be called whenever the node is removed from it's parent node.
	 */
	public void invalidate() {
		isValid = false;
	}

}
