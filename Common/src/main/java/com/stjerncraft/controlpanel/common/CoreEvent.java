package com.stjerncraft.controlpanel.common;

public class CoreEvent {
	public enum Action {
		Added, //New Module has been added
		Removed, //Existing Module has been removed
		Activated, //New Module has been activated
		Deactivated, //Active Module has been deactivated
		Updated
	}
}
