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

package at.ait.dme.yuma.suite.apps.image.core.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.Annotation;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.Annotation.MediaType;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.Annotation.Scope;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.SemanticTag;
import at.ait.dme.yuma.suite.apps.core.client.gui.events.CancelClickHandler;
import at.ait.dme.yuma.suite.apps.core.client.gui.events.SaveClickHandler;
import at.ait.dme.yuma.suite.apps.core.client.gui.events.UpdateClickHandler;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationEditForm;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationPanel;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationTreeNode;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * form to create and update annotations
 * 
 * @author Christian Sadilek
 * @author Manuel Gay
 * @author Rainer Simon
 */
public class StandardImageAnnotationForm extends AnnotationEditForm {
	
	public class Checkbox extends com.google.gwt.user.client.ui.CheckBox {
	    
	    private Object resource;

	    public Object getResource() {
	        return resource;
	    }

	    public void setResource(Object resource) {
	        this.resource = resource;
	    }
	}
	
	private static final String SCOPE_RADIO_GROUP_NAME = "scope";
	
	private VerticalPanel formPanel = new VerticalPanel();
	private TextBox titleTextBox = new TextBox();	
	protected TextArea textArea = new TextArea();
	private RadioButton rdPublic, rdPrivate = null;

	private List<SemanticTag> tags = new ArrayList<SemanticTag>();
    private VerticalPanel linksStack;
    
    protected MediaType mediaType;

    public StandardImageAnnotationForm(MediaType mediaType) {
    	super();
    	this.mediaType = mediaType;
    }
    
	public StandardImageAnnotationForm(AnnotationPanel panel, AnnotationTreeNode annotation, 
			AnnotationTreeNode parent, MediaType mediaType) {
		
		super(panel, annotation, parent);
		this.mediaType = mediaType;
		
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
		
		return new StandardImageAnnotationForm(panel, annotation, parent, mediaType);
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
	    
        linksStack = new VerticalPanel();
        linksStack.setStyleName("imageAnnotation-form-links");
        linksPanel.add(linksStack);
        
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
		a.setCreatedBy(YUMACoreProperties.getUser());
		a.setMediaType(mediaType);
		a.setTitle(titleTextBox.getText());
		a.setText(textArea.getText());

		if (rdPublic.getValue()) {
			a.setScope(Scope.PUBLIC);
		} else {
			a.setScope(Scope.PRIVATE);
		}
		
		a.setTags(tags);
		a.setFragment(panel.getMediaViewer().getActiveMediaFragment());		
		
		return a;
	}
	
	@Override
	public void addTag(SemanticTag t) {
	    this.tags.add(t);
	}
	
	@Override
	public void removeTag(SemanticTag t) {
	    this.tags.remove(t);
	}

}
