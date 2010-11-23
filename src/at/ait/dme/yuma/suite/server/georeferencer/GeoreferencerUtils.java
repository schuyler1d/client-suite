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

package at.ait.dme.yuma.suite.server.georeferencer;

import java.util.ArrayList;

import at.ait.dme.yuma.suite.client.annotation.SemanticTag;
import at.ait.dme.yuma.suite.client.image.ImageFragment;
import at.ait.dme.yuma.suite.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.suite.client.image.annotation.ImageAnnotation.Scope;
import at.ait.dme.yuma.suite.client.image.shape.GeoPoint;
import at.ait.dme.yuma.suite.server.map.transformation.ControlPoint;

public class GeoreferencerUtils {
	
	public static ImageAnnotation toAnnotation(ControlPoint p) {
		ImageAnnotation annotation = new ImageAnnotation( 
				"",
				"",
				"",
				null,
				null,
				"georeferencer.org",
				p.getName(),
				p.getName(),
				Scope.PUBLIC,
				new ArrayList<SemanticTag>()
		);
		
		annotation.setFragment(new ImageFragment(
			new GeoPoint(
				p.getName(),
				p.getXY().x, 
				p.getXY().y, 
				p.getLatLon().lat,
				p.getLatLon().lon
			)
		));

		return annotation;
	}

}
