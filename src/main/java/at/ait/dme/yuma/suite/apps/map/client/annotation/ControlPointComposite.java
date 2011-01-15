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

package at.ait.dme.yuma.suite.apps.map.client.annotation;

import java.util.Set;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

import at.ait.dme.yuma.suite.apps.core.client.gui.events.CreateClickHandler;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationPanel;
import at.ait.dme.yuma.suite.apps.map.client.TileBasedImageViewer;
import at.ait.dme.yuma.suite.apps.map.client.YumaMapClient;

/**
 * Composite used to manage control points
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class ControlPointComposite extends AnnotationPanel {
	
	/**
	 * 'Create control point' button
	 */
	private PushButton createButton;

	public ControlPointComposite(TileBasedImageViewer imageComposite, 
			ControlPointForm imageAnnotationForm, Set<String> shapeTypes)   {
	
		super(imageComposite, imageAnnotationForm, shapeTypes);
	}

	@Override
	protected Widget createHeader() {
		// The parent header panel
		FlowPanel header = new FlowPanel();
		
		// 'Help geo-Reference' label
		Label addAnnotationLabel = new Label(YumaMapClient.getConstants().helpGeoreference());
		addAnnotationLabel.setStyleName("imageAnnotation-add-annotation");
		header.add(addAnnotationLabel);
		
		// 'Help' link
		HTML help = new HTML("<a target=\"_blank\" href=\"userguide_" + 
				LocaleInfo.getCurrentLocale().getLocaleName()+".html\">" + 
				YumaMapClient.getConstants().help() + "</a>" );
		help.setStyleName("imageAnnotation-help");
		header.add(help);		
		
		// Instructions text
		Label addAnnotationHint = new Label(YumaMapClient.getConstants().helpGeoreferenceHint()); 
		addAnnotationHint.setStyleName("imageAnnotation-add-annotation-hint");
		header.add(addAnnotationHint);
		
		// Button panel
		HorizontalPanel buttons = new HorizontalPanel();
		
		// 'Create Control Point' button
		createButton = new PushButton(YumaMapClient.getConstants().actionCreateCP());
		createButton.setStyleName("imageAnnotation-button");
		createButton.addClickHandler(new CreateClickHandler(this,null,false,false));
		createButton.setEnabled(!YumaMapClient.getUser().isEmpty());
		buttons.add(createButton);
		
		if (YumaMapClient.getImageUrl().startsWith("http://georeferencer"))
			createButton.setEnabled(false);
		
		header.add(buttons);
		
		// Placeholder for the annotation form 
		header.add(annotationFormPanel);
		
		return header;
	}
}
