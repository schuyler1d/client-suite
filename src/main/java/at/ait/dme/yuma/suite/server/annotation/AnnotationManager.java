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
import java.util.Arrays;
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

import at.ait.dme.yuma.suite.client.annotation.Annotation;
import at.ait.dme.yuma.suite.client.annotation.Annotation.MediaType;
import at.ait.dme.yuma.suite.client.image.annotation.ImageFragment;
import at.ait.dme.yuma.suite.client.server.AnnotationService;
import at.ait.dme.yuma.suite.client.server.exception.AnnotationServiceException;
import at.ait.dme.yuma.suite.server.util.Config;
import at.ait.dme.yuma.suite.server.util.URLEncoder;

/**
 * This class contains all actions on annotations.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class AnnotationManager implements AnnotationService {
	private static Logger logger = Logger.getLogger(AnnotationManager.class);

	private static final String ANNOTATION_SERVICE_URL_PROPERTY = "annotation.server.base.url";
	private static final String FAILED_TO_PARSE_ANNOTATION = "failed to parse anntotation";
	
	private static String annotationServerBaseUrl = null;
	private HttpServletRequest clientRequest = null;
	
	// we will use a simple cache here for now.
	// TODO don't use this on a cluster
	private static final int MAX_SIZE_ANNOTATION_CACHE = 20;
	private static ConcurrentLinkedHashMap<String, Collection<Annotation>> annotationCache = 
		new ConcurrentLinkedHashMap<String, Collection<Annotation>>(EvictionPolicy.LRU, 
				MAX_SIZE_ANNOTATION_CACHE);

	public static void init(Config config) throws ServletException {
		annotationServerBaseUrl = config.getStringProperty(ANNOTATION_SERVICE_URL_PROPERTY);
	}

	public AnnotationManager(HttpServletRequest clientRequest) {
		this.clientRequest = clientRequest;
	}
	
	@Override
	public Annotation createAnnotation(Annotation annotation)
			throws AnnotationServiceException {
				
		String annotationId = null;
		try {
			// Call the Annotation Server
			ClientResponse<String> response = getAnnotationServer()
				.createAnnotation(JSONAnnotationHandler.serializeAnnotations(Arrays.asList(annotation)).toString());
			
			// Check response
			if (response.getStatus() != HttpResponseCodes.SC_CREATED)
				throw new AnnotationServiceException(response.getStatus());
			
			annotationId = response.getEntity();
			
			// Remove from cache
			annotationCache.remove(annotation.getObjectUri());
		} catch (AnnotationServiceException e) {
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		}
		
		annotation.setId(annotationId);
		return annotation;
	}
	
	@Override
	public Annotation updateAnnotation(Annotation annotation) 
			throws AnnotationServiceException {
		
		String annotationId = null;
		try {
			// Call the Annotation Server
			ClientResponse<String> response = getAnnotationServer()
				.updateAnnotation(URLEncoder.encode(annotation.getId()), JSONAnnotationHandler.serializeAnnotations(Arrays.asList(annotation)).toString());
			
			// Check response			
			if(response.getStatus() != HttpResponseCodes.SC_OK)
				throw new AnnotationServiceException(response.getStatus());	
			annotationId = response.getEntity();
			
			// Remove from cache
			annotationCache.remove(annotation.getObjectUri());
		} catch(AnnotationServiceException ase) {
			logger.error(ase.getMessage(), ase);
			throw ase;
		} catch (Exception e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationServiceException(e.getMessage());
		} 
		
		annotation.setId(annotationId);
		return annotation;
	}
	
	@Override
	public void deleteAnnotation(String annotationId) throws AnnotationServiceException {
		try {					
			// Call the Annotation Server
			ClientResponse<String> response = getAnnotationServer().
				deleteAnnotation(URLEncoder.encode(annotationId));
			
			// Check response			
			if(response.getStatus() != HttpResponseCodes.SC_OK &&
					response.getStatus() != HttpResponseCodes.SC_NO_CONTENT)
				throw new AnnotationServiceException(response.getStatus());
			
			// Clear cache
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
	public Collection<Annotation> listAnnotations(String objectId) 
			throws AnnotationServiceException {
		Collection<Annotation> annotations = null;			

		try {
			if ((annotations = annotationCache.get(objectId)) == null) {
				
				// Call the Annotation Server
				ClientResponse<String> response=getAnnotationServer().
					getAnnotationTree(URLEncoder.encode(objectId));	
				
				// Check response
				if (response.getStatus() != HttpResponseCodes.SC_OK)
					throw new AnnotationServiceException(response.getStatus());
				
				// Parse the response
				annotations = JSONAnnotationHandler.parseAnnotations(response.getEntity());
				
				// Cache the response
				annotationCache.putIfAbsent(objectId, annotations);
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
	public Collection<Annotation> listAnnotations(String objectId, Set<String> shapeTypes)
		throws AnnotationServiceException {
		
		Collection<Annotation> annotations = new ArrayList<Annotation>(); 
		
		// List all annotations of this object and keep only those that have
		// a fragment with a shape of one of the given types
		for (Annotation a : listAnnotations(objectId)) {
			if (a.getMediaType() == MediaType.IMAGE) {
				ImageFragment fragment = (ImageFragment) a.getFragment();
				if (fragment != null && fragment.getShape() != null && shapeTypes != null &&
						shapeTypes.contains(fragment.getShape().getClass().getName())) {
					annotations.add(a);
				}
			}
		}

		return annotations;
	}
	
	private RESTAnnotationServer getAnnotationServer() {
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

		return ProxyFactory.create(RESTAnnotationServer.class, annotationServerBaseUrl,
				new ApacheHttpClientExecutor(client));
	}
}
