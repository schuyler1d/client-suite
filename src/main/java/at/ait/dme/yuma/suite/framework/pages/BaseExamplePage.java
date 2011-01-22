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
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

import at.ait.dme.yuma.suite.YUMASuite;

public abstract class BaseExamplePage extends WebPage {

	protected String devModeParams = "";
	
	public BaseExamplePage(String title, final PageParameters parameters) {
		add(new Label("title", title));
		add(new Label("header", title));
		
		// When in dev mode, append user name, email and gwt.codesvr attribute
		if (YUMASuite.isDevMode()) {
			devModeParams = "&username=rsimon&email=rainer@no5.at";
			if (parameters.getString("gwt.codesvr") != null)
				devModeParams += "&gwt.codesvr=" + parameters.getString("gwt.codesvr");
		}
	}
	
}
