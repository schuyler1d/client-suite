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
import java.util.Collection;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.util.HttpResponseCodes;

import com.reardencommerce.kernel.collections.shared.evictable.ConcurrentLinkedHashMap;
import com.reardencommerce.kernel.collections.shared.evictable.ConcurrentLinkedHashMap.EvictionPolicy;

import at.ait.dme.yuma.suite.client.image.ImageFragment;
import at.ait.dme.yuma.suite.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.suite.client.server.ImageAnnotationService;
import at.ait.dme.yuma.suite.client.server.exception.AnnotationServiceException;
import at.ait.dme.yuma.suite.server.annotation.builder.RdfXmlAnnotationBuilder;
import at.ait.dme.yuma.suite.server.util.Config;
import at.ait.dme.yuma.suite.server.util.URLEncoder;

/**
 * This class contains all actions on annotations.
 * 
 * @author Christian Sadilek
 */
public class ImageAnnotationManager implements ImageAnnotationService {
	private static Logger logger = Logger.getLogger(ImageAnnotationManager.class);

	private static final String ANNOTATION_SERVICE_URL_PROPERTY = "annotation.middleware.base.url";
	private static final String FAILED_TO_PARSE_ANNOTATION = "failed to parse anntotation";
	
	private static String annotationMiddlewareBaseUrl = null;
	private HttpServletRequest clientRequest = null;
	
	// we will use a simple cache here for now.
	// TODO don't use this on a cluster
	private static final int MAX_SIZE_ANNOTATION_CACHE = 20;
	private static ConcurrentLinkedHashMap<String, Collection<ImageAnnotation>> annotationCache = 
		new ConcurrentLinkedHashMap<String, Collection<ImageAnnotation>>(EvictionPolicy.LRU, 
				MAX_SIZE_ANNOTATION_CACHE);

	public static void init(Config config) throws ServletException {
		annotationMiddlewareBaseUrl = config.getStringProperty(ANNOTATION_SERVICE_URL_PROPERTY);
	}

	public ImageAnnotationManager(HttpServletRequest clientRequest) {
		this.clientRequest = clientRequest;
	}
	
	@Override
	public ImageAnnotation createAnnotation(ImageAnnotation annotation)
			throws AnnotationServiceException {

		ImageAnnotation storedAnnotation = null;
		try {
			// call the annotation middleware
			ClientResponse<String> response = getAnnotationService().createAnnotation(
					RdfXmlAnnotationBuilder.toRdfXml(annotation));

			// check the response
			if (response.getStatus() != HttpResponseCodes.SC_CREATED)
				throw new AnnotationServiceException(response.getStatus());

			// parse the response
			Collection<ImageAnnotation> annotations = 
				RdfXmlAnnotationBuilder.fromRdfXml(response.getEntity());
			if (!annotations.isEmpty())
				storedAnnotation = (ImageAnnotation) annotations.iterator().next();
			
			//remove from cache
			annotationCache.remove(annotation.getObjectId());
		} catch (AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		}
		return storedAnnotation;
	}
	
	@Override
	public ImageAnnotation updateAnnotation(ImageAnnotation annotation) 
			throws AnnotationServiceException {
		
		ImageAnnotation storedAnnotation = null;
		try {					
			// call the annotation middleware			
			ClientResponse<String> response = getAnnotationService().
				updateAnnotation(URLEncoder.encode(annotation.getId()),
						RdfXmlAnnotationBuilder.toRdfXml(annotation));
			
			// check the response			
			if(response.getStatus()!=HttpResponseCodes.SC_OK)
				throw new AnnotationServiceException(response.getStatus());
		
			// parse the response			
			Collection<ImageAnnotation> annotations = 
				RdfXmlAnnotationBuilder.fromRdfXml(response.getEntity());
			if(!annotations.isEmpty()) 
				storedAnnotation=(ImageAnnotation)annotations.iterator().next();
			
			//remove from cache
			annotationCache.remove(annotation.getObjectId());
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		} 
		return storedAnnotation;
	}
	
