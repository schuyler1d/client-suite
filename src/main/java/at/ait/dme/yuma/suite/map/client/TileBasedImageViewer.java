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

package at.ait.dme.yuma.suite.map.client;

import org.gwt.mosaic.ui.client.MessageBox;

import at.ait.dme.yuma.suite.core.client.I18NErrorMessages;
import at.ait.dme.yuma.suite.core.client.datamodel.Annotation;
import at.ait.dme.yuma.suite.core.client.gui.MediaViewer;
import at.ait.dme.yuma.suite.core.client.gui.LoadingPopup;
import at.ait.dme.yuma.suite.image.core.client.ImageAnnotation;
import at.ait.dme.yuma.suite.image.core.client.ImageFragment;
import at.ait.dme.yuma.suite.image.core.client.ImageRect;
import at.ait.dme.yuma.suite.image.core.client.shape.GeoPoint;
import at.ait.dme.yuma.suite.image.core.client.shape.Shape;
import at.ait.dme.yuma.suite.image.core.client.tagcloud.TagCloud;
import at.ait.dme.yuma.suite.image.core.client.tagcloud.annotation.TagEnabledAnnotationForm;
import at.ait.dme.yuma.suite.map.client.annotation.AnnotationLayer;
import at.ait.dme.yuma.suite.map.client.annotation.ControlPointLayer;
import at.ait.dme.yuma.suite.map.client.explore.KMLLayer;
import at.ait.dme.yuma.suite.map.client.explore.SearchLayer;
import at.ait.dme.yuma.suite.map.client.server.TilesetService;
import at.ait.dme.yuma.suite.map.client.server.TilesetServiceAsync;
import at.ait.dme.yuma.suite.map.client.server.exception.TilesetNotAvailableException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;

/**
 * 'Searchable Map' map GUI component based on OpenLayers.
 * 
 * @author Rainer Simon
 */
public class TileBasedImageViewer extends MediaViewer {
		
	/**
	 * The main GUI panel
	 */
	private AbsolutePanel panel;
	
	/**
	 * Image extent (x direction)
	 */
	private int xExtent;
	
	/**
	 * Image extent (y direction)
	 */
	private int yExtent;
	
	/**
	 * Layers
	 */
	private AnnotationLayer lAnnotation;
	private ControlPointLayer lControlPoints;
	private KMLLayer lKml;
	private SearchLayer lSearch;
	
	/**
	 * 'Loading' popup
	 */
	private LoadingPopup loadMask;

	public TileBasedImageViewer(String mapUrl) {
		panel = new AbsolutePanel();
		panel.setSize("100%", "100%");
		
		loadMask = new LoadingPopup("Loading Map...");
		loadMask.show();
		
		loadTileset(mapUrl);
		
		initWidget(panel); 
        DOM.setStyleAttribute(panel.getElement(), "zIndex", "1");
	}
	
	public void init(final Tileset ts) {
		loadMask.hide();
		this.xExtent = ts.getWidth();
		this.yExtent = ts.getHeight();
		
		panel.add(createMap(ts));
        
		LoadEvent.fireNativeEvent(Document.get().createLoadEvent(), this);	
	}
	
	public MapComponent createMap(Tileset tileset) {	
		// Map
		MapComponent mapComponent = new MapComponent(tileset);
		
		// Annotation layer
		lAnnotation = new AnnotationLayer("annotations", mapComponent, handlerManager, true);
		lAnnotation.setVisibility(true);
		
		// Control Point layer
		lControlPoints = new ControlPointLayer("controlpoints", mapComponent, handlerManager);
		lControlPoints.setVisibility(false);
		
		// KML overlay layer
		lKml = new KMLLayer(mapComponent);
		
		// Search layer
		lSearch = new SearchLayer(mapComponent);
		
		return mapComponent;
	}
	
	public ControlPointLayer getControlPointLayer() {
		return lControlPoints;
	}
	
	public AnnotationLayer getAnnotationLayer() {
		return lAnnotation;
	}
	
	public KMLLayer getKMLLayer() {
		return lKml;
	}
	
	public SearchLayer getSearchLayer() {
		return lSearch;
	}
	
	public TagCloud getTagCloud() {
		return lAnnotation.getTagCloud();
	}
	
	public void setAnnotationForm(TagEnabledAnnotationForm annotationForm) {
		lAnnotation.setAnnotationForm(annotationForm);
	}
	
	public void showAnnotationLayer() {
		lAnnotation.setVisibility(true);
		lControlPoints.setVisibility(false);
	}
	
	public void showControlPointLayer() {
		lAnnotation.setVisibility(false);
		lControlPoints.setVisibility(true);		
	}
	
