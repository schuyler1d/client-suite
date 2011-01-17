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

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

import at.ait.dme.yuma.suite.pages.examples.ImageExamplePage;
import at.ait.dme.yuma.suite.pages.examples.MapExamplePage;
import at.ait.dme.yuma.suite.pages.hostpages.ImageHostPage;
import at.ait.dme.yuma.suite.pages.hostpages.MapHostPage;

public class AnnotationSuite extends WebApplication {    
    
	public AnnotationSuite() {
		this.mountBookmarkablePage("image", ImageHostPage.class);
		this.mountBookmarkablePage("image/examples", ImageExamplePage.class);
		
		this.mountBookmarkablePage("map", MapHostPage.class);
		this.mountBookmarkablePage("map/examples", MapExamplePage.class);
	}
	
	public Class<? extends Page> getHomePage() {
		return ImageHostPage.class;
	}

}
