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

import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationTreeNode;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * click listener to delete an annotation
 * 
 * @author Christian Sadilek
 */
public class DeleteClickHandler extends BaseClickHandler {
	
	public DeleteClickHandler(AnnotationPanel annotationComposite,
			AnnotationTreeNode annotationTreeNode) {
		super(annotationComposite, annotationTreeNode);
	}
	
	public void onClick(ClickEvent event) {
		final AnnotationPanel annotationComposite=getTreeViewComposite();
		annotationComposite.enableLoadingImage();				
		((FocusWidget)event.getSource()).setEnabled(false);
		
		// YumaImageClient.authenticateAs(getAnnotationTreeNode().getAnnotation().getCreatedBy());
		
		getAnnotationService().deleteAnnotation(getAnnotationTreeNode().getAnnotationId(),
			new AsyncCallback<Void>() {
				public void onFailure(Throwable caught) {
					handleFailure(caught, errorMessages.failedToDeleteAnnotation());
				}

				public void onSuccess(Void result) {
					annotationComposite.removeAnnotation(getAnnotationTreeNode());
					annotationComposite.disableLoadingImage();
				}
			});
	}	
}