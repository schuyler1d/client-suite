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

package at.ait.dme.yuma.suite.apps.image.core.client.tagcloud.annotation;

import java.util.HashMap;

import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import at.ait.dme.yuma.suite.apps.core.client.datamodel.Annotation.MediaType;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.SemanticTag;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationEditForm;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationPanel;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationTreeNode;
import at.ait.dme.yuma.suite.apps.image.core.client.StandardImageAnnotationForm;
import at.ait.dme.yuma.suite.apps.image.core.client.tagcloud.TagCloud;
import at.ait.dme.yuma.suite.apps.map.client.annotation.AnnotationLayer;

/**
 * A sub-class of {@link StandardImageAnnotationForm} that works with the {@link AnnotationLayer}'s
 * tag cloud.
 * 
 * @author Rainer Simon
 *
 */
public class TagEnabledAnnotationForm extends StandardImageAnnotationForm {
	
	/**
	 * Reference to the tag cloud
	 */
	private TagCloud tagCloud;
	
	/**
	 * Reference to the annotationComposite
	 */
	private AnnotationPanel panel;
	
	/**
	 * The FlowPanel displaying the current tags  
	 */
	private FlowPanel tagPanel;
	
	/**
	 * The tags for this annotation
	 */
	public HashMap<SemanticTag, Widget> tags = new HashMap<SemanticTag, Widget>(); 

	public TagEnabledAnnotationForm(TagCloud tagCloud, MediaType mediaType) {
		super(mediaType);
		this.tagCloud = tagCloud;
	}
	
	public TagEnabledAnnotationForm(AnnotationPanel panel, AnnotationTreeNode annotation,
			AnnotationTreeNode parent, TagCloud tagCloud, MediaType mediaType) {
	
		super(panel, annotation, parent, mediaType);
		
		this.panel = panel;
		this.tagCloud = tagCloud;
	
		((HasTagCloud) panel.getMediaViewer()).setAnnotationEditForm(this);
		
		if (annotation != null && annotation.getAnnotation().hasTags()) {
			for (SemanticTag t : annotation.getAnnotation().getTags()) {
				addTag(t);
			}
		}
	}
	
	@Override
	public AnnotationEditForm newInstance(AnnotationPanel panel, AnnotationTreeNode annotation,
			AnnotationTreeNode parent) {
		
		return new TagEnabledAnnotationForm(panel, annotation, parent, tagCloud, mediaType);
	}
	
	@Override
	public void addTag(SemanticTag tag) {
		InlineHTML span = new InlineHTML("<a target=\"_blank\" href=\""
				+ tag.getURI() + "\" title=\"" 
				+ tag.getDescription() + "\">" 
				+ tag.getLabel() + "</a>"
		);
		tagPanel.add(span);
		tags.put(tag, span);
		panel.layout();
	}
	
	@Override
	public void removeTag(SemanticTag tag) {
		tagPanel.remove(tags.get(tag));
		tags.remove(tag);
		panel.layout();
	}

}
