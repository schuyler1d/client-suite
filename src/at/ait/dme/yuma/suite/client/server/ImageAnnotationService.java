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

package at.ait.dme.yuma.suite.client.server;

import java.util.Collection;
import java.util.Set;

import at.ait.dme.yuma.suite.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.suite.client.server.exception.AnnotationServiceException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * interface of the image annotation service
 * 
 * @author Christian Sadilek
 */
@RemoteServiceRelativePath("imageannotation")
public interface ImageAnnotationService extends RemoteService {
	
	/**
	 * create a new image annotation.
	 * 
	 * @param annotation
	 * @return then new annotation
	 * @throws AnnotationServiceException
	 */
	public ImageAnnotation createAnnotation(ImageAnnotation annotation) 
		throws AnnotationServiceException;

	/**
	 * update an existing annotation.
	 * 
	 * @param annotation
	 * @return the updated annotation
	 * @throws AnnotationServiceException
	 */
	public ImageAnnotation updateAnnotation(ImageAnnotation annotation) 
		throws AnnotationServiceException;

	/**
	 * delete an annotation.
	 * 
	 * @param annotationId
	 * @throws AnnotationServiceException
	 */
	public void deleteAnnotation(String annotationId) 
		throws AnnotationServiceException;

	/**
	 * list annotations of a given object.
	 * 
	 * @param imageUrl (addressable uri to the image)
	 * @return collection of image annotations
	 * @throws AnnotationServiceException
	 */
	public Collection<ImageAnnotation> listAnnotations(String imageUrl) 
		throws AnnotationServiceException;
	
	/**
	 * list annotations of a given object addressing a fragment with a shape 
	 * of one of the given types. 
	 * use the class name as shape type e.g. <code>Ellipse.class.getName()<code>.
	 * 
	 * @param imageUrl
	 * @param shapeTypes
	 * @return collection of image annotations
	 * @throws AnnotationServiceException
	 */
	public Collection<ImageAnnotation> listAnnotations(String imageUrl, 
			Set<String> shapeTypes) throws AnnotationServiceException;
	
	/**
	 * find annotations that match the given search term.
	 * 
	 * @param search term
	 * @return collection of image annotations
	 * @throws AnnotationServiceException
	 */
	public Collection<ImageAnnotation> findAnnotations(String searchTerm) 
		throws AnnotationServiceException;
}
