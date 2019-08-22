package com.stjerncraft.controlpanel.modules.loading;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface LoadingProgressResources extends ClientBundle {
	public static final LoadingProgressResources DATA = GWT.create(LoadingProgressResources.class);
	
	@Source("LoadingProgress.html")
	public TextResource loadingProgressWidgetHtml();
	
	@Source("LoadingProgress.js")
	public TextResource loadingProgressWidgetJs();
}
