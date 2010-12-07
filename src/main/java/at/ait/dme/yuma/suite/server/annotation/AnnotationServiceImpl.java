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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import at.ait.dme.yuma.suite.client.annotation.Annotation;
import at.ait.dme.yuma.suite.client.image.shape.GeoPoint;
import at.ait.dme.yuma.suite.client.server.AnnotationService;
import at.ait.dme.yuma.suite.client.server.exception.AnnotationServiceException;
import at.ait.dme.yuma.suite.server.georeferencer.GeoreferencerUtils;
import at.ait.dme.yuma.suite.server.map.transformation.ControlPoint;
import at.ait.dme.yuma.suite.server.util.Config;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * the implementation of the image annotation service
 * 
 * @author Christian Sadilek
 */

public class AnnotationServiceImpl extends RemoteServiceServlet implements AnnotationService {
	
	private static final long serialVersionUID = 3081954210900384269L;

	/**
	 * Reads the configuration from the servlet context. In case it's not found
	 * there, it tries to read it from the property file.
	 */
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
	    super.init(servletConfig);	
	    Config config = new Config(servletConfig, 
				getClass().getResourceAsStream("annotation-service.properties"));
	    
	    AnnotationManager.init(config);
	}

	@Override
	public Annotation createAnnotation(Annotation annotation) 
			throws AnnotationServiceException {
		
		return new AnnotationManager(getThreadLocalRequest()).createAnnotation(annotation);
	}

	@Override
	public Annotation updateAnnotation(Annotation annotation) 
			throws AnnotationServiceException {

		return new AnnotationManager(getThreadLocalRequest()).updateAnnotation(annotation);
	}
	
	@Override
	public void deleteAnnotation(String annotationId)
		throws AnnotationServiceException {
		
		new AnnotationManager(getThreadLocalRequest()).deleteAnnotation(annotationId);
	}
	
	@Override
	public Collection<Annotation> listAnnotations(String objectId) 
			throws AnnotationServiceException {	
		Collection<Annotation> annotations = 
			new AnnotationManager(getThreadLocalRequest()).listAnnotations(objectId);
		
		// TODO this mess needs to be cleaned up...
		if (objectId.startsWith("http://georeferencer")) {
			@SuppressWarnings("unchecked")
			List<ControlPoint> controlPoints = (List<ControlPoint>) getThreadLocalRequest().getSession().getAttribute("controlPoints");
			for (ControlPoint p : controlPoints) {
				annotations.add(GeoreferencerUtils.toAnnotation(p));
			}
		}
		
		return annotations;
	}
	
	@Override
	public Collection<Annotation> listAnnotations(String objectId, Set<String> shapeTypes)
		throws AnnotationServiceException {		
		Collection<Annotation> annotations = 
			new AnnotationManager(getThreadLocalRequest()).listAnnotations(objectId, shapeTypes);
		
		if (objectId.startsWith("http://georeferencer")) {
			if (shapeTypes.contains(GeoPoint.class.getName())) {
				@SuppressWarnings("unchecked")
				List<ControlPoint> controlPoints = (List<ControlPoint>) getThreadLocalRequest().getSession().getAttribute("controlPoints");
				
				for (ControlPoint p : controlPoints) {
					annotations.add(GeoreferencerUtils.toAnnotation(p));
				}
			}
		}

		return annotations;
	}

}
