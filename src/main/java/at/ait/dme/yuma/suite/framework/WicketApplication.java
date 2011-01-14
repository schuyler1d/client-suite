package at.ait.dme.yuma.suite.framework;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import at.ait.dme.yuma.suite.framework.hostpages.ImageHostPage;
import at.ait.dme.yuma.suite.framework.hostpages.MapHostPage;

public class WicketApplication extends WebApplication {    
    
	public WicketApplication() {
		this.mountBookmarkablePage("image", ImageHostPage.class);
		this.mountBookmarkablePage("map", MapHostPage.class);
	}
	
	public Class<? extends Page> getHomePage() {
		return ImageHostPage.class;
	}

}
