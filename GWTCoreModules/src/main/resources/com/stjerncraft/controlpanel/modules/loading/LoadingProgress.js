var LoadingProgressJs = {
	setProgress: function(divId, loadedModules, countModules, latestModule) {
		var progress = loadedModules / countModules * 100.0;
		
		var text;
		if(loadedModules == countModules)
			text = "All Modules loaded!";
		else
			text = "Loading Modules: " + latestModule + "(" + loadedModules + "/" + countModules + ")";
		
		$wnd.$("#" + divId + " .progress-bar").attr("aria-valuenow", progress).css({"width": progress + "%"}).text(text);
	}
}