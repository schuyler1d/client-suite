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

package at.ait.dme.yuma.suite.apps.core.client.gui.treeview;

import at.ait.dme.yuma.suite.apps.core.client.datamodel.Annotation;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.SemanticTag;

import com.google.gwt.user.client.ui.Composite;

/**
 * Base class for image annotation forms
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public abstract class NewAnnotationEditForm extends Composite {
	
	/**
	 * Reference to the annotation panel
	 */
	protected NewAnnotationPanel panel = null;
	
	/**
	 * The annotation to edit or null, if new annotation
	 */
	protected Annotation annotation = null;
	
	/**
	 * The parent of this annotation or null, if it's a root annotation
	 */
	protected Annotation parent = null;

	public NewAnnotationEditForm() { }

	public NewAnnotationEditForm(NewAnnotationPanel panel, Annotation annotation, Annotation parent) {
		this.panel = panel;
		this.annotation = annotation;
		this.parent = parent;
	}
	
	public abstract NewAnnotationEditForm newInstance(NewAnnotationPanel panel, Annotation annotation, Annotation parent);
	
	public abstract void layout();

	public abstract Annotation getAnnotation();
	
	public Annotation getParentAnnotation() {
		return parent;
	}
	
	public abstract void addTag(SemanticTag t);
	
	public abstract void removeTag(SemanticTag t);
	
}
