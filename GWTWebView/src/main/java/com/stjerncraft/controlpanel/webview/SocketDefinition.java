package com.stjerncraft.controlpanel.webview;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.stjerncraft.controlpanel.client.api.webview.IWidget;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketDefinition;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;

public class SocketDefinition implements ISocketDefinition {

	private String name;
	private Set<IWidget> widgets;
	private Set<ISocketInstance> instances;
	
	public SocketDefinition(String name) {
		this.name = name;
		widgets = new HashSet<IWidget>();
		instances = new HashSet<ISocketInstance>();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void addWidget(IWidget widget) {
		widgets.add(widget);
	}
	
	@Override
	public void removeWidget(IWidget widget) {
		//TODO: Force-update all socket instances using this Widget
	}
	
	@Override
	public Collection<IWidget> getWidgets() {
		return widgets;
	}
	
	@Override
	public void addInstance(ISocketInstance instance) {
		instances.add(instance);
	}

	@Override
	public void removeInstance(ISocketInstance instance) {
		instances.remove(instance);
	}

	@Override
	public Collection<ISocketInstance> getSocketInstances() {
		return instances;
	}

}
