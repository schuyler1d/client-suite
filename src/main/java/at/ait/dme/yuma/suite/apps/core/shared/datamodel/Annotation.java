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

package at.ait.dme.yuma.suite.apps.core.shared.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.ait.dme.yuma.suite.apps.core.shared.User;

/**
 * The base class for all types of annotations. Annotations are usually identical
 * across different media types. The major difference is the fragment, which is 
 * entirely media-specific and is not covered in this base class.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */ 
public abstract class Annotation implements Serializable {

	private static final long serialVersionUID = 5702749187171740401L;
	
	/**
	 * Scope definitions
	 */
	public enum Scope { PUBLIC, PRIVATE };
	
	/**
	 * Type definitionsR
	 */
	public enum MediaType { IMAGE, MAP, AUDIO, VIDEO };
		
	/**
	 * Annotation ID
	 */
	private String annotationId;
	
	/**
	 * The annotation's parent ID (if it is a reply)
	 */
	private String parentId;
	
	/**
	 * The annotation's root ID (if it is a reply)
	 */
	private String rootId;	
	
	/**
	 * The URI of the annotated object (must be resolvable by the annotation frontend)
	 */
	private String objectUri;
	
	/**
	 * Creation timestamp
	 */
	private Date created;
	
	/**
	 * Timestamp of last modification
	 */
	private Date lastModified;
	
	/**
	 * Creator
	 */
	private User createdBy;

	/**
	 * The title
	 */
	private String title;
	
	/**
	 * The annotation text
	 */
	private String text;
	
	/**
	 * The annotation media type
	 */
	private MediaType mediaType;
	
	/**
	 * The media fragment
	 */
	private MediaFragment fragment;
	
	/**
	 * The annotation scope
	 */
	private Scope scope;
	
	/**
	 * The semantic tags which are part of this annotation
	 */
	protected List<SemanticTag> tags;
	
	/**
	 * Nested replies
	 */
	private List<Annotation> replies;	
	
	public String getId() {
		return annotationId;
	}
	
	public void setId(String id) {
		this.annotationId = toNullIfEmpty(id);
	}

	public String getParentId() {
		return parentId;
	}
	
	public void setParentId(String parentId) {
		this.parentId = toNullIfEmpty(parentId);
	}
	
	public String getRootId() {
		return rootId;
	}
	
	public void setRootId(String rootId) {
		this.rootId = toNullIfEmpty(rootId);
	}
	
	public String getObjectUri() {
		return objectUri;
	}
	
	public void setObjectUri(String objectId) {
		this.objectUri = toNullIfEmpty(objectId);
	}

	public Date getCreated() {
		return new Date(created.getTime());
	}
	
	public void setCreated(Date created) {
		this.created = new Date(created.getTime());
	}
	
	public Date getLastModified() {
		return new Date(lastModified.getTime());
	}
	
	public void setLastModified(Date lastModified) {
		this.lastModified = new Date(lastModified.getTime());
	}
	
	public User getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = toNullIfEmpty(title);
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = toNullIfEmpty(text);
	}		
	
	public MediaType getMediaType() {
		return mediaType;
	}
	
	public void setMediaType(MediaType type) {
		this.mediaType = type;
	}
	
	public MediaFragment getFragment() {
		return fragment;
	}
	
	public void setFragment(MediaFragment fragment) {
		this.fragment = fragment;
	}
	
	public boolean hasFragment() {
		return (fragment != null && !fragment.isVoid());
	}
	
	public Scope getScope() {
		return scope;
	}
		
	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public List<SemanticTag> getTags() {
	    return tags;
	}
	
	public void addTag(SemanticTag semanticTag) {
	    if(tags == null) {
	        tags = new ArrayList<SemanticTag>();
	    }
	    tags.add(semanticTag);
	}
	
	public void setTags(List<SemanticTag> semanticTags) {
	    this.tags = semanticTags;
	}
	
	public boolean hasTags() {
		return (tags != null && !tags.isEmpty());
	}
	
	public List<Annotation> getReplies() {
		return replies;
	}
	
	public void addReply(Annotation reply) {
		if (replies == null) 
			replies = new ArrayList<Annotation>();
		replies.add(reply);
	}
	
	public void removeReply(Annotation reply) {
		if (replies != null) {
			replies.remove(reply);
		}	
	}
	
	public void setReplies(List<Annotation> replies) {
		this.replies = replies;
	}
	
	public boolean hasReplies() {
		if (replies == null)
			return false;
		
		return !replies.isEmpty();
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Annotation))
			return false;

		if (this == other)
			return true;
		
		Annotation a = (Annotation) other;
	
		if (!equalsNullable(annotationId, a.annotationId))
			return false;

		if (!equalsNullable(parentId, a.parentId))
			return false;
		
		if (!equalsNullable(rootId, a.rootId))
			return false;
		
		if (!equalsNullable(objectUri, a.objectUri))
			return false;
		
		// Compare creation timestamp w/o milliseconds
		if (created == null) {
			if (a.created != null)
				return false;
		} else {
			Date thisDate = new Date((created.getTime()/1000)*1000);
			Date compareDate = new Date((a.created.getTime()/1000)*1000);			
			if (!thisDate.equals(compareDate))
				return false;
		}
		
		// Compare modification date w/o milliseconds
		if (lastModified == null) {
			if (a.lastModified != null)
				return false;
		} else {
			Date thisDate = new Date((lastModified.getTime()/1000)*1000);
			Date compareDate = new Date((a.lastModified.getTime()/1000)*1000);			
			if (!thisDate.equals(compareDate))
				return false;
		}
		
		if (!equalsNullable(createdBy, a.createdBy))
			return false;
		
		if (!equalsNullable(title, a.title))
			return false;
		
		if (!equalsNullable(text, a.text))
			return false;
		
		if (!equalsNullable(mediaType, a.mediaType))
			return false;
		
		if (!equalsNullable(fragment, a.fragment))
			return false;

		if (!equalsNullable(scope, a.scope))
			return false;

		if (!equalsNullable(tags, a.tags))
			return false;
		
		if (!equalsNullable(replies, a.replies))
			return false;

		return true;	
	}
		
	@Override
	public int hashCode() {
		return (annotationId + title + text).hashCode();
	}
	
	protected boolean equalsNullable(Object a, Object b) {
		if (a == null)
			return b == null;
		
		if (b == null)
			return a == null;
		
		return a.equals(b);
	}
	
	protected String toNullIfEmpty(String str) {
		if (str == null)
			return null;
		
		if (str.trim().isEmpty())
			return null;
		
		return str;
	}

}