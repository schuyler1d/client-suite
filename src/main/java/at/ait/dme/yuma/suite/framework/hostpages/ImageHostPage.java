package at.ait.dme.yuma.suite.framework.hostpages;

import org.apache.wicket.PageParameters;

public class ImageHostPage extends BaseHostPage {
	
	public ImageHostPage(final PageParameters parameters) {
		super("YUMA Image", "yumaImage/yumaImage.nocache.js", parameters);
	}

}
