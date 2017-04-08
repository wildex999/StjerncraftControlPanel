package com.stjerncraft.controlpanel.core.module;

/**
 * Event sent out when something changes with a Module
 */
public class ModuleEvent {
	public enum Action {
		Added, //New Module has been added
		Removed, //Existing Module has been removed
		Activated, //New Module has been activated
		Deactivated, //Active Module has been deactivated
		Updated
	}
}
