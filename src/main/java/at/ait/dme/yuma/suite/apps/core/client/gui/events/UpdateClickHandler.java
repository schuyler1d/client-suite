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

package at.ait.dme.yuma.suite.apps.core.client.gui.events;

import java.util.Date;

import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.Annotation;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationEditForm;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationTreeNode;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationPanel;
import at.ait.dme.yuma.suite.apps.image.core.client.ImageAnnotation;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * click listener for annotation updates
 * 
 * @author Christian Sadilek
 */
public class UpdateClickHandler extends BaseClickHandler {
	// reference to the annotation form to retrieve title and text 
	private AnnotationEditForm annotationForm;
	
	public UpdateClickHandler(AnnotationPanel annotationComposite,
			AnnotationTreeNode annotationTreeNode, AnnotationEditForm annotationForm) {
		
		super(annotationComposite, annotationTreeNode);
		this.annotationForm = annotationForm;
	}		
	
	public void onClick(ClickEvent event) {
		final AnnotationPanel annotationComposite=getTreeViewComposite();
		annotationComposite.enableLoadingImage();				
		((FocusWidget)event.getSource()).setEnabled(false);

		// create a new annotation
		ImageAnnotation a = new ImageAnnotation();
	
		AnnotationTreeNode node = getAnnotationTreeNode();
		if (node != null) {
			a.setId(node.getAnnotationId());
			a.setParentId(node.getParentAnnotationId());
			a.setRootId(node.getAnnotationRootId());
		}

		a.setObjectUri(YUMACoreProperties.getObjectURI());
		a.setCreatedBy(YUMACoreProperties.getUser());
		a.setTitle(annotationForm.getAnnotationTitle());
		a.setText(annotationForm.getAnnotationText());
		a.setScope(annotationForm.getAnnotationScope());
		a.setMediaType(getAnnotationTreeNode().getAnnotation().getMediaType());
		a.setTags(annotationForm.getSemanticTags());
		a.setCreated(getAnnotationTreeNode().getAnnotation().getCreated());
		a.setLastModified(new Date());
		
		// create the fragment if necessary
		addFragment(a);
		
		// update the annotation on the server
		getAnnotationService().updateAnnotation(a,
			new AsyncCallback<Annotation>() {
				public void onFailure(Throwable caught) {
					handleFailure(caught, errorMessages.failedToSaveAnnotation());				
				}

				// on success update the annotation in the tree
				public void onSuccess(Annotation result) {
					if (getAnnotationTreeNode() != null)
						result.setReplies(getAnnotationTreeNode().getAnnotation().getReplies());

					annotationComposite.removeAnnotation(getAnnotationTreeNode());
					annotationComposite.addAnnotation((ImageAnnotation) result, getAnnotationTreeNode().
							getParentAnnotation());
					annotationComposite.hideAnnotationForm(getAnnotationTreeNode(), false);
					annotationComposite.disableLoadingImage();
				}
			}
		);
	}	
}
