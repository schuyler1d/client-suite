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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import at.ait.dme.yuma.suite.client.annotation.Annotation;
import at.ait.dme.yuma.suite.client.annotation.Annotation.Scope;
import at.ait.dme.yuma.suite.client.annotation.Annotation.MediaType;
import at.ait.dme.yuma.suite.client.annotation.SemanticTag;
import at.ait.dme.yuma.suite.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.suite.client.image.annotation.ImageFragment;

/**
 * Converts annotations to and from JSON.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class JSONAnnotationHandler {
	
	private static final String KEY_ID = "id";
	private static final String KEY_PARENT_ID = "parent-id";
	private static final String KEY_ROOT_ID = "root-id";
	private static final String KEY_OBJECT_URI = "object-uri";
	private static final String KEY_CREATED = "created";	
	private static final String KEY_LAST_MODIFIED = "last-modified";
	private static final String KEY_CREATED_BY = "created-by";
	private static final String KEY_TITLE = "title";
	private static final String KEY_TEXT = "text";
	private static final String KEY_MEDIA_TYPE = "media-type";
	private static final String KEY_FRAGMENT = "fragment";
	private static final String KEY_SCOPE = "scope";
	private static final String KEY_TAGS = "tags";
	private static final String KEY_REPLIES = "replies";
	
	private static final String KEY_TAG_URI = "uri";
	private static final String KEY_TAG_LABEL = "label";
	private static final String KEY_TAG_DESCRIPTION = "description";
	private static final String KEY_TAG_LANG = "lang";
	private static final String KEY_TAG_TYPE = "type";

    private static Logger logger = Logger.getLogger(JSONAnnotationHandler.class);
	
	public static ArrayList<Annotation> parseAnnotations(String json) {
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		JSONArray jsonArray=(JSONArray)JSONValue.parse(json);
		
		if (jsonArray == null)
			return annotations;
		
		for (Object obj : jsonArray) {
			JSONObject jsonObj = (JSONObject) obj;
			Annotation annotation;
			
			MediaType type = MediaType.valueOf(((String) jsonObj.get(KEY_MEDIA_TYPE)).toUpperCase());
			if (type == MediaType.IMAGE) {
				annotation = new ImageAnnotation();		
				
				String fragment = (String) jsonObj.get(KEY_FRAGMENT);
				if ((fragment != null) && (!fragment.isEmpty())){
					SVGFragmentHandler svg = new SVGFragmentHandler();
					try {
						annotation.setFragment(svg.toImageFragment((String) jsonObj.get(KEY_FRAGMENT)));
					} catch (IOException e) {
						logger.warn("Could not parse image fragment: " + e.getMessage());
					}
				}
			} else {
				throw new RuntimeException("Unsupported annotation type: " + type.name());
			}
			
			annotation.setId((String) jsonObj.get(KEY_ID));			
			annotation.setParentId((String) jsonObj.get(KEY_PARENT_ID));
			annotation.setRootId((String) jsonObj.get(KEY_ROOT_ID));
			annotation.setObjectUri((String) jsonObj.get(KEY_OBJECT_URI));
			annotation.setCreated(new Date((Long) jsonObj.get(KEY_CREATED)));
			annotation.setLastModified(new Date((Long) jsonObj.get(KEY_LAST_MODIFIED)));
			annotation.setCreatedBy((String) jsonObj.get(KEY_CREATED_BY));
			annotation.setTitle((String) jsonObj.get(KEY_TITLE));
			annotation.setText((String) jsonObj.get(KEY_TEXT));
			annotation.setMediaType(type);
			
			String scope = (String) jsonObj.get(KEY_SCOPE);
			if (scope != null) {
				annotation.setScope(Scope.valueOf(scope.toUpperCase()));
			} else {
				annotation.setScope(Scope.PUBLIC);
			}
			
			JSONArray jsonTags = (JSONArray) jsonObj.get(KEY_TAGS);
			if (jsonTags != null)
				annotation.setTags(parseSemanticTags(jsonTags));

			JSONArray jsonReplies = (JSONArray) jsonObj.get(KEY_REPLIES);
			if (jsonReplies != null) {
				ArrayList<Annotation> replies = parseAnnotations(jsonReplies.toString());
				annotation.setReplies(replies);
			}
			
			annotations.add(annotation);
		}
		return annotations;
	}
	
	public static ArrayList<SemanticTag> parseSemanticTags(JSONArray jsonArray) {
		ArrayList<SemanticTag> tags = new ArrayList<SemanticTag>();
		
		for (Object obj : jsonArray) {
			JSONObject jsonObj = (JSONObject) obj;
			SemanticTag t = new SemanticTag();
			
			t.setURI((String) jsonObj.get(KEY_TAG_URI));
			t.setLabel((String) jsonObj.get(KEY_TAG_LABEL));
			t.setDescription((String) jsonObj.get(KEY_TAG_DESCRIPTION));
			t.setLanguage((String) jsonObj.get(KEY_TAG_LANG));
			t.setType((String) jsonObj.get(KEY_TAG_TYPE));
			
			tags.add(t);
		}
		
		return tags;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray serializeAnnotations(List<Annotation> annotations) throws IOException {
		JSONArray jsonArray = new JSONArray();
		if (annotations != null) {
			for(Annotation annotation : annotations) {
				JSONObject jsonObj = new JSONObject();

				jsonObj.put(KEY_ID, annotation.getId());
				jsonObj.put(KEY_PARENT_ID, annotation.getParentId());		
				jsonObj.put(KEY_ROOT_ID, annotation.getRootId());						
				jsonObj.put(KEY_OBJECT_URI, annotation.getObjectUri());						
				jsonObj.put(KEY_CREATED, annotation.getCreated().getTime());
				jsonObj.put(KEY_LAST_MODIFIED, annotation.getLastModified().getTime());
				jsonObj.put(KEY_CREATED_BY, annotation.getCreatedBy());
				jsonObj.put(KEY_TITLE, annotation.getTitle());		
				jsonObj.put(KEY_TEXT, annotation.getText());
				jsonObj.put(KEY_MEDIA_TYPE, annotation.getMediaType().name());
						
				if (annotation.getMediaType() == MediaType.IMAGE) {
					ImageAnnotation i = (ImageAnnotation) annotation;
					if(i.hasFragment()) {		
						SVGFragmentHandler svg = new SVGFragmentHandler();
						jsonObj.put("fragment", svg.toSVG((ImageFragment) i.getFragment()));				
					}
				}

				jsonObj.put(KEY_SCOPE, annotation.getScope().name());
				
				if (annotation.hasTags()) 
					jsonObj.put(KEY_TAGS, serializeSemanticTags(annotation.getTags()));
				
				if (annotation.hasReplies())
					jsonObj.put(KEY_REPLIES, serializeAnnotations(annotation.getReplies()));

				jsonArray.add(jsonObj);
			}
		}
		
		return jsonArray;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray serializeSemanticTags(List<SemanticTag> tags) {
		JSONArray jsonArray = new JSONArray();
		
		for (SemanticTag t : tags) {
			JSONObject jsonObj = new JSONObject();
			
			jsonObj.put(KEY_TAG_URI, t.getURI());
			jsonObj.put(KEY_TAG_LABEL, t.getLabel());
			jsonObj.put(KEY_TAG_DESCRIPTION, t.getDescription());
			jsonObj.put(KEY_TAG_LANG, t.getLanguage());
			jsonObj.put(KEY_TAG_TYPE, t.getType());
			
			jsonArray.add(jsonObj);
		}
		
		return jsonArray;
	}
	
}
