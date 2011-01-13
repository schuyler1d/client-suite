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

package at.ait.dme.yuma.suite.core.client.annotation;

import java.io.Serializable;
import java.util.List;

/**
 * A Semantic Tag - i.e. a tag which represents a resource
 * on the linked data Web.
 * 
 * @author Rainer Simon
 */
public class SemanticTag implements Serializable {
	
	private static final long serialVersionUID = 948700343852610581L;

	/**
	 * The tag URI
	 */
	private String uri;
	
	/**
	 * The tag title
	 */
	private String label;
	
	/**
	 * Alternative labels
	 */
	private List<String> alternativeLabels = null;

	/**
	 * The description/abstract for this tag
	 */
	private String description;
	
	/**
	 * Language(s)
	 */
	private String lang;
		
	/**
	 * The type
	 */
	private String type;
			
	public SemanticTag() {
		// Required for GWT serialization
	}
	
	public SemanticTag(String label, List<String> alternativeLabels, String type, String lang, String description, String uri) {
		this.label = label;
		this.alternativeLabels = alternativeLabels;
		this.type = type;
		this.lang = lang;
		this.description = description;
		this.uri = uri;
	}
	
	public String getURI() {
		return uri;
	}
	
	public void setURI(String uri) {
		this.uri = uri;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getLanguage() {
		return lang;
	}
	
	public void setLanguage(String lang) {
		this.lang = lang;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public boolean hasAltLabels() {
		if (alternativeLabels == null)
			return false;
		
		return alternativeLabels.size() > 0;
	}
	
	public List<String> getAlternativeLabels() {
		return alternativeLabels;
	}
	
	@Override
	public boolean equals(Object tag) {
		// TODO implement this properly!
		if (!(tag instanceof SemanticTag)) return false; 
		SemanticTag s = (SemanticTag) tag;
		return this.uri.equals(s.uri) && this.label.equals(s.label);
	}

}