	@Override
	public HandlerRegistration addLoadHandler(LoadHandler loadHandler) {
	    if(loadHandler==null) return null;
	    return addHandler(loadHandler,LoadEvent.getType());
	}
	
	@Override
	public void editAnnotation(Annotation annotation) {
		if (lControlPoints.isVisible()) {
			lControlPoints.showActiveFragmentPanel((ImageAnnotation) annotation, true);
		} else {
			lAnnotation.showActiveFragmentPanel((ImageAnnotation) annotation, true);
		}
	}
	
	@Override
	public void stopEditing() {
		if (lControlPoints.isVisible()) {
			lControlPoints.hideActiveFragmentPanel();
		} else {
			lAnnotation.hideActiveFragmentPanel();
		}
	}

	@Override
	public void selectAnnotation(Annotation annotation, boolean selected) {
		if (annotation.getFragment() != null && ((ImageFragment) annotation.getFragment()).getShape() instanceof GeoPoint) {
			lControlPoints.selectFragment((ImageAnnotation) annotation, selected);
		} else {
			lAnnotation.selectFragment((ImageAnnotation) annotation, selected);
		}
	}
	
	@Override
	public void showAnnotation(Annotation annotation) {
		if (((ImageFragment)annotation.getFragment()).getShape() instanceof GeoPoint) {
			lControlPoints.showFragment((ImageAnnotation) annotation);
		} else {
			lAnnotation.showFragment((ImageAnnotation) annotation);
		}
	}
	
	@Override
	public void hideAnnotation(Annotation annotation) {
		if (((ImageFragment)annotation.getFragment()).getShape() instanceof GeoPoint) {
			lControlPoints.hideFragment((ImageAnnotation) annotation);
		} else {
			lAnnotation.hideFragment((ImageAnnotation) annotation);
		}
	}

	@Override
	public ImageFragment getActiveMediaFragment() {
		if (lControlPoints.isVisible()) {
			return null; //lControlPoints.getActiveShape();
		} else {
			return null; // lAnnotation.getActiveShape();
		}
	}
		
	private void loadTileset(final String url) {
		final TilesetServiceAsync tileService = (TilesetServiceAsync) GWT
			.create(TilesetService.class);
	
		tileService.getTileset(url, new AsyncCallback<Tileset>() {
			
			public void onFailure(Throwable t) {					
				try { 
					throw t;
				} catch (TilesetNotAvailableException e) {
					if (url.toLowerCase().endsWith("xml")) {
						// Tileset scheme supported, but URL broken or remote connection down!
						loadMask.hide();
						I18NErrorMessages errorMessages = (I18NErrorMessages) GWT.create(I18NErrorMessages.class);
						MessageBox.error(errorMessages.error(), t.getMessage());
					} else {
						startOnTheFlyTiler(url);
					}
				} catch (Throwable other) {
					loadMask.hide();
					I18NErrorMessages errorMessages = (I18NErrorMessages) GWT.create(I18NErrorMessages.class);
					MessageBox.error(errorMessages.error(), t.getMessage());
				}
			}
			public void onSuccess(Tileset tileset) {
				init(tileset);
			}
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void startOnTheFlyTiler(final String url) {
		loadMask.hide();
		loadMask = new LoadingPopup("Generating Tiles...");
		loadMask.show();
		
		final TilesetServiceAsync tileService = (TilesetServiceAsync) GWT
			.create(TilesetService.class);

		tileService.startOnTheFlyTiler(url, new AsyncCallback() {

			public void onFailure(Throwable t) {					
				loadMask.hide();
				I18NErrorMessages errorMessages = (I18NErrorMessages) GWT.create(I18NErrorMessages.class);
				MessageBox.error(errorMessages.error(), t.getMessage());
			}
			
			public void onSuccess(Object result) {
				Timer timer = new Timer() {
					public void run() {
						pollOnTheFlyTiler(url, this);									
					}						
				};
				timer.schedule(1000);				
			}
		});
	}
	
	private void pollOnTheFlyTiler(final String url, final Timer timer) {
		final TilesetServiceAsync tileService = (TilesetServiceAsync) GWT
			.create(TilesetService.class);

		tileService.pollOnTheFlyTiler(url, new AsyncCallback<Tileset>() {
			public void onFailure(Throwable caught) {					
				try { 
					throw caught;				
				} catch (Throwable t) {
					loadMask.hide();
					I18NErrorMessages errorMessages = (I18NErrorMessages) GWT.create(I18NErrorMessages.class);
					MessageBox.error(errorMessages.error(), t.getMessage());
				}
			}
			public void onSuccess(Tileset tileset) {
				if (tileset != null) {
					init(tileset);					
				} else {
					timer.schedule(2500);
				}
			}
		});		
	}

}
