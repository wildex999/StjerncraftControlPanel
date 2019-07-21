package com.stjerncraft.controlpanel.common;

import com.stjerncraft.controlpanel.api.annotation.DataObject;
import com.stjerncraft.controlpanel.common.data.ModuleInfo;

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
	public ModuleInfo module; //TODO: Allow Array for batching large changes?
	
	public ModuleEvent() {}
	
	public ModuleEvent(Action action, ModuleInfo module) {
		this.action = action;
		this.module = module;
	}
}
