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

public class MapHostPage extends BaseHostPage {
	
	public MapHostPage(final PageParameters parameters) {
		super("YUMA Map", "yuma.map/yuma.map.nocache.js", parameters);	
		
		// Add required JS libaries
		add(JavascriptPackageResource.getHeaderContribution("js/raphael/raphael-min.js"));
		add(JavascriptPackageResource.getHeaderContribution("js/openlayers/OpenLayers.js"));
		add(JavascriptPackageResource.getHeaderContribution("js/openlayers/OpenLayers.js"));
		add(JavascriptPackageResource.getHeaderContribution("js/ait-jsutils.js"));
	}

}