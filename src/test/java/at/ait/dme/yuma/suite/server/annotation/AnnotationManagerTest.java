package at.ait.dme.yuma.suite.server.annotation;

import java.util.Collection;
import java.util.Properties;

import org.junit.Test;

import at.ait.dme.yuma.suite.client.annotation.Annotation;

import at.ait.dme.yuma.suite.server.Data;
import at.ait.dme.yuma.suite.server.util.Config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Performs end-to-end annotation CRUD operations on the Annotation Server
 * through the Annotation Manager. The YUMA Annotation Server (plus database
 * backend) must be running, or else this test will fail!
 * 
 * @author Rainer Simon
 */
public class AnnotationManagerTest {

	@Test
	public void testAnnotationCRUD() throws Exception {
		Properties properties = new Properties();
		properties.put("annotation.server.base.url", "http://localhost:8080/yuma-server");
		
		AnnotationManager.init(new Config(properties));
		AnnotationManager manager = new AnnotationManager(null);
		
		// Create
		Annotation a = manager.createAnnotation(Data.newAnnotation());
		String id = a.getId();
		assertNotNull(id);
		System.out.println("Created: " + id);
		
		// Read
		Collection<Annotation> tree = manager.listAnnotations("object-lissabon");
		assertTrue(tree.size() > 0);
		
		// Delete
		manager.deleteAnnotation(id);
	}
	
}
