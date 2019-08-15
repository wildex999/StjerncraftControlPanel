package com.stjerncraft.controlpanel.webview;

import com.stjerncraft.controlpanel.client.api.webview.IPage;
import com.stjerncraft.controlpanel.client.api.webview.IWebView;

import jsinterop.annotations.JsType;

@JsType
public class Page extends Template implements IPage {

	public Page(IWebView webView, String html) {
		super(webView, html);
	}

	@Override
	public void onPageLoaded() {
		
	}

	@Override
	public void onPageUnloading() {
		
	}
	
}
