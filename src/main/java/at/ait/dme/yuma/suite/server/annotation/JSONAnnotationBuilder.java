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
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import at.ait.dme.yuma.suite.client.annotation.Annotation;
import at.ait.dme.yuma.suite.client.annotation.Annotation.Scope;
import at.ait.dme.yuma.suite.client.annotation.Annotation.Type;
import at.ait.dme.yuma.suite.client.annotation.MediaFragment;
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
	private static final String KEY_PARENT_ID = "parentId";
	private static final String KEY_ROOT_ID = "rootId";
	private static final String KEY_OBJECT_ID = "objectId";
	private static final String KEY_CREATED = "created";	
	private static final String KEY_LAST_MODIFIED = "lastModfied";
	private static final String KEY_CREATED_BY = "createdBy";
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
			
			Type type = Type.valueOf((String) jsonObj.get(KEY_TYPE));
			if (type == Type.IMAGE) {
				annotation = new ImageAnnotation();				
				annotation.setFragment(toImageFragment((String) jsonObj.get(KEY_FRAGMENT)));									
			} else {
				throw new RuntimeException("Unsupported annotation type: " + type.name());
			}
			
			annotation.setId((String) jsonObj.get(KEY_ID));
			annotation.setParentId((String) jsonObj.get(KEY_PARENT_ID));
			annotation.setRootId((String) jsonObj.get(KEY_ROOT_ID));
			annotation.setObjectId((String) jsonObj.get(KEY_OBJECT_ID));
			// created 
			// lastModified
			annotation.setCreatedBy((String) jsonObj.get(KEY_CREATED_BY));
			annotation.setTitle((String) jsonObj.get(KEY_TITLE));
			annotation.setText((String) jsonObj.get(KEY_TEXT));
			annotation.setType(type);
			
			String scope = (String) jsonObj.get(KEY_SCOPE);
			if (scope != null) {
				annotation.setScope(Scope.valueOf(scope));
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
	
	private static ImageFragment toImageFragment(String imageFragment) {
		/*
		ImageFragment fragment = null;
		
		if (imageFragment != null) {		
			JSONObject jsonImageRect = (JSONObject)jsonFragment.get("imageRect");
			int left = ((Long)jsonImageRect.get("left")).intValue();
			int top = ((Long)jsonImageRect.get("top")).intValue();
			int width = ((Long)jsonImageRect.get("width")).intValue();
			int height = ((Long)jsonImageRect.get("height")).intValue();
			ImageRect imageRect = new ImageRect(left,top,width,height);
			
			JSONObject jsonVisibleRect = (JSONObject)jsonFragment.get("visibleRect");
			left = ((Long)jsonVisibleRect.get("left")).intValue();
			top = ((Long)jsonVisibleRect.get("top")).intValue();
			width = ((Long)jsonVisibleRect.get("width")).intValue();
			height = ((Long)jsonVisibleRect.get("height")).intValue();
			ImageRect visibleRect = new ImageRect(left,top,width,height);
			
			JSONObject jsonShape = (JSONObject)jsonFragment.get("shape");
			left = ((Long)jsonShape.get("left")).intValue();
			top = ((Long)jsonShape.get("top")).intValue();
			width = ((Long)jsonShape.get("width")).intValue();
			height = ((Long)jsonShape.get("height")).intValue();
			int strokeWidth = ((Long)jsonShape.get("strokeWidth")).intValue();
			
			JSONObject jsonColor = (JSONObject)jsonShape.get("color");
			int r = ((Long)jsonColor.get("r")).intValue();
			int g = ((Long)jsonColor.get("g")).intValue();
			int b = ((Long)jsonColor.get("b")).intValue();				
			
			Shape shape = null;
			String type = (String)jsonShape.get("type");
			if(type.equals("ellipse")) 
				shape = new Ellipse(left,top,width,height,new Color(r,g,b),strokeWidth);
			else if(type.equals("rectangle")) 
				shape = new Rectangle(left,top,width,height,new Color(r,g,b),strokeWidth);
			else if(type.equals("cross")) 
				shape = new Cross(left,top,width,height,new Color(r,g,b),strokeWidth);
			else if(type.equals("polygon")) { 
				Collection<Point> points = new ArrayList<Point>();
				JSONArray jsonPoints=(JSONArray)JSONValue.parse((String)jsonShape.get("points"));
				for(Object jsonPoint : jsonPoints) {
					Point point = new Point(
							((Long)((JSONObject)jsonPoint).get("x")).intValue(),
							((Long)((JSONObject)jsonPoint).get("y")).intValue());
					points.add(point);
				}
				shape = new Polygon(left,top,width,height,new Color(r,g,b),strokeWidth,points);
			}	
			fragment = new ImageFragment(visibleRect, imageRect, shape);
		}
		return fragment;
		*/
		return null;
	}
	
	public static JSONArray toJSON(Annotation annotation) {
		List<Annotation> annotations = new ArrayList<Annotation>();
		annotations.add(annotation);
		return toJSON(annotations);
	}
	
	@SuppressWarnings("unchecked")
	public static JSONArray toJSON(List<Annotation> annotations) {
		JSONArray jsonArray = new JSONArray();
		if (annotations != null) {
			for(Annotation annotation : annotations) {
				JSONObject jsonObj = new JSONObject();

				jsonObj.put(KEY_ID, annotation.getId());
				jsonObj.put(KEY_PARENT_ID, annotation.getParentId());		
				jsonObj.put(KEY_ROOT_ID, annotation.getRootId());						
				jsonObj.put(KEY_OBJECT_ID, annotation.getObjectId());						
				jsonObj.put(KEY_CREATED, annotation.getCreated().toString());
				jsonObj.put(KEY_LAST_MODIFIED, annotation.getLastModified());
				jsonObj.put(KEY_CREATED_BY, annotation.getCreatedBy());
				jsonObj.put(KEY_TITLE, annotation.getTitle());		
				jsonObj.put(KEY_TEXT, annotation.getText());
				jsonObj.put(KEY_TYPE, annotation.getType().name());
								
				if (annotation.getType() == Type.IMAGE) {
					ImageAnnotation i = (ImageAnnotation) annotation;
					if(i.hasFragment()) {														
						jsonObj.put("fragment", toJSON(i.getFragment()));				
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
	
	private static JSONObject toJSON(MediaFragment fragment) {
		JSONObject jsonFragment = new JSONObject();		
		
		/*
		JSONObject jsonImageRect= new JSONObject();
		jsonImageRect.put("left", fragment.getImageRect().getLeft());
		jsonImageRect.put("top", fragment.getImageRect().getTop());
		jsonImageRect.put("width", fragment.getImageRect().getWidth());
		jsonImageRect.put("height", fragment.getImageRect().getHeight());
		jsonFragment.put("imageRect", jsonImageRect);
		
		JSONObject jsonVisibleRect= new JSONObject();
		jsonVisibleRect.put("left", fragment.getVisibleRect().getLeft());
		jsonVisibleRect.put("top", fragment.getVisibleRect().getTop());
		jsonVisibleRect.put("width", fragment.getVisibleRect().getWidth());
		jsonVisibleRect.put("height", fragment.getVisibleRect().getHeight());
		jsonFragment.put("visibleRect", jsonVisibleRect);
		
		JSONObject jsonShape= new JSONObject();
		if(fragment.getShape() instanceof Ellipse)
			jsonShape.put("type", "ellipse");
		else if(fragment.getShape() instanceof Rectangle)
			jsonShape.put("type", "rectangle");
		else if(fragment.getShape() instanceof Cross)
			jsonShape.put("type", "cross");
		else if(fragment.getShape() instanceof Polygon) {
			jsonShape.put("type", "polygon");
			JSONArray jsonPoints = new JSONArray();
			for(Object point : ((Polygon)fragment.getShape()).getPoints()) {						
				JSONObject jsonPoint = new JSONObject();
				jsonPoint.put("x", ((Point)point).getX());
				jsonPoint.put("y", ((Point)point).getY());							
				jsonPoints.add(jsonPoint);
			}
			
			jsonShape.put("points", jsonPoints.toString());
		}	
		jsonShape.put("left", fragment.getShape().getLeft());
		jsonShape.put("top", fragment.getShape().getTop());
		jsonShape.put("width", fragment.getShape().getWidth());
		jsonShape.put("height", fragment.getShape().getHeight());
		jsonShape.put("strokeWidth", fragment.getShape().getStrokeWidth());
		
		JSONObject jsonColor= new JSONObject();
		jsonColor.put("r", fragment.getShape().getColor().getR());
		jsonColor.put("g", fragment.getShape().getColor().getG());
		jsonColor.put("b", fragment.getShape().getColor().getB());
		jsonShape.put("color", jsonColor);					
		jsonFragment.put("shape", jsonShape);			
		*/
		
		return jsonFragment;
	}
	
}
