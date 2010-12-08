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

package at.ait.dme.yuma.suite.server.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import at.ait.dme.yuma.suite.client.annotation.Annotation;
import at.ait.dme.yuma.suite.client.image.ImageRect;
import at.ait.dme.yuma.suite.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.suite.client.image.annotation.ImageFragment;
import at.ait.dme.yuma.suite.client.image.shape.GeoPoint;
import at.ait.dme.yuma.suite.client.map.annotation.WGS84Coordinate;
import at.ait.dme.yuma.suite.client.map.annotation.XYCoordinate;
import at.ait.dme.yuma.suite.server.annotation.AnnotationManager;
import at.ait.dme.yuma.suite.server.image.ImageTilesetGenerator;
import at.ait.dme.yuma.suite.server.map.transformation.ControlPoint;
import at.ait.dme.yuma.suite.server.map.transformation.ControlPointManager;
import at.ait.dme.yuma.suite.server.util.Config;

public class ControlPointManagerTest {

	private class ControlPointComparator implements Comparator<ControlPoint> {
		@Override
		public int compare(ControlPoint cp1, ControlPoint cp2) {
			return cp1.getName().compareTo(cp2.getName());
		}
	}
	
	@Before
	public void setUp() throws Exception {
		Properties props = new Properties();
		props.put("annotation.middleware.base.url","http://localhost:8080");
		
		Config config = new Config(props);
		AnnotationManager.init(config);	
	}
	
	@Ignore @Test 
	public void testImportControlPoints() throws Exception {
		String controlPointsRootPath = "/Users/csa/Projects/TELplus/image-annotation-frontend/war/controlpoints/";
		
		String[] imageUrls = {
				"http://upload.wikimedia.org/wikipedia/commons/4/49/Hirschvogel_Map_Austria.jpg",
				"http://www.nacis.org/data/world_map/map1/map1_type.jpg",
				"http://planets-project.arcs.ac.at/files/ct000342.tif",
				"http://upload.wikimedia.org/wikipedia/commons/4/40/MacedonEmpire.jpg",
				"http://www.emersonkent.com/images/europe_14th_century.jpg",
				"http://planets-project.arcs.ac.at/files/E21.035.jpg",
				"http://planets-project.arcs.ac.at/files/FKBQ.11.3.jpg",
				"http://upload.wikimedia.org/wikipedia/commons/e/ec/Waldseemuller_map.jpg"
		};
		
		for(String imageUrl : imageUrls) testImportControlPoint(imageUrl, controlPointsRootPath);
	}
	
	public void testImportControlPoint(String imageUrl, String path) throws Exception {	
		Set<String> shapeTypes = new HashSet<String>();
		shapeTypes.add(GeoPoint.class.getName());
		
		// first delete all existing control points of this map
		AnnotationManager iam = new AnnotationManager(null);
		Collection<Annotation> annotations = iam.listAnnotations(imageUrl, shapeTypes);
		for(Annotation annotation : annotations) {
			iam.deleteAnnotation(annotation.getId());
		}
		
		// read control points from file
		List<ControlPoint> controlPoints = 
			readControlPointFile(path+ImageTilesetGenerator.createPathForImage(imageUrl));
		
		// store them as annotations
		for(ControlPoint cp : controlPoints) {
			ImageAnnotation annotation = new ImageAnnotation();
			annotation.setObjectId(imageUrl);
			annotation.setCreatedBy("importer");
			ImageFragment fragment = new ImageFragment(new ImageRect(),
					new GeoPoint(cp.getName(),cp.getXY().x,cp.getXY().y,cp.getLatLon().lat,cp.getLatLon().lon));
			annotation.setFragment(fragment);
			annotation.setTitle("controlPoint for:"+cp.getName());
			iam.createAnnotation(annotation);
			
			System.out.println("imported control point for:"+cp.getName() + " on " + imageUrl);
		}
		
		List<ControlPoint> importedControlPoints = new ControlPointManager(null, imageUrl).getControlPoints();
		
		Collections.sort(importedControlPoints, new ControlPointComparator());
		Collections.sort(controlPoints, new ControlPointComparator());
		assertEquals(importedControlPoints,controlPoints);
	}
	
	private List<ControlPoint> readControlPointFile(String controlPointFile) throws Exception {
		List<ControlPoint> controlPoints = new ArrayList<ControlPoint>();
		
		// Read control points from file
		InputStream is = new FileInputStream(controlPointFile+".txt");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		String[] csv;
		while ((line = br.readLine()) != null) {
			csv = line.split(";");
			if (csv.length == 5) {
				controlPoints.add(new ControlPoint(
						csv[0], // name
						new XYCoordinate(Integer.parseInt(csv[1]), Integer.parseInt(csv[2])), // XY
						new WGS84Coordinate(Double.parseDouble(csv[3]), Double.parseDouble(csv[4])) // LatLon
				));
			} 
		}
		return controlPoints;
	}
}
