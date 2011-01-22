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

package at.ait.dme.yuma.suite.apps.image.core.client.treeview;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import at.ait.dme.yuma.suite.apps.core.client.User;
import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.Annotation;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.Annotation.MediaType;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.Annotation.Scope;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.SemanticTag;
import at.ait.dme.yuma.suite.apps.core.client.events.CancelClickHandler;
import at.ait.dme.yuma.suite.apps.core.client.events.SaveClickHandler;
import at.ait.dme.yuma.suite.apps.core.client.events.UpdateClickHandler;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationEditForm;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationPanel;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationTreeNode;
import at.ait.dme.yuma.suite.apps.image.core.client.datamodel.ImageAnnotation;
import at.ait.dme.yuma.suite.apps.image.core.client.tagcloud.TagCloud;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Image-specific sub-class of the AnnotationEdit form.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class ImageAnnotationEditForm extends AnnotationEditForm {
	
	private static final String SCOPE_RADIO_GROUP_NAME = "scope";
	
	private VerticalPanel formPanel = new VerticalPanel();

	private TextBox titleTextBox = new TextBox();	
	private TextArea textArea = new TextArea();
	
	private RadioButton rdPublic, rdPrivate = null;

	public HashMap<SemanticTag, Widget> tags = new HashMap<SemanticTag, Widget>(); 
	
	private FlowPanel tagPanel;
    
    private MediaType mediaType;
    
	private TagCloud tagCloud;

    public ImageAnnotationEditForm(MediaType mediaType, TagCloud tagCloud) {
    	super();
    	
    	this.mediaType = mediaType;
    	this.tagCloud = tagCloud;
    }
    
	public ImageAnnotationEditForm(AnnotationPanel panel, 
			AnnotationTreeNode annotation, AnnotationTreeNode parent,
			MediaType mediaType, TagCloud tagCloud) {
		
		super(panel, annotation, parent);
		
		this.mediaType = mediaType;
		this.tagCloud = tagCloud;
		
		panel.getMediaViewer().setAnnotationEditForm(this);
		if (annotation != null && annotation.getAnnotation().hasTags()) {
			for (SemanticTag t : annotation.getAnnotation().getTags()) {
				addTag(t);
			}
		}
		
    	formPanel.setStyleName("imageAnnotation-form");		
		formPanel.add(createTitlePanel());
		formPanel.add(createTextPanel());
		formPanel.add(createScopePanel());
		formPanel.add(createTagPanel());
		formPanel.add(createButtonsPanel());	
	 	initWidget(formPanel);
	}
	
	@Override
	public AnnotationEditForm newInstance(AnnotationPanel panel, AnnotationTreeNode annotation,
			AnnotationTreeNode parent) {
		
		return new ImageAnnotationEditForm(panel, annotation, parent, mediaType, tagCloud);
	}

	/**
	 * Workaround: for some reason, radio buttons are reset during GUI layout.
	 * This workaround restores their state directly after layout.
	 */
	@Override
	public void layout() {
		if (rdPublic != null) {
			final boolean pub = rdPublic.getValue();
			final boolean prv = rdPrivate.getValue();	
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					rdPublic.setValue(pub);
					rdPrivate.setValue(prv);			
				}
			});
		}
	}

	protected HorizontalPanel createTitlePanel() {
		HorizontalPanel titlePanel = new HorizontalPanel();
		
		Label titleLabel = new Label(YUMACoreProperties.getConstants().annotationTitle());
		titleLabel.setStyleName("imageAnnotation-form-label");
		
		titleTextBox.setStyleName("imageAnnotation-form-title");
		if (annotation == null && parent != null) {
			titleTextBox.setText("RE: " + parent.getAnnotation().getTitle());
		} else if (annotation != null) { 
			titleTextBox.setText(annotation.getAnnotation().getTitle());
		}
			
		titlePanel.add(titleLabel);		
		titlePanel.add(titleTextBox);
		return titlePanel;		
	}
	
	protected HorizontalPanel createTextPanel() {
		HorizontalPanel textPanel = new HorizontalPanel();
		
		Label textLabel = new Label(YUMACoreProperties.getConstants().annotationText());
		textLabel.setStyleName("imageAnnotation-form-label");		
		
		textArea.setStyleName("imageAnnotation-form-text");
		if (annotation != null)
			textArea.setText(annotation.getAnnotation().getText());
		
		textPanel.add(textLabel);
		textPanel.add(textArea);
		
		return textPanel;
	}
	
	protected HorizontalPanel createScopePanel() {
	    HorizontalPanel radioPanel = new HorizontalPanel();

		Label scopeLabel = new Label();
		scopeLabel.setStyleName("imageAnnotation-form-label");         
	     
        rdPublic = new RadioButton(SCOPE_RADIO_GROUP_NAME,
                " " + YUMACoreProperties.getConstants().publicScope());
        rdPublic.setStyleName("imageAnnotation-form-radiobutton");
        
        rdPrivate = new RadioButton(SCOPE_RADIO_GROUP_NAME,
                " " + YUMACoreProperties.getConstants().privateScope());
        rdPrivate.setStyleName("imageAnnotation-form-radiobutton");     
        		
		if (annotation != null && annotation.getAnnotation().getScope() == Scope.PRIVATE) {
			rdPrivate.setValue(true, true);
		} else {
			rdPublic.setValue(true, true);
		}       

        radioPanel.add(scopeLabel);
        radioPanel.add(rdPublic);
        radioPanel.add(rdPrivate);
        
		return radioPanel;
	}
	
	protected Panel createTagPanel() {
	    HorizontalPanel linksPanel = new HorizontalPanel();
	    
	    Label linksLabel = new Label(YUMACoreProperties.getConstants().annotationLinks());
	    linksLabel.setStyleName("imageAnnotation-form-label");      
        linksPanel.add(linksLabel);
        
        if(annotation != null && annotation.getAnnotation().hasTags()) {
            for(SemanticTag t: annotation.getAnnotation().getTags()) {
                addTag(t);
            }
        }
	    
	    return linksPanel;
	}
	
	protected HorizontalPanel createButtonsPanel() {
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		
		PushButton btnSave = new PushButton(YUMACoreProperties.getConstants().actionSave());
		btnSave.setStyleName("imageAnnotation-form-button");
		if (annotation == null) {
			btnSave.addClickHandler(new SaveClickHandler(panel, parent, this));	
		} else {
			btnSave.addClickHandler(new UpdateClickHandler(panel, annotation, parent, this));
		}
		buttonsPanel.add(btnSave);
		
		PushButton btnCancel = new PushButton(YUMACoreProperties.getConstants().actionCancel());
		btnCancel.setStyleName("imageAnnotation-form-button");
		if (annotation == null) {
			btnCancel.addClickHandler(new CancelClickHandler(panel, parent));
		} else {
			btnCancel.addClickHandler(new CancelClickHandler(panel, annotation));
		}
		buttonsPanel.add(btnCancel);
		
		return buttonsPanel;
	}
	
	@Override
	public Annotation getAnnotation() {
		Date timestamp = new Date();
		
		Annotation a = new ImageAnnotation();
		a.setObjectUri(YUMACoreProperties.getObjectURI());
		
		if (parent != null) {
			a.setParentId(parent.getAnnotation().getId());
		
			String rootId = parent.getAnnotation().getRootId();
			if (rootId == null)
				rootId = parent.getAnnotation().getId();
			a.setRootId(rootId);
		}
		
		if (annotation == null) {
			a.setCreated(timestamp);
		} else {
			a.setCreated(annotation.getAnnotation().getCreated());
		}
		
		a.setLastModified(timestamp);
		a.setCreatedBy(User.get());
		a.setMediaType(mediaType);
		a.setTitle(titleTextBox.getText());
		a.setText(textArea.getText());

		if (rdPublic.getValue()) {
			a.setScope(Scope.PUBLIC);
		} else {
			a.setScope(Scope.PRIVATE);
		}
		
		a.setTags(new ArrayList<SemanticTag>(tags.keySet()));		
		a.setFragment(panel.getMediaViewer().getActiveMediaFragment());		
		return a;
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
