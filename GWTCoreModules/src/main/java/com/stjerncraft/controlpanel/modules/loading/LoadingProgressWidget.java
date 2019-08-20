package com.stjerncraft.controlpanel.modules.loading;

import java.util.logging.Logger;

import com.stjerncraft.controlpanel.client.api.IClientModuleListener;
import com.stjerncraft.controlpanel.client.api.IClientModuleManager;
import com.stjerncraft.controlpanel.client.api.module.IClientModule;
import com.stjerncraft.controlpanel.client.api.webview.IWidgetInstance;
import com.stjerncraft.controlpanel.client.api.webview.base.BaseWidget;
import com.stjerncraft.controlpanel.client.api.webview.base.StaticWidgetInstance;
import com.stjerncraft.controlpanel.client.api.webview.socket.ISocketInstance;
import com.stjerncraft.controlpanel.common.data.IModuleInfo;

import jsinterop.annotations.JsType;

/**
 * Indicate loading progress for modules
 */
@JsType
public class LoadingProgressWidget extends BaseWidget {
	static Logger logger = Logger.getLogger("LoadingProgressWidget");
	
	@JsType
	public class LoadingProgressWidgetInstance extends StaticWidgetInstance {
		IClientModuleManager moduleManager;
		IClientModuleListener moduleListener = new IClientModuleListener() {
			
			@Override
			public void onModuleLoading(String module) {
				updateProgress();
			}
			
			@Override
			public void onModuleLoaded(IClientModule module) {
				updateProgress();
			}
			
			@Override
			public void onModuleDeactivated(IClientModule module) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onModuleActivated(IClientModule module) {
				// TODO Auto-generated method stub
				
			}
		};
	
		public LoadingProgressWidgetInstance(IClientModuleManager moduleManager) {
			super(LoadingProgressResources.DATA.loadingProgressWidgetHtml().getText());
			
			this.moduleManager = moduleManager;
		}
		
		@Override
		public void onInsert(String divId) {
			super.onInsert(divId);
			
			moduleManager.addModuleListener(moduleListener);
			updateProgress();
		}
		
		@Override
		public void onRemove() {
			super.onRemove();
			
			moduleManager.removeModuleListener(moduleListener);
		}
		
		private void updateProgress() {
			IModuleInfo[] loadingModules = moduleManager.getLoadingModules();
			int loaded = moduleManager.getLoadedModules().length;
			int loading = moduleManager.getLoadingModules().length;
			
			String currentModule;
			if(loading > 0)
				currentModule = loadingModules[0].getDescriptiveName();
			else
				currentModule = "";
			
			setProgress(divId, loaded, loaded+loading, currentModule);
		}
		
		private native void setProgress(String divId, int loadedModules, int countModules, String latestModule) /*-{
			var progress = loadedModules / countModules * 100.0;
			
			var text;
			if(loadedModules == countModules)
				text = "All Modules loaded!";
			else
				text = "Loading Modules: " + latestModule + "(" + loadedModules + "/" + countModules + ")";
			
			$wnd.$("#" + divId + " .progress-bar").attr("aria-valuenow", progress).css({"width": progress + "%"}).text(text);
		}-*/;
	}
	
	IClientModuleManager moduleManager;
	
	public LoadingProgressWidget(IClientModule module, IClientModuleManager moduleManager) {
		super(module);
		this.moduleManager = moduleManager;
	}

	@Override
	public IWidgetInstance createInstance(ISocketInstance socket) {
		return new LoadingProgressWidgetInstance(moduleManager);
	}
}
