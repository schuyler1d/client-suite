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

package at.ait.dme.yuma.suite.image.core.client.tagcloud.annotation;

import java.util.ArrayList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import at.ait.dme.yuma.suite.core.client.datamodel.SemanticTag;
import at.ait.dme.yuma.suite.core.client.gui.treeview.TreeViewComposite;
import at.ait.dme.yuma.suite.core.client.gui.treeview.AnnotationEditForm;
import at.ait.dme.yuma.suite.core.client.gui.treeview.AnnotationTreeNode;
import at.ait.dme.yuma.suite.core.client.server.enrichment.SemanticEnrichmentService;
import at.ait.dme.yuma.suite.core.client.server.enrichment.SemanticEnrichmentServiceAsync;
import at.ait.dme.yuma.suite.core.client.server.enrichment.SemanticTagSuggestions;
import at.ait.dme.yuma.suite.image.core.client.StandardImageAnnotationForm;
import at.ait.dme.yuma.suite.image.core.client.StandardImageComposite;
import at.ait.dme.yuma.suite.image.core.client.tagcloud.TagCloud;
import at.ait.dme.yuma.suite.map.client.annotation.AnnotationLayer;

/**
 * A sub-class of {@link StandardImageAnnotationForm} that works with the {@link AnnotationLayer}'s
 * tag cloud.
 * 
 * @author Rainer Simon
 *
 */
public class TagEnabledAnnotationForm extends StandardImageAnnotationForm {
	
	/**
	 * Tag font size
	 */
	private static final int TAG_FONT_SIZE= 24;
	
	/**
	 * Reference to the tag cloud
	 */
	private TagCloud tagCloud;
	
	/**
	 * Reference to the annotationComposite
	 */
	private TreeViewComposite annotationComposite;
	
	/**
	 * The FlowPanel displaying the current tags  
	 */
	private FlowPanel tagPanel;
	
	/**
	 * The tags for this annotation
	 */
	public HashMap<SemanticTag, Widget> tags = new HashMap<SemanticTag, Widget>(); 
	
	/**
	 * Semantic enrichment service
	 */
	private SemanticEnrichmentServiceAsync enrichmentService = 
		(SemanticEnrichmentServiceAsync) GWT.create(SemanticEnrichmentService.class);
	
	/**
	 * Enrichment service type constant
	 */
	private static final String WHICH_ENRICHMENT_SERVICE = 
		SemanticEnrichmentService.OPENCALAIS_DBPEDIA_LOOKUP; 

	public TagEnabledAnnotationForm(TagCloud tagCloud) {
		this.tagCloud = tagCloud;
	}
	
	public TagEnabledAnnotationForm(TreeViewComposite annotationComposite,
			TagCloud tagCloud, AnnotationTreeNode annotationTreeNode,
			boolean fragmentAnnotation, boolean update) {
	
		super(annotationComposite, annotationTreeNode, fragmentAnnotation, update);
		
		this.annotationComposite = annotationComposite;
		this.tagCloud = tagCloud;
	
		((StandardImageComposite)annotationComposite.getImageComposite()).setAnnotationForm(this);
		
		if (update && annotationTreeNode.getAnnotation().hasTags()) {
			for (SemanticTag t : annotationTreeNode.getAnnotation().getTags()) {
				addTag(t);
			}
		}
	}
	
	@Override
	public AnnotationEditForm createNew(TreeViewComposite annotationComposite,
			AnnotationTreeNode annotationTreeNode, boolean fragmentAnnotation, boolean update) {
		return new TagEnabledAnnotationForm(annotationComposite, tagCloud, annotationTreeNode,
				fragmentAnnotation, update);
	}
	
	@Override
	protected Panel createLinksPanel(boolean update, AnnotationTreeNode annotationTreeNode) {
		tagPanel = new FlowPanel();
		tagPanel.setStyleName("imageAnnotation-taglist");
		return tagPanel;
	}
	
	@Override
	protected Panel createSemanticLinksPanel(boolean update, AnnotationTreeNode annotationTreeNode) {
		return new FlowPanel();
	}
	
	@Override
	protected KeyDownHandler createKeyDownHandler(TreeViewComposite annotationComposite) {
		return new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
	            if (event.getNativeKeyCode() == ' ' || event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) {
	            	enrichmentService.getTagSuggestions(textArea.getValue(), WHICH_ENRICHMENT_SERVICE, 
	            			new AsyncCallback<Collection<SemanticTagSuggestions>>() {
						@Override
						public void onSuccess(Collection<SemanticTagSuggestions> result) {
							if (result.size() > 0 && !tagCloud.isVisible()) tagCloud.show();
							for (SemanticTagSuggestions group : result) {
								for (SemanticTag tag : group.getAmbiguousTags()) {
									tagCloud.addTag(tag, TAG_FONT_SIZE, "#FFD77D");
								}
							}
						}
						
						@Override
						public void onFailure(Throwable t) {
							// Do nothing...
						}
					});
	            }
			}
		};
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
		annotationComposite.layout();
	}
	
	@Override
	public void removeTag(SemanticTag tag) {
		tagPanel.remove(tags.get(tag));
		tags.remove(tag);
		annotationComposite.layout();
	}
	
	@Override
	public List<SemanticTag> getSemanticTags() {
	    return new ArrayList<SemanticTag>(tags.keySet());
	}

}
