package com.stjerncraft.controlpanel.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.stjerncraft.controlpanel.client.core.ClientCore;

public class Main implements EntryPoint {
	static Logger logger = Logger.getLogger("ClientCoreMain");
	ClientCore clientCore;
	
	@Override
	public void onModuleLoad() {
		clientCore = new ClientCore("localhost:8080");
		
		/*Timer timer = new Timer()
        {
            @Override
            public void run()
            {
        		ScriptInjector.fromUrl("webview/webview.nocache.js").setWindow(ScriptInjector.TOP_WINDOW).setCallback(
      				  new Callback<Void, Exception>() {
      				     public void onFailure(Exception reason) {
      				       Window.alert("Script load failed.");
      				     }
      				    public void onSuccess(Void result) {
      				      //Window.alert("Script load success.");
      				     }
      				  }).inject();
            }
        };

        timer.schedule(1000);*/
		

	}

}
