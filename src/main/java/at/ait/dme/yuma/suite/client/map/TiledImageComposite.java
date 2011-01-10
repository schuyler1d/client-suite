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

package at.ait.dme.yuma.suite.client.map;

import org.gwt.mosaic.ui.client.MessageBox;

import at.ait.dme.yuma.suite.client.ErrorMessages;
import at.ait.dme.yuma.suite.client.image.ImageComposite;
import at.ait.dme.yuma.suite.client.image.ImageRect;
import at.ait.dme.yuma.suite.client.image.annotation.ImageAnnotation;
import at.ait.dme.yuma.suite.client.image.annotation.ImageFragment;
import at.ait.dme.yuma.suite.client.image.shape.GeoPoint;
import at.ait.dme.yuma.suite.client.image.shape.Shape;
import at.ait.dme.yuma.suite.client.map.annotation.AnnotationLayer;
import at.ait.dme.yuma.suite.client.map.annotation.ControlPointLayer;
import at.ait.dme.yuma.suite.client.map.explore.KMLLayer;
import at.ait.dme.yuma.suite.client.map.explore.SearchLayer;
import at.ait.dme.yuma.suite.client.server.TilesetService;
import at.ait.dme.yuma.suite.client.server.TilesetServiceAsync;
import at.ait.dme.yuma.suite.client.server.exception.TilesetNotAvailableException;
import at.ait.dme.yuma.suite.client.tagcloud.TagCloud;
import at.ait.dme.yuma.suite.client.tagcloud.annotation.TagEnabledAnnotationForm;
import at.ait.dme.yuma.suite.client.util.LoadMask;

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
public class TiledImageComposite extends ImageComposite {
		
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
	private LoadMask loadMask;

	public TiledImageComposite(String mapUrl) {
		panel = new AbsolutePanel();
		panel.setSize("100%", "100%");
		
		loadMask = new LoadMask("Loading Map...");
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
	
	@Override
	public TagCloud getTagCloud() {
		return lAnnotation.getTagCloud();
	}
	
	@Override
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
	public void showActiveFragmentPanel(ImageAnnotation annotation, boolean forceVisible) {
		if (lControlPoints.isVisible()) {
			lControlPoints.showActiveFragmentPanel(annotation, true);
		} else {
			lAnnotation.showActiveFragmentPanel(annotation, true);
		}
	}
	
	@Override
	public void hideActiveFragmentPanel() {
		if (lControlPoints.isVisible()) {
			lControlPoints.hideActiveFragmentPanel();
		} else {
			lAnnotation.hideActiveFragmentPanel();
		}
	}

	@Override
	public void selectFragment(ImageAnnotation annotation, boolean selected) {
		if (annotation.getFragment() != null && ((ImageFragment) annotation.getFragment()).getShape() instanceof GeoPoint) {
			lControlPoints.selectFragment(annotation, selected);
		} else {
			lAnnotation.selectFragment(annotation, selected);
		}
	}
	
	@Override
	public void showFragment(ImageAnnotation annotation) {
		if (((ImageFragment)annotation.getFragment()).getShape() instanceof GeoPoint) {
			lControlPoints.showFragment(annotation);
		} else {
			lAnnotation.showFragment(annotation);
		}
	}
	
	@Override
	public void hideFragment(ImageAnnotation annotation) {
		if (((ImageFragment)annotation.getFragment()).getShape() instanceof GeoPoint) {
			lControlPoints.hideFragment(annotation);
		} else {
			lAnnotation.hideFragment(annotation);
		}
	}

	@Override
	public Shape getActiveShape() {
		if (lControlPoints.isVisible()) {
			return lControlPoints.getActiveShape();
		} else {
			return lAnnotation.getActiveShape();
		}
	}
	
	@Override
	public ImageRect getVisibleRect() {
		return getImageRect();
	}

	@Override
	public ImageRect getImageRect() {
		return new ImageRect(0, 0, xExtent, yExtent);
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
						ErrorMessages errorMessages = (ErrorMessages) GWT.create(ErrorMessages.class);
						MessageBox.error(errorMessages.error(), t.getMessage());
					} else {
						startOnTheFlyTiler(url);
					}
				} catch (Throwable other) {
					loadMask.hide();
					ErrorMessages errorMessages = (ErrorMessages) GWT.create(ErrorMessages.class);
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
		loadMask = new LoadMask("Generating Tiles...");
		loadMask.show();
		
		final TilesetServiceAsync tileService = (TilesetServiceAsync) GWT
			.create(TilesetService.class);

		tileService.startOnTheFlyTiler(url, new AsyncCallback() {

			public void onFailure(Throwable t) {					
				loadMask.hide();
				ErrorMessages errorMessages = (ErrorMessages) GWT.create(ErrorMessages.class);
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
					ErrorMessages errorMessages = (ErrorMessages) GWT.create(ErrorMessages.class);
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
