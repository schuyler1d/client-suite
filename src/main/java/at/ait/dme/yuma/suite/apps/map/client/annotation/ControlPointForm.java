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

import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationEditForm;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationPanel;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationTreeNode;
import at.ait.dme.yuma.suite.apps.core.shared.datamodel.Annotation;
import at.ait.dme.yuma.suite.apps.core.shared.datamodel.SemanticTag;
import at.ait.dme.yuma.suite.apps.core.shared.datamodel.Annotation.Scope;
import at.ait.dme.yuma.suite.apps.image.core.shared.datamodel.ImageAnnotation;
import at.ait.dme.yuma.suite.apps.image.core.shared.datamodel.ImageFragment;
import at.ait.dme.yuma.suite.apps.image.core.shared.shape.GeoPoint;
import at.ait.dme.yuma.suite.apps.map.shared.geo.WGS84Coordinate;
import at.ait.dme.yuma.suite.apps.map.shared.rpc.GeocoderService;
import at.ait.dme.yuma.suite.apps.map.shared.rpc.GeocoderServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A form for creating/editing control points 
 * 
 * @author Rainer Simon
 */
public class ControlPointForm extends AnnotationEditForm {
	
	/**
	 * Place name
	 */
	private TextBox placeName;
	
	/**
	 * Lon/Lat textboxes
	 */
	private TextBox lon;
	private TextBox lat;
	
	/**
	 * X/Y textbox
	 */
	private TextBox xy;

	/**
	 * Reference to the control point layer
	 */
	private ControlPointLayer controlPointLayer;
	
	public ControlPointForm(ControlPointLayer controlPointLayer) {
		this.controlPointLayer = controlPointLayer;
	}
	
	public ControlPointForm(AnnotationPanel panel, AnnotationTreeNode annotation, ControlPointLayer controlPointLayer) {
		// Reference to control point layer
		this.controlPointLayer = controlPointLayer;
		
		// Place name (will be geo-coded)
		HorizontalPanel placeNamePanel = new HorizontalPanel();
		
		Label placeNameLabel = new Label("Place: ");
		placeNameLabel.setStyleName("cp-Editor-Label");
		placeNamePanel.add(placeNameLabel);
		
		placeName = new TextBox();
		placeName.setStyleName("cp-Editor-Field");
		placeName.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == ' ') {
				 doAsyncGeocoding(placeName.getText() + event.getCharCode());
				}
			}
		});
			
		placeNamePanel.add(placeName);	
		
		// Lon (determined automatically - field disabled)
		HorizontalPanel lonPanel = new HorizontalPanel();
		
		Label lonLabel = new Label("Lon: ");
		lonLabel.setStyleName("cp-Editor-Label");
		lonPanel.add(lonLabel);
		
		lon = new TextBox();
		lon.setEnabled(false);
		lon.setStyleName("cp-Editor-Field");
		lonPanel.add(lon);	
		
		// Lat (determined automatically - field disabled)
		HorizontalPanel latPanel = new HorizontalPanel();
		
		Label latLabel = new Label("Lat: ");
		latLabel.setStyleName("cp-Editor-Label");
		latPanel.add(latLabel);
		
		lat = new TextBox();
		lat.setEnabled(false);
		lat.setStyleName("cp-Editor-Field");
		latPanel.add(lat);	
		
		// X/Y (determined automatically - field disabled)
		HorizontalPanel xyPanel = new HorizontalPanel();
		
		Label xyLabel = new Label("X/Y: ");
		xyLabel.setStyleName("cp-Editor-Label");
		xyPanel.add(xyLabel);
		
		xy = new TextBox();
		xy.setEnabled(false);
		xy.setStyleName("cp-Editor-Field");
		xyPanel.add(xy);
	
		if (annotation != null) {
			placeName.setText(annotation.getTitle());
			GeoPoint p = (GeoPoint) ((ImageFragment)annotation.getAnnotation().getFragment()).getShape();
			lon.setText(Double.toString(p.getLng()));
			lat.setText(Double.toString(p.getLat()));
			setXY(p.getX(), p.getY());
		}
		
		// Assemble the main FlowPanel
		FlowPanel form = new FlowPanel();
		form.setStyleName("cp-Editor");
		form.add(placeNamePanel);
		form.add(lonPanel);
		form.add(latPanel);
		form.add(xyPanel);
		form.add(createButtonsPanel(panel, annotation.getAnnotation()));
		form.setStyleName("imageAnnotation-form");		
		initWidget(form);
		
		controlPointLayer.showActiveFragmentPanel(((ImageAnnotation)annotation.getAnnotation()), false);
	}
	
	private HorizontalPanel createButtonsPanel(AnnotationPanel panel, Annotation annotation) {
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		
		PushButton saveButton = new PushButton(YUMACoreProperties.getConstants().actionSave());
		/*
		if(update) {
			saveButton.addClickHandler(new UpdateClickHandler(annotationComposite, 
					annotationTreeNode, this));
		} else {
			saveButton.addClickHandler(new SaveClickHandler(annotationComposite, 
					annotationTreeNode, this));
		}
		*/
		saveButton.setStyleName("imageAnnotation-form-button");
		buttonsPanel.add(saveButton);
		
		PushButton cancelButton = new PushButton(YUMACoreProperties.getConstants().actionCancel());
		cancelButton.setStyleName("imageAnnotation-form-button");
		/*
		cancelButton.addClickHandler(new CancelClickHandler(annotationComposite,
				annotationTreeNode));
		*/
		buttonsPanel.add(cancelButton);
		
		return buttonsPanel;
	}
	
	@Override
	public AnnotationEditForm newInstance(AnnotationPanel panel, 
			AnnotationTreeNode annotation, AnnotationTreeNode parent) {
		
		return new ControlPointForm(panel, annotation, controlPointLayer);
	}
	
	private void doAsyncGeocoding(String place) {
		GeocoderServiceAsync service = (GeocoderServiceAsync) GWT.create(GeocoderService.class);
		service.getCoordinate(place.trim(), new AsyncCallback<WGS84Coordinate>(){
		    public void onSuccess(WGS84Coordinate c) {
		    	if (c != null) {
			    	lat.setValue(Double.toString(c.lat));
			    	lon.setValue(Double.toString(c.lon));
		    	}
		    }
		    
			public void onFailure(Throwable t) {
				// Do nothing (textfield won't update)
			}
		});
		
	}
	
	public double getLat() {
		return Double.parseDouble(lat.getValue());
	}
	
	public double getLon() {
		return Double.parseDouble(lon.getValue());
	}
	
	public void setXY(int x, int y) {
		xy.setValue(x + "/" + y);
	}

	@Override
	public Annotation getAnnotation() {
		Annotation a = new ImageAnnotation();
		a.setTitle(placeName.getValue());
		a.setText("x/y:" + xy.getText() + "<br/>Lat=" + lat.getText() + ", Lon=" + lon.getText());
		a.setScope(Scope.PUBLIC);
		return a;
	}

	@Override
	public void layout() { }

	@Override
	public void addTag(SemanticTag t) { }

	@Override
	public void removeTag(SemanticTag t) { }

}
