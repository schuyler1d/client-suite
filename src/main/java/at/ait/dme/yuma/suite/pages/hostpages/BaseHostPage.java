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

package at.ait.dme.yuma.suite.pages.hostpages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

public abstract class BaseHostPage extends WebPage {
	
	protected String user;
	
	protected String objectURI;
	
	public BaseHostPage(String title, String js, final PageParameters parameters) {
		add(new Label("title", title));	
		add(JavascriptPackageResource.getHeaderContribution(js));

		user = parameters.getString("user");
		objectURI = parameters.getString("objectURI");
		
		String dictionary = "var parameters = {\n" +
							"objectURL:	\"" + objectURI + "\",\n" +
							"imageURL:	\"" + objectURI + "\",\n" +
							"user:	\"" + user + "\" }\n";
		
		add(new Label("dictionary", dictionary).setEscapeModelStrings(false));
    }; 

}
