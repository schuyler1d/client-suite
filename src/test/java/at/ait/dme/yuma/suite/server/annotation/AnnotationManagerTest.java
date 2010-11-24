package at.ait.dme.yuma.suite.server.annotation;

import java.util.Collection;
import java.util.Properties;

import org.junit.Test;

import at.ait.dme.yuma.suite.client.annotation.Annotation;
import at.ait.dme.yuma.suite.server.Data;
import at.ait.dme.yuma.suite.server.util.Config;


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
		properties.put("annotation.middleware.base.url", "http://localhost:8080/");
		
		AnnotationManager.init(new Config(properties));
		AnnotationManager manager = new AnnotationManager(null);

		/*
		String id = manager.createAnnotation(Data.IMAGE_ANNOTATION).getId();
		System.out.println(id);
		*/
		
		Collection<Annotation> annotations = manager.listAnnotations("test");
		System.out.println(annotations.size());
	}
	
}
