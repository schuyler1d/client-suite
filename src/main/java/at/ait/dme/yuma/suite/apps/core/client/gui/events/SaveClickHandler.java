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

import at.ait.dme.yuma.suite.apps.core.client.datamodel.Annotation;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationEditForm;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * click listener to save annotations
 * 
 * @author Christian Sadilek
 */
public class SaveClickHandler extends AbstractClickHandler {
	
	private Annotation parent;
	
	public SaveClickHandler(AnnotationPanel panel, Annotation parent, AnnotationEditForm editForm) {
		super(panel, null, editForm);
		this.parent = parent;
	}	
	
	public void onClick(ClickEvent event) {
		panel.enableLoadingImage();		
		((FocusWidget)event.getSource()).setEnabled(false);
		
		getAnnotationService().createAnnotation(editForm.getAnnotation(),
			new AsyncCallback<Annotation>() {
			
				public void onFailure(Throwable caught) {
					handleFailure(caught, errorMessages.failedToSaveAnnotation());					
				}

				public void onSuccess(Annotation result) {
					panel.stopEditing(parent, false);
					panel.appendChild(parent, result);
					panel.refresh();
					panel.disableLoadingImage();
				}
			}
		);
	}	
}
