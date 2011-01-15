package at.ait.dme.yuma.suite;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import at.ait.dme.yuma.suite.hostpages.HostPageImage;
import at.ait.dme.yuma.suite.hostpages.HostPageMap;

public class WicketApplication extends WebApplication {    
    
	public WicketApplication() {
		this.mountBookmarkablePage("image", HostPageImage.class);
		this.mountBookmarkablePage("map", HostPageMap.class);
	}
	
	public Class<? extends Page> getHomePage() {
		return HostPageImage.class;
	}

}
