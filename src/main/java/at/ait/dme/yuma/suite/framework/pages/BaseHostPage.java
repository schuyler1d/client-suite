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

package at.ait.dme.yuma.suite.framework.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

import at.ait.dme.yuma.suite.YUMASuite;
import at.ait.dme.yuma.suite.YUMAWebSession;
import at.ait.dme.yuma.suite.apps.core.client.User;
import at.ait.dme.yuma.suite.framework.auth.MD5Util;

public abstract class BaseHostPage extends WebPage {
	
	public BaseHostPage(String title, String js, final PageParameters params) {
		add(JavascriptPackageResource.getHeaderContribution(js));
		add(new Label("title", title));
		
		YUMAWebSession.get().setUser(getUser(params));

		String baseUrl = YUMASuite.getBaseUrl(getWebRequestCycle().getWebRequest().getHttpServletRequest());		
		String dictionary = "\nvar parameters = {\n" +
							"  objectURI: \"" + params.getString("objectURI") + "\",\n" +
							"  baseURL:   \"" + baseUrl + "\", \n" +
							"}\n";
		add(new Label("dictionary", dictionary).setEscapeModelStrings(false));
    }; 
    
    private User getUser(PageParameters params) {
		String username = params.getString("username");
		User user = new User(username);

		String email = params.getString("email");
		if (email != null)
			user.setGravatarHash(MD5Util.md5Hex(email));
		
    	return user;
    }
    
}
