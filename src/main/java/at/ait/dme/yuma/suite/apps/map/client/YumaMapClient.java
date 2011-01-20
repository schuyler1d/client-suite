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
package at.ait.dme.yuma.suite.apps.map.client;

import org.gwt.mosaic.ui.client.DecoratedTabLayoutPanel;
import org.gwt.mosaic.ui.client.TabLayoutPanel;
import org.gwt.mosaic.ui.client.WindowPanel;

import at.ait.dme.yuma.suite.apps.core.client.I18NConstants;
import at.ait.dme.yuma.suite.apps.core.client.I18NErrorMessages;
import at.ait.dme.yuma.suite.apps.core.client.User;
import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.core.client.gui.MediaViewer;
import at.ait.dme.yuma.suite.apps.core.client.gui.events.selection.AnnotationSelectionEvent;
import at.ait.dme.yuma.suite.apps.core.client.gui.events.selection.AnnotationSelectionHandler;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.NewAnnotationPanel;
import at.ait.dme.yuma.suite.apps.core.client.server.auth.AuthenticationService;
import at.ait.dme.yuma.suite.apps.core.client.server.auth.AuthenticationServiceAsync;
import at.ait.dme.yuma.suite.apps.image.core.client.gui.MinMaxWindowPanel;
import at.ait.dme.yuma.suite.apps.image.core.client.shape.ShapeTypeRegistry;
import at.ait.dme.yuma.suite.apps.image.core.client.tagcloud.annotation.TagEnabledAnnotationForm;
import at.ait.dme.yuma.suite.apps.map.client.TileBasedImageViewer;
import at.ait.dme.yuma.suite.apps.map.client.annotation.ControlPointComposite;
import at.ait.dme.yuma.suite.apps.map.client.annotation.ControlPointForm;
import at.ait.dme.yuma.suite.apps.map.client.explore.ExplorationComposite;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This is the entry point to the application.
 *  
 * TODO "goto fragment" function in case the fragment lies outside the
 * visible rect due to dragging. 
 * 
 * TODO support rating
 * 
 * TODO paging for the search results table and annotation tree
 *  
 * @author Christian Sadilek, Rainer Simon
 */
public class YumaMapClient implements EntryPoint {
	private static final String LEMO_COOKIE_NAME = "lemo_user";
	private static User authenticatedUser = null;
	private static I18NConstants annotationConstants = null;
	
	private MediaViewer imageComposite = null;
	
	public YumaMapClient() {}

	/**
	 * only used by unit tests to create an image composite w/o an annotation composite
	 * 
	 * @param imageUrl
	 */
	public YumaMapClient(String imageUrl) {
		showImage(imageUrl);
	}
	
	/**
	 * load the module and initialize the application
	 */
	public void onModuleLoad() {	
		initApplication(YUMACoreProperties.getObjectURI());
	}
		
	private void initApplication(String imageUrl) {
		showImage(imageUrl); 
		
		// the image has to be completely loaded before we can show the annotations
		// otherwise possible fragments can not be displayed properly
		imageComposite.addLoadHandler(new LoadHandler() {
			public void onLoad(LoadEvent events) {
				// first we authenticate the user by either using the provided
				// user name or the secure authentication token.	
				String userName = YUMACoreProperties.getUser();
				if(userName!=null&&!userName.trim().isEmpty()) {
					// TODO deactivate this if you ever go into production					
					// setAuthenticatedUser(new User(userName));
					showAnnotations();
					return;
				}
				
				String authToken = ""; //getRequestParameterValue("authToken");
				String appSign = ""; //getRequestParameterValue("appSign");
				
				AuthenticationServiceAsync authService = (AuthenticationServiceAsync) GWT
						.create(AuthenticationService.class);
				authService.authenticate(authToken, appSign, new AsyncCallback<User>() {
					public void onFailure(Throwable caught) {
						I18NErrorMessages errorMessages=(I18NErrorMessages)GWT.create(I18NErrorMessages.class);
						Window.alert(errorMessages.failedToAuthenticate());
						// create non-privileged user to use read-only mode.
						// setAuthenticatedUser(new User());
						showAnnotations();
					}
					public void onSuccess(User user) {
						// setAuthenticatedUser(user);						 
						showAnnotations();
					}
				});					
			}			
		});
	}

