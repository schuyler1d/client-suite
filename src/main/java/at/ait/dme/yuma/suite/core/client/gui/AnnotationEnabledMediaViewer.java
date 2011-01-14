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

package at.ait.dme.yuma.suite.core.client.gui;

import at.ait.dme.yuma.suite.core.client.datamodel.Annotation;
import at.ait.dme.yuma.suite.core.client.datamodel.MediaFragment;
import at.ait.dme.yuma.suite.core.client.gui.events.selection.AnnotationSelectionEvent;
import at.ait.dme.yuma.suite.core.client.gui.events.selection.AnnotationSelectionHandler;
import at.ait.dme.yuma.suite.core.client.gui.events.selection.HasAnnotationSelectionHandlers;

import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

/**
 * Base class for image composites
 * 
 * @author Christian Sadilek
 */
public abstract class AnnotationEnabledMediaViewer extends Composite implements 
		HasAnnotationSelectionHandlers, HasLoadHandlers {
	
	final protected HandlerManager handlerManager = new HandlerManager(this);	
	
	@Override
	public HandlerRegistration addImageAnnotationSelectionHandler(
			AnnotationSelectionHandler handler) {
		return handlerManager.addHandler(AnnotationSelectionEvent.getType(), handler);
	}
	
	public abstract MediaFragment getMediaFragment();		
	public abstract void showAnnotation(Annotation annotation);	
	public abstract void selectAnnotation(Annotation annotation, boolean selected);
	public abstract void hideAnnotation(Annotation annotation);
	
	public abstract void editAnnotation(Annotation annotation, boolean forceVisible);
	public abstract void stopEditing();	
		
}
