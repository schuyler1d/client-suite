/*
 * Copyright 2008-2010 Austrian Institute of Technology
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package at.ait.dme.yuma.suite;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;

import at.ait.dme.yuma.suite.framework.pages.image.ImageExamplePage;
import at.ait.dme.yuma.suite.framework.pages.image.ImageHostPage;
import at.ait.dme.yuma.suite.framework.pages.map.MapExamplePage;
import at.ait.dme.yuma.suite.framework.pages.map.MapHostPage;

public class YUMASuite extends WebApplication {    
    
	private static final String HTTP = "http://";
	
	public YUMASuite() {
		this.mountBookmarkablePage("image", ImageHostPage.class);
		this.mountBookmarkablePage("image/examples", ImageExamplePage.class);
		
		this.mountBookmarkablePage("map", MapHostPage.class);
		this.mountBookmarkablePage("map/examples", MapExamplePage.class);
	}

	@Override
	public Class<? extends Page> getHomePage() {
		return ImageHostPage.class;
	}
	
	@Override
	public final Session newSession(Request request, Response response) {
		return new YUMAWebSession(request);
	}
	
	public static boolean isDevMode() {
		return YUMASuite.get()
			.getConfigurationType().equals(WebApplication.DEVELOPMENT);
	}
	
    public static String getBaseUrl(HttpServletRequest request) {
		String baseURL = HTTP + request.getServerName();
		
		int serverPort = request.getServerPort();
		if (serverPort != 80)
			baseURL += ":" + serverPort;
		
		if (request.getContextPath() != null && request.getContextPath().length() > 0) {
			baseURL += request.getContextPath();
		} else if (request.getPathInfo() != null && request.getPathInfo().length()> 0) {
			int lastSlash = request.getPathInfo().lastIndexOf("/");
			if (lastSlash > 0)
				baseURL += request.getPathInfo().substring(0, lastSlash);
		}
		return baseURL + "/";
    }

}
