package com.stjerncraft.controlpanel.modules.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.resources.client.DataResource.MimeType;

public interface LoginResources extends ClientBundle {
	public static final LoginResources DATA = GWT.create(LoginResources.class);
	
	@Source("LoginPage.html")
	public TextResource loginPageHtml();
	
	@MimeType("text/css")
	@Source("LoginPage.css")
	public DataResource loginPageCss();
	
	@Source("LoginWidget.html")
	public TextResource loginWidgetHtml();
}