	/**
	 * show the image composite
	 * 
	 * @param imageUrl
	 * @throws TilesetGenerationException 
	 * @throws TilesetNotFoundException 
	 */
	private void showImage(String imageUrl) {
		imageComposite = new TileBasedImageViewer(imageUrl);
		RootPanel.get().add(imageComposite, 0, 0);
	}	

	/**
	 * show the annotation composite
	 */
	private void showAnnotations() {
		// Create a floating window
		final WindowPanel window = MinMaxWindowPanel.createMinMaxWindowPanel(500, 50, 430, 600);
		window.show();
		window.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				// we do not allow to close this window
				window.show();
			}
		});
		
		// Parent tab panel 
		TabLayoutPanel tabPanel = new DecoratedTabLayoutPanel();
		tabPanel.setPadding(0);
		showAnnotationsTab(tabPanel);

		showGeoReferencingTab(tabPanel);
		showExplorationTab(tabPanel);
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
				
			TileBasedImageViewer tic = (TileBasedImageViewer) imageComposite;

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if (event.getSelectedItem().intValue() == 0) {
					// Annotations tab
					tic.showAnnotationLayer();
				} else if (event.getSelectedItem().intValue() == 1) {
					// Georeferencing tab
					tic.showControlPointLayer();
				} else if (event.getSelectedItem().intValue() == 2) {
					// Exploration tab
				}
			}

		});

		window.setWidget(tabPanel);
	}
	
	/**
	 * show annotations tab
	 * 
	 * @param tabPanel
	 */
	private void showAnnotationsTab(TabLayoutPanel tabPanel) {
		NewAnnotationPanel annComposite;

		annComposite = new NewAnnotationPanel(imageComposite, 
				new TagEnabledAnnotationForm(((TileBasedImageViewer)imageComposite).getTagCloud()));
				// ShapeTypeRegistry.allTypes());			

		annComposite.addAnnotationSelectionHandler(new AnnotationSelectionHandler() {
			@Override
			public void onAnnotationSelection(AnnotationSelectionEvent event) {
				imageComposite.selectAnnotation(event.getAnnotation(), event.isSelected());
			}
		});
		tabPanel.add(annComposite, YUMACoreProperties.getConstants().tabAnnotations());
	}
	
	/**
	 * show georeferencing tab
	 * 
	 * @param tabPanel
	 */
	private void showGeoReferencingTab(TabLayoutPanel tabPanel) {
		NewAnnotationPanel geoRefComposite = new ControlPointComposite(
				(TileBasedImageViewer)imageComposite, 
				new ControlPointForm(((TileBasedImageViewer)imageComposite).getControlPointLayer()), 
				ShapeTypeRegistry.geoTypes());
		
		geoRefComposite.addAnnotationSelectionHandler(new AnnotationSelectionHandler() {
			@Override
			public void onAnnotationSelection(AnnotationSelectionEvent event) {
				imageComposite.selectAnnotation(event.getAnnotation(), event.isSelected());
			}
		});
				
		tabPanel.add(geoRefComposite, YUMACoreProperties.getConstants().tabGeoReferencing());
	}
	
	/**
	 * show exploration tab
	 * 
	 * @param tabPanel
	 */
	private void showExplorationTab(TabLayoutPanel tabPanel) {
		ExplorationComposite expComposite = new ExplorationComposite((TileBasedImageViewer)imageComposite);
		tabPanel.add(expComposite, YUMACoreProperties.getConstants().tabExploration());
	}
		
	/**
	 * returns the image composite
	 * 
	 * @return image composite
	 */
	public MediaViewer getImageComposite() {
		return imageComposite;
	}

	/**
	 * reload the application
	 */
	public static native void reload() /*-{
     	$wnd.location.reload();
  	}-*/;

}
