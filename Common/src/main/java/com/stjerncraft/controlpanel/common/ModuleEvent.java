package com.stjerncraft.controlpanel.common;

import com.stjerncraft.controlpanel.api.annotation.DataObject;

/**
 * Event sent out when something changes with a Module
 */
@DataObject
public class ModuleEvent {
	public enum Action {
		Added, //New Module has been added
		Removed, //Existing Module has been removed
		Activated, //New Module has been activated
		Deactivated, //Active Module has been deactivated
		Updated
	}

	public Action action;
}
