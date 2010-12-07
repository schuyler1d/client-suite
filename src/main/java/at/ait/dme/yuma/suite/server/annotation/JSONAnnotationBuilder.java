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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import at.ait.dme.yuma.suite.client.annotation.Annotation;
import at.ait.dme.yuma.suite.client.annotation.Annotation.Scope;
import at.ait.dme.yuma.suite.client.annotation.Annotation.Type;
import at.ait.dme.yuma.suite.client.image.ImageFragment;
import at.ait.dme.yuma.suite.client.image.annotation.ImageAnnotation;

/**
 * Converts annotations to and from JSON.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class JSONAnnotationBuilder {
	
	private static final String KEY_ID = "id";
	private static final String KEY_PARENT_ID = "parent-id";
	private static final String KEY_ROOT_ID = "root-id";
	private static final String KEY_OBJECT_ID = "object-id";
	private static final String KEY_CREATED = "created";	
	private static final String KEY_LAST_MODIFIED = "last-modified";
	private static final String KEY_CREATED_BY = "created-by";
	private static final String KEY_TITLE = "title";
	private static final String KEY_TEXT = "text";
	private static final String KEY_TYPE = "type";
	private static final String KEY_FRAGMENT = "fragment";
	private static final String KEY_SCOPE = "scope";
	// private static final String KEY_TAGS = "tags";
	private static final String KEY_REPLIES = "replies";

	public static ArrayList<Annotation> toAnnotations(String json) {
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		JSONArray jsonArray=(JSONArray)JSONValue.parse(json);
		
		if (jsonArray == null)
			return annotations;
		
		for (Object obj : jsonArray) {
			JSONObject jsonObj = (JSONObject) obj;
			Annotation annotation;
			
			Type type = Type.valueOf(((String) jsonObj.get(KEY_TYPE)).toUpperCase());
			if (type == Type.IMAGE) {
				annotation = new ImageAnnotation();				
				SVGBuilder svg = new SVGBuilder();
				annotation.setFragment(svg.toImageFragment((String) jsonObj.get(KEY_FRAGMENT)));									
			} else {
				throw new RuntimeException("Unsupported annotation type: " + type.name());
			}
			
			annotation.setId((String) jsonObj.get(KEY_ID));			
			annotation.setParentId((String) jsonObj.get(KEY_PARENT_ID));
			annotation.setRootId((String) jsonObj.get(KEY_ROOT_ID));
			annotation.setObjectId((String) jsonObj.get(KEY_OBJECT_ID));
			annotation.setCreated(new Date());
			annotation.setLastModified(new Date());
			annotation.setCreatedBy((String) jsonObj.get(KEY_CREATED_BY));
			annotation.setTitle((String) jsonObj.get(KEY_TITLE));
			annotation.setText((String) jsonObj.get(KEY_TEXT));
			annotation.setType(type);
			
			String scope = (String) jsonObj.get(KEY_SCOPE);
			if (scope != null) {
				annotation.setScope(Scope.valueOf(scope.toUpperCase()));
			} else {
				annotation.setScope(Scope.PUBLIC);
			}

			JSONArray jsonReplies = (JSONArray) jsonObj.get(KEY_REPLIES);
			if (jsonReplies != null) {
				ArrayList<Annotation> replies = toAnnotations(jsonReplies.toString());
				annotation.setReplies(replies);
			}
			
			annotations.add(annotation);
		}
		return annotations;
	}
		
	public static JSONArray toJSON(Annotation annotation) throws IOException {
		List<Annotation> annotations = new ArrayList<Annotation>();
		annotations.add(annotation);
		return toJSON(annotations);
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray toJSON(List<Annotation> annotations) throws IOException {
		JSONArray jsonArray = new JSONArray();
		if (annotations != null) {
			for(Annotation annotation : annotations) {
				JSONObject jsonObj = new JSONObject();

				jsonObj.put(KEY_ID, annotation.getId());
				jsonObj.put(KEY_PARENT_ID, annotation.getParentId());		
				jsonObj.put(KEY_ROOT_ID, annotation.getRootId());						
				jsonObj.put(KEY_OBJECT_ID, annotation.getObjectId());						
				jsonObj.put(KEY_CREATED, annotation.getCreated().getTime());
				jsonObj.put(KEY_LAST_MODIFIED, annotation.getLastModified().getTime());
				jsonObj.put(KEY_CREATED_BY, annotation.getCreatedBy());
				jsonObj.put(KEY_TITLE, annotation.getTitle());		
				jsonObj.put(KEY_TEXT, annotation.getText());
				jsonObj.put(KEY_TYPE, annotation.getType().name());
						
				if (annotation.getType() == Type.IMAGE) {
					ImageAnnotation i = (ImageAnnotation) annotation;
					if(i.hasFragment()) {		
						SVGBuilder svg = new SVGBuilder();
						jsonObj.put("fragment", svg.toSVG((ImageFragment) i.getFragment()));				
					}
				}

				jsonObj.put(KEY_SCOPE, annotation.getScope().name());
				
				// TODO tags!
				// if (annotation.hasTags()) 
				//	jsonObj.put(KEY_TAGS, toJSON(annotation.getTags()));
				
				if (annotation.hasReplies())
					jsonObj.put(KEY_REPLIES, toJSON(annotation.getReplies()));

				jsonArray.add(jsonObj);
			}
		}
		
		return jsonArray;
	}
	
}
