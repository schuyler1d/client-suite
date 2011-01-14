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

import org.gwt.mosaic.ui.client.MessageBox;

import at.ait.dme.yuma.suite.apps.core.client.I18NErrorMessages;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.Annotation;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.MediaFragment;
import at.ait.dme.yuma.suite.apps.core.client.gui.MediaViewer;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationTreeNode;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.TreeViewComposite;
import at.ait.dme.yuma.suite.apps.core.client.server.RESTfulServiceException;
import at.ait.dme.yuma.suite.apps.core.client.server.annotation.AnnotationService;
import at.ait.dme.yuma.suite.apps.core.client.server.annotation.AnnotationServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * base click listener class
 * 
 * @author Christian Sadilek
 */
public abstract class BaseClickHandler implements ClickHandler {
	
	private TreeViewComposite annotationComposite = null;
	
	private AnnotationTreeNode annotationTreeNode = null;

	protected I18NErrorMessages errorMessages = (I18NErrorMessages) GWT.create(I18NErrorMessages.class);
	
	public BaseClickHandler(TreeViewComposite annotationComposite, AnnotationTreeNode annotationTreeNode) {
		this.annotationComposite=annotationComposite;
		this.annotationTreeNode=annotationTreeNode;
	}
	
	/**
	 * returns the corresponding tree node (the tree node on which
	 * the button was clicked).
	 * 
	 * @return image annotation tree node
	 */
	protected AnnotationTreeNode getAnnotationTreeNode() {
		return annotationTreeNode;
	}

	/**
	 * returns the annotation composite. used to add and remove tree nodes
	 * and to update the annotation tree.
	 * 
	 * @return image annotation composite
	 */
	protected TreeViewComposite getTreeViewComposite() {
		return annotationComposite;
	}
		
	/**
	 * returns a reference to the image annotation service used by all click
	 * listeners to create/update/delete annotations.
	 *  
	 * @return ImageAnnotationServiceAsync reference to the image annotation service
	 */
	protected AnnotationServiceAsync getAnnotationService() {
		AnnotationServiceAsync annotationService = 
			(AnnotationServiceAsync) GWT.create(AnnotationService.class);
		return annotationService;
	}
	
	/**
	 * add the active fragment to the given annotation
	 * 
	 * @param annotation
	 */
	protected void addFragment(Annotation annotation) {
		MediaViewer imageComposite=annotationComposite.getImageComposite();
		MediaFragment fragment = imageComposite.getActiveMediaFragment();
		if(fragment == null) {
			// fragment = new MediaFragment(new VoidShape());
		}
		annotation.setFragment(fragment);
	}
	/**
	 * handle failures. in case of a conflict reload the application. 
	 * 
	 * @param caught
	 * @param defaultMessage
	 */
	protected void handleFailure(Throwable caught, String defaultMessage) {
		I18NErrorMessages errorMessages = (I18NErrorMessages) GWT.create(I18NErrorMessages.class);
		try {
			throw caught;
		} catch (RESTfulServiceException rse) {
			if(rse.isConflict()) {
				MessageBox.error(errorMessages.error(), errorMessages.annotationConflict());
				annotationComposite.refreshTree();								
			} else {
				MessageBox.error(errorMessages.error(), defaultMessage);
			}
		} catch (Throwable t) {
			MessageBox.error(errorMessages.error(), defaultMessage);
		}									
	}
}
