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

package at.ait.dme.yuma.suite.server.annotation;

import java.util.ArrayList;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import at.ait.dme.yuma.suite.client.annotation.Annotation;
import at.ait.dme.yuma.suite.client.annotation.Annotation.Scope;
import at.ait.dme.yuma.suite.client.annotation.Annotation.Type;
import at.ait.dme.yuma.suite.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.suite.server.Data;

public class JSONAnnotationBuilderTest {
	
	@Test
	public void testJSONParsing() {
		ArrayList<Annotation> annotations = JSONAnnotationBuilder.toAnnotations(Data.ANNOTATION_JSON);
		assertTrue(annotations.size() == 1);
		
		ImageAnnotation a = (ImageAnnotation) annotations.get(0);
		assertEquals("4sfd4345kvr326546", a.getId());
		assertEquals(null, a.getParentId());
		assertEquals(null, a.getRootId());
		assertEquals("http://upload.wikimedia.org/wikipedia/commons/7/77/Lissabon.jpg", a.getObjectId());
		// created 
		// lastModified
		assertEquals("Ponte 25 de Abril", a.getTitle());
		assertEquals("The 25 de Abril Bridge is a suspension bridge connecting the city of Lisbon, " +
				"capital of Portugal, to the municipality of Almada on the left bank of the Tagus " +
				"river. It was inaugurated on August 6, 1966 and a train platform was added in 1999. " +
				"It is often compared to the Golden Gate Bridge in San Francisco, USA, due to " +
				"their similarities and same construction company. With a total length of 2.277 m, " +
				"it is the 19th largest suspension bridge in the world. The upper platform carries " +
				"six car lanes, the lower platform two train tracks. Until 1974 the bridge was named " +
				"Salazar Bridge.", a.getText());
		assertEquals(Type.IMAGE, a.getType());
		// fragment
		assertEquals(Scope.PUBLIC, a.getScope());
		// tags
		// replies
	}
	
	@Test
	public void testJSONSerialization() {
		String serialized = JSONAnnotationBuilder.toJSON(Data.IMAGE_ANNOTATION).toString();
		System.out.println(serialized);
	}

}
