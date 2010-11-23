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

import at.ait.dme.yuma.suite.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.suite.client.image.shape.GeoPoint;
import at.ait.dme.yuma.suite.client.server.ImageAnnotationService;
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

public class ImageAnnotationServiceImpl extends RemoteServiceServlet 
	implements ImageAnnotationService {
	
	private static final long serialVersionUID = 7979737020415861621L;
	
	/**
	 * reads the configuration from the servlet context. in case it's not found
	 * there it tries to read it from the property file.
	 */
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
	    super.init(servletConfig);	
	    Config config = new Config(servletConfig, 
				getClass().getResourceAsStream("image-annotation-service.properties"));
	    
	    ImageAnnotationManager.init(config);
	}

	@Override
	public ImageAnnotation createAnnotation(ImageAnnotation annotation) 
			throws AnnotationServiceException {
		
		// set the mime type of the annotated object
		// annotation.setMimeType(getServletContext().getMimeType(annotation.getObjectId()));

		return new ImageAnnotationManager(getThreadLocalRequest()).createAnnotation(annotation);
	}

	@Override
	public ImageAnnotation updateAnnotation(ImageAnnotation annotation) 
			throws AnnotationServiceException {

		return new ImageAnnotationManager(getThreadLocalRequest()).updateAnnotation(annotation);
	}
	
	@Override
	public void deleteAnnotation(String annotationId) throws AnnotationServiceException {
		
		new ImageAnnotationManager(getThreadLocalRequest()).deleteAnnotation(annotationId);
	}
	
	@Override
	public Collection<ImageAnnotation> listAnnotations(String imageUrl) 
			throws AnnotationServiceException {
		
		Collection<ImageAnnotation> annotations = new ImageAnnotationManager(getThreadLocalRequest()).listAnnotations(imageUrl);
		
		if (imageUrl.startsWith("http://georeferencer")) {
			@SuppressWarnings("unchecked")
			List<ControlPoint> controlPoints = (List<ControlPoint>) getThreadLocalRequest().getSession().getAttribute("controlPoints");
			for (ControlPoint p : controlPoints) {
				annotations.add(GeoreferencerUtils.toAnnotation(p));
			}
		}
		
		return annotations;
		
		
	}
	
	@Override
	public Collection<ImageAnnotation> listAnnotations(String imageUrl, 
			Set<String> shapeTypes) throws AnnotationServiceException {

		Collection<ImageAnnotation> annotations = new ImageAnnotationManager(getThreadLocalRequest()).listAnnotations(imageUrl, shapeTypes);
		
		if (imageUrl.startsWith("http://georeferencer")) {
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
	
	@Override
	public Collection<ImageAnnotation> findAnnotations(String searchTerm) 
			throws AnnotationServiceException {
	
		return new ImageAnnotationManager(getThreadLocalRequest()).findAnnotations(searchTerm);
	}
}
