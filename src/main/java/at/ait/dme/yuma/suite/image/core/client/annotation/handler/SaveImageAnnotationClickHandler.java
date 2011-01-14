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

package at.ait.dme.yuma.suite.image.core.client.annotation.handler;

import java.util.Date;

import at.ait.dme.yuma.suite.core.client.datamodel.Annotation;
import at.ait.dme.yuma.suite.core.client.datamodel.Annotation.MediaType;
import at.ait.dme.yuma.suite.image.client.EntryPointClass;
import at.ait.dme.yuma.suite.image.core.client.annotation.ImageAnnotation;
import at.ait.dme.yuma.suite.image.core.client.annotation.ImageAnnotationComposite;
import at.ait.dme.yuma.suite.image.core.client.annotation.ImageAnnotationForm;
import at.ait.dme.yuma.suite.image.core.client.annotation.ImageAnnotationTreeNode;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * click listener to save annotations
 * 
 * @author Christian Sadilek
 */
public class SaveImageAnnotationClickHandler extends ImageAnnotationClickHandler {

	// reference to the annotation form to retrieve title and text 
	private ImageAnnotationForm annotationForm;
	
	public SaveImageAnnotationClickHandler(ImageAnnotationComposite annotationComposite,
			ImageAnnotationTreeNode annotationTreeNode, ImageAnnotationForm annotationForm) {
		
		super(annotationComposite, annotationTreeNode);
		this.annotationForm = annotationForm;
	}		
	
	public void onClick(ClickEvent event) {
		final ImageAnnotationComposite annotationComposite=getAnnotationComposite();
		annotationComposite.enableLoadingImage();		
		((FocusWidget)event.getSource()).setEnabled(false);

		ImageAnnotationTreeNode annotationTreeNode = getAnnotationTreeNode();
		String parentId = null, rootId = null;

		// check if the new annotation is a reply
		if(annotationTreeNode!=null) {
			// if the parent is a reply too it has a rootId		
			// if not the parent is also the root for new annotation
			parentId = annotationTreeNode.getAnnotationId();
			rootId = annotationTreeNode.getAnnotationRootId();
			if(rootId==null) rootId = parentId;
		}
		
		// create the new annotation
		Date timestamp = new Date();
		ImageAnnotation a = new ImageAnnotation();
		a.setObjectUri(EntryPointClass.getImageUrl());
		a.setParentId(parentId);
		a.setRootId(rootId);
		a.setCreated(timestamp);
		a.setLastModified(timestamp);
		a.setCreatedBy(EntryPointClass.getUser());
		a.setMediaType(MediaType.IMAGE);
		a.setTitle(annotationForm.getAnnotationTitle());
		a.setText(annotationForm.getAnnotationText());
		a.setScope(annotationForm.getAnnotationScope());
		a.setTags(annotationForm.getSemanticTags());
		
		// create the fragment if necessary 
		addFragment(a);
		
		// now save the annotation
		getImageAnnotationService().createAnnotation(a,
			new AsyncCallback<Annotation>() {
			
				public void onFailure(Throwable caught) {
					handleFailure(caught, errorMessages.failedToSaveAnnotation());					
				}

				// on success add the annotation to the tree
				public void onSuccess(Annotation result) {
					ImageAnnotationTreeNode treeNode = getAnnotationTreeNode();
					ImageAnnotation parent = (treeNode == null) ? null : treeNode.getAnnotation();
					annotationComposite.hideAnnotationForm(treeNode, false);
					annotationComposite.disableLoadingImage();
					annotationComposite.addAnnotation((ImageAnnotation) result, parent);
				}
			}
		);
	}	
}
