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

package at.ait.dme.yuma.suite.pages;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

public abstract class BaseHostPage extends WebPage {
	
	public BaseHostPage(String title, String js, final PageParameters parameters) {
		add(new Label("title", title));	
		add(JavascriptPackageResource.getHeaderContribution(js));

		String user = parameters.getString("user");
		String objectURI = parameters.getString("objectURI");
		
		HttpServletRequest request = getWebRequestCycle().getWebRequest().getHttpServletRequest();
		String baseURL = 
			request.getScheme()+ "://" + 
			request.getServerName()+ ":" + request.getServerPort();
		
		if (request.getContextPath() != null && request.getContextPath().length()!=0) {
			baseURL += request.getContextPath();
		} else if (request.getPathInfo() != null && request.getPathInfo().length()!=0) {
			int lastSlashPos = request.getPathInfo().lastIndexOf("/");
			if(lastSlashPos>0)
				baseURL += request.getPathInfo().substring(0,lastSlashPos);
		}
		baseURL+="/";
		
		String dictionary = "\nvar parameters = {\n" +
							"  objectURI: \"" + objectURI + "\",\n" +
							"  baseURL:   \"" + baseURL + "\", \n" +
							"  user:      \"" + user + "\"\n" + 
							"}\n";
		
		add(new Label("dictionary", dictionary).setEscapeModelStrings(false));
    }; 

}
