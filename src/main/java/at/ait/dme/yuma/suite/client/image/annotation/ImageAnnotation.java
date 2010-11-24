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

package at.ait.dme.yuma.suite.client.image.annotation;

import java.util.Date;
import java.util.List;

import at.ait.dme.yuma.suite.client.annotation.Annotation;
import at.ait.dme.yuma.suite.client.annotation.SemanticTag;
import at.ait.dme.yuma.suite.client.image.ImageFragment;

/**
 * Represents an image annotation with an unique addressable URI as id.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class ImageAnnotation extends Annotation {
		
	private static final long serialVersionUID = 7807008224822381714L;
	
	/**
	 * The annotated image fragment
	 */
	private ImageFragment fragment;
		
	public ImageAnnotation() {
		this.setCreated(new Date());
		this.setLastModified(new Date());
		this.setScope(Scope.PUBLIC);
		this.fragment = new ImageFragment();
	}
	
	public ImageAnnotation(String objectId, String createdBy) {
		this();
		this.setObjectId(objectId);
		this.setCreatedBy(createdBy);
	}
	
	public ImageAnnotation(String id, String objectId, String createdBy) {
		this(objectId, createdBy);
		this.setId(id);
	}
	
	public ImageAnnotation(String objectId, String parentId, String rootId,
			String createdBy, String title, String text, Scope scope, List<SemanticTag> tags) {
		this(objectId, createdBy);
		this.setTitle(title);
		this.setText(text);
		this.setParentId(parentId);
		this.setRootId(rootId);
		this.setScope(scope);
		this.setTags(tags);
	}

	public ImageAnnotation(String id, String objectId, String parentId, String rootId, 
			String createdBy, String title, String text, Scope scope, List<SemanticTag> tags) {
		
		this(objectId, parentId, rootId, createdBy, title, text, scope, tags);
		this.setId(id);
	}

	public ImageAnnotation(String id, String objectId, String parentId, String rootId, 
			String createdBy, String title, String text, Scope scope) {
		
		this(id, objectId, parentId, rootId, createdBy, title, text, scope, null);
	}
	
	public String toHtml() {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>")		
		.append("<head><title>")
		.append(getTitle())
		.append("</title></head>")
		.append("<body>")	
		.append(getText())	
		.append("</body>")		
		.append("</html>");			
		return buf.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ImageAnnotation))
			return false;
		
		if (!super.equals(other))
			return false;
		
		if (!equalsNullable(fragment, ((ImageAnnotation) other).fragment))
			return false;
		
		return true;	
	}

}