	@Override
	public void deleteAnnotation(String annotationId) throws AnnotationServiceException {
		try {					
			// call the annotation middleware			
			ClientResponse<String> response = getAnnotationService().
				deleteAnnotation(URLEncoder.encode(annotationId));
			
			// check the response			
			if(response.getStatus()!=HttpResponseCodes.SC_OK&&
					response.getStatus()!=HttpResponseCodes.SC_NO_CONTENT)
				throw new AnnotationServiceException(response.getStatus());
			
			//clear cache
			annotationCache.clear();
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		} 		
	}
	
	@Override
	public Collection<ImageAnnotation> listAnnotations(String imageUrl) 
			throws AnnotationServiceException {
		
		Collection<ImageAnnotation> annotations = null;			

		try {
			if((annotations=annotationCache.get(imageUrl))==null) {
				// call the annotation middleware			
				ClientResponse<String> response=getAnnotationService().
					listAnnotations(URLEncoder.encode(imageUrl));	
				
				// check the response
				if(response.getStatus()!=HttpResponseCodes.SC_OK)
					throw new AnnotationServiceException(response.getStatus());
				
				// parse the response			
				annotations = RdfXmlAnnotationBuilder.fromRdfXml(response.getEntity());
				
				// cache the response
				annotationCache.putIfAbsent(imageUrl, annotations);
			}
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;			
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		}
		
		return annotations;
	}
	
	@Override
	public Collection<ImageAnnotation> listAnnotations(String imageUrl,
			Set<String> shapeTypes) throws AnnotationServiceException {
		
		Collection<ImageAnnotation> annotations = new ArrayList<ImageAnnotation>(); 
		
		// list all annotations of this object and keep only those that have
		// a fragment with a shape of one of the given types
		for(ImageAnnotation annotation : listAnnotations(imageUrl)) {
			ImageFragment fragment = annotation.getFragment();
			if(fragment!=null && fragment.getShape() != null && shapeTypes!=null &&
					shapeTypes.contains(fragment.getShape().getClass().getName())) {
				annotations.add(annotation);
			}
		}

		return annotations;
	}
	
	@Override
	public Collection<ImageAnnotation> findAnnotations(String searchTerm) 
			throws AnnotationServiceException {
		
		Collection<ImageAnnotation> annotations = null;			

		try {
			// call the annotation middleware			
			ClientResponse<String> response=getAnnotationService().
				findAnnotations(URLEncoder.encode(searchTerm));	
			
			// check the response
			if(response.getStatus()!=HttpResponseCodes.SC_OK)
				throw new AnnotationServiceException(response.getStatus());
			
			// parse the response			
			annotations = RdfXmlAnnotationBuilder.fromRdfXml(response.getEntity());
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		}
		
		return annotations;
	}
	
	private AnnotationServer getAnnotationService() {
		HttpClient client = new HttpClient();
		/*
		 * Credentials defaultcreds = new UsernamePasswordCredentials("both",
		 * "tomcat"); client.getState().setCredentials(new
		 * AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT,
		 * AuthScope.ANY_REALM), defaultcreds);
		 */

		if(clientRequest!=null) {
			// make sure to forward all cookies to the middleware
			javax.servlet.http.Cookie[] cookies = clientRequest.getCookies();
			if (cookies != null) {
				for (javax.servlet.http.Cookie c : cookies) {
					c.setDomain(clientRequest.getServerName());
					c.setPath("/");
					Cookie apacheCookie = new Cookie(c.getDomain(), c.getName(), c.getValue(), c
							.getPath(), c.getMaxAge(), c.getSecure());
					client.getState().addCookie(apacheCookie);
				}
				client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			}
		}

		return ProxyFactory.create(AnnotationServer.class, annotationMiddlewareBaseUrl,
				new ApacheHttpClientExecutor(client));
	}
}
