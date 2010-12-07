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

package at.ait.dme.yuma.suite.server;

import java.util.Date;

import at.ait.dme.yuma.suite.client.annotation.Annotation.Scope;
import at.ait.dme.yuma.suite.client.annotation.Annotation.Type;
import at.ait.dme.yuma.suite.client.image.annotation.ImageAnnotation;

public class Data {
	
	public static final String ANNOTATION_JSON =
		"[ { \"id\" : \"4sfd4345kvr326546\" , "+ 
		  "\"parent-id\" : \"\" , " +
		  "\"root-id\" : \"\" , " +
		  "\"title\" : \"Ponte 25 de Abril\" , "+
		  "\"text\" : \"The 25 de Abril Bridge is a suspension bridge connecting the city of Lisbon, capital of Portugal, " + 
		  "to the municipality of Almada on the left bank of the Tagus river. It was inaugurated on August 6, 1966 " +
		  "and a train platform was added in 1999. It is often compared to the Golden Gate Bridge in San Francisco, USA, due to " +
		  "their similarities and same construction company. With a total length of 2.277 m, it is the 19th largest " +
		  "suspension bridge in the world. The upper platform carries six car lanes, the lower platform two train tracks. Until " +
		  "1974 the bridge was named Salazar Bridge.\", " +
		  "\"scope\" : \"public\" , "+
		  "\"last-modified\" : 1224043200000 ,"+
		  "\"created\" : 1224043200000 , "+
		  "\"created-by\" : \"rsimon\" , "+
		  "\"fragment\" : \"" +
			"<svg:svg xmlns:svg=\\\"http://www.w3.org/2000/svg\\\" width=\\\"640px\\\" height=\\\"480px\\\" viewbox=\\\"0px 0px 640px 480px\\\"> " +
			  "<svg:defs xmlns:svg=\\\"http://www.w3.org/2000/svg\\\"> " +
				"<svg:symbol xmlns:svg=\\\"http://www.w3.org/2000/svg\\\" id=\\\"Polygon\\\"> " +
				"<svg:polygon xmlns:svg=\\\"http://www.w3.org/2000/svg\\\" " +
				"points=\\\"0,24 45,22 45,32 49,32 49,23 190,20 285,19 193,0 119,17 48,5\\\" stroke=\\\"rgb(229,0,0)\\\" " +
				"stroke-width=\\\"2\\\" fill=\\\"none\\\"> " +
				"</svg:polygon> " +
				"</svg:symbol> " +
			  "</svg:defs>" +
			"</svg:svg>" +
		  "\" , "+
		  "\"type\" : \"image\" , "+
		  "\"object-id\" : \"object-lissabon\", " +
		  "\"tags\" : [ " +
		    "{ \"alt-labels\" : { } , \"label\" : \"Lisbon\" , \"type\" : \"place\" , \"relation\" : { \"namespace\" : \"\" , \"property\" : \"spatiallyContains\" } , \"alt-descriptions\" : { } , \"lang\" : \"en\" , \"uri\" : \"http://www.geonames.org/2267057/\"} ," +
		    "{ \"alt-labels\" : { } , \"label\" : \"Portugal\" , \"type\" : \"place\" , \"relation\" : { \"namespace\" : \"\" , \"property\" : \"spatiallyContains\" } , \"alt-descriptions\" : { } , \"lang\" : \"en\" , \"uri\" : \"http://www.geonames.org/2264397/\"} " +
		  "]" +
		"} ]";
	
	public static ImageAnnotation IMAGE_ANNOTATION;
	
	static {
		IMAGE_ANNOTATION = new ImageAnnotation();
		IMAGE_ANNOTATION.setId("4sfd4345kvr326546");
		IMAGE_ANNOTATION.setParentId(null);
		IMAGE_ANNOTATION.setRootId(null);
		IMAGE_ANNOTATION.setObjectId("object-lissabon");
		IMAGE_ANNOTATION.setCreated(new Date());
		IMAGE_ANNOTATION.setLastModified(new Date());
		IMAGE_ANNOTATION.setCreatedBy("rsimon");
		IMAGE_ANNOTATION.setTitle("Ponte 25 de Abril");
		IMAGE_ANNOTATION.setText("The 25 de Abril Bridge is a suspension bridge connecting the city of Lisbon, " +
				"capital of Portugal, to the municipality of Almada on the left bank of the Tagus " +
				"river. It was inaugurated on August 6, 1966 and a train platform was added in 1999. " +
				"It is often compared to the Golden Gate Bridge in San Francisco, USA, due to " +
				"their similarities and same construction company. With a total length of 2.277 m, " +
				"it is the 19th largest suspension bridge in the world. The upper platform carries " +
				"six car lanes, the lower platform two train tracks. Until 1974 the bridge was named " +
				"Salazar Bridge.");
		IMAGE_ANNOTATION.setType(Type.IMAGE);
		IMAGE_ANNOTATION.setFragment(null);
		IMAGE_ANNOTATION.setScope(Scope.PUBLIC);
	}

}
