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

package at.ait.dme.yuma.suite.apps.image.client;

import org.gwt.mosaic.ui.client.DecoratedTabLayoutPanel;
import org.gwt.mosaic.ui.client.TabLayoutPanel;
import org.gwt.mosaic.ui.client.WindowPanel;

import at.ait.dme.yuma.suite.apps.core.client.I18NErrorMessages;
import at.ait.dme.yuma.suite.apps.core.client.User;
import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.core.client.gui.MediaViewer;
import at.ait.dme.yuma.suite.apps.core.client.gui.events.selection.AnnotationSelectionEvent;
import at.ait.dme.yuma.suite.apps.core.client.gui.events.selection.AnnotationSelectionHandler;
import at.ait.dme.yuma.suite.apps.core.client.gui.treeview.AnnotationPanel;
import at.ait.dme.yuma.suite.apps.core.client.server.auth.AuthenticationService;
import at.ait.dme.yuma.suite.apps.core.client.server.auth.AuthenticationServiceAsync;
import at.ait.dme.yuma.suite.apps.image.core.client.ImageViewer;
import at.ait.dme.yuma.suite.apps.image.core.client.gui.MinMaxWindowPanel;
import at.ait.dme.yuma.suite.apps.image.core.client.shape.ShapeTypeRegistry;
import at.ait.dme.yuma.suite.apps.image.core.client.tagcloud.annotation.TagEnabledAnnotationForm;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
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
public class YumaImageClient implements EntryPoint {
	
	private static final String LEMO_COOKIE_NAME = "lemo_user";
	
	private static User authenticatedUser = null;
	
	private MediaViewer imageComposite = null;
	
	public YumaImageClient() {}

	/**
	 * only used by unit tests to create an image composite w/o an annotation composite
	 * 
	 * @param imageUrl
	 */
	public YumaImageClient(String imageUrl) {
		showImage(imageUrl);
	}
	
	/**
	 * load the module and initialize the application
	 */
	public void onModuleLoad() {
		String imageUrl = YUMACoreProperties.getObjectURI();
		
		if (imageUrl != null) {
			// Standard Web image annotation
			initApplication(imageUrl);
		}
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
				
				String authToken = ""; // getRequestParameterValue("authToken");
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
	 * @throws TileGenerationStartedException 
	 * @throws TilesetNotAvailableException 
	 */
	private void showImage(String imageUrl) {
		imageComposite = new ImageViewer(imageUrl);
		// RootPanel.get().add(imageComposite, 10, 80);
		RootPanel.get().add(imageComposite, 10, 10);
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
		window.setWidget(tabPanel);
	}
	
	/**
	 * show annotations tab
	 * 
	 * @param tabPanel
	 */
	private void showAnnotationsTab(TabLayoutPanel tabPanel) {
		AnnotationPanel annComposite;

		annComposite = new AnnotationPanel(
				imageComposite, 
				new TagEnabledAnnotationForm(((ImageViewer)imageComposite).getTagCloud()),
				ShapeTypeRegistry.allTypes());			

		annComposite.addAnnotationSelectionHandler(new AnnotationSelectionHandler() {
			@Override
			public void onAnnotationSelection(AnnotationSelectionEvent event) {
				imageComposite.selectAnnotation(event.getAnnotation(), event.isSelected());
			}
		});
		tabPanel.add(annComposite, YUMACoreProperties.getConstants().tabAnnotations());
	}
	
	/**
	 * returns the image composite
	 * 
	 * @return image composite
	 */
	public MediaViewer getImageComposite() {
		return imageComposite;
	}
	
	/*
	 * returns the provided name of the user 
	 * 
	 * @return user name
	 *
	public static String getUser() {
		String userName = authenticatedUser.getName();
		if(!userName.equalsIgnoreCase(Cookies.getCookie(LEMO_COOKIE_NAME))) {
			Cookies.setCookie(LEMO_COOKIE_NAME, userName, null, null, "/", false);
		}
		return userName;
	}
	*/
	
	/**
	 * set the authenticated user and create a cookie
	 * 
	 * @param user
	 *
	public static void setAuthenticatedUser(User user) {
		Cookies.setCookie(LEMO_COOKIE_NAME, user.getName(), null, null, "/", false);				
		authenticatedUser = user;
	}
	*/
	
	/**
	 * check if the given user is the currently authenticated user
	 * 
	 * @param user
	 * @return true if user is authenticated, otherwise false
	 *
	public static boolean isAuthenticatedUser(String user) {
		if(user==null || getUser()==null) return false;			
		return getUser().equalsIgnoreCase(user) || authenticatedUser.isAdmin();
	}*/
	
	/**
	 * authenticate admin user as user provided
	 * 
	 * @param user
	 *
	public static void authenticateAs(String user) {
		if(authenticatedUser.isAdmin()) {
			Cookies.setCookie(LEMO_COOKIE_NAME, user, null, null, "/", false);							
		}
	}
	*/
	
	/**
	 * reload the application
	 */
	public static native void reload() /*-{
     	$wnd.location.reload();
  	}-*/;
	
}
