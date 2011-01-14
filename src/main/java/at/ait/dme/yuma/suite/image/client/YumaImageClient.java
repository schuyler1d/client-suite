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

package at.ait.dme.yuma.suite.image.client;

import org.gwt.mosaic.ui.client.DecoratedTabLayoutPanel;
import org.gwt.mosaic.ui.client.TabLayoutPanel;
import org.gwt.mosaic.ui.client.WindowPanel;

import at.ait.dme.yuma.suite.core.client.I18NConstants;
import at.ait.dme.yuma.suite.core.client.I18NErrorMessages;
import at.ait.dme.yuma.suite.core.client.User;
import at.ait.dme.yuma.suite.core.client.gui.MediaViewer;
import at.ait.dme.yuma.suite.core.client.gui.events.selection.AnnotationSelectionEvent;
import at.ait.dme.yuma.suite.core.client.gui.events.selection.AnnotationSelectionHandler;
import at.ait.dme.yuma.suite.core.client.gui.treeview.TreeViewComposite;
import at.ait.dme.yuma.suite.core.client.server.auth.AuthenticationService;
import at.ait.dme.yuma.suite.core.client.server.auth.AuthenticationServiceAsync;
import at.ait.dme.yuma.suite.image.core.client.ImageViewer;
import at.ait.dme.yuma.suite.image.core.client.gui.MinMaxWindowPanel;
import at.ait.dme.yuma.suite.image.core.client.shape.ShapeTypeRegistry;
import at.ait.dme.yuma.suite.image.core.client.tagcloud.annotation.TagEnabledAnnotationForm;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Cookies;
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
	private static I18NConstants annotationConstants = null;
	
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
		String imageUrl = getRequestParameterValue("objectURL");
		
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
				String userName = getRequestParameterValue("user");
				if(userName!=null&&!userName.trim().isEmpty()) {
					// TODO deactivate this if you ever go into production					
					setAuthenticatedUser(new User(userName));
					showAnnotations();
					return;
				}
				
				String authToken = getRequestParameterValue("authToken");
				String appSign = getRequestParameterValue("appSign");
				
				AuthenticationServiceAsync authService = (AuthenticationServiceAsync) GWT
						.create(AuthenticationService.class);
				authService.authenticate(authToken, appSign, new AsyncCallback<User>() {
					public void onFailure(Throwable caught) {
						I18NErrorMessages errorMessages=(I18NErrorMessages)GWT.create(I18NErrorMessages.class);
						Window.alert(errorMessages.failedToAuthenticate());
						// create non-privileged user to use read-only mode.
						setAuthenticatedUser(new User());
						showAnnotations();
					}
					public void onSuccess(User user) {
						setAuthenticatedUser(user);						 
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
		TreeViewComposite annComposite;

		annComposite = new TreeViewComposite(
				imageComposite, 
				new TagEnabledAnnotationForm(((ImageViewer)imageComposite).getTagCloud()),
				ShapeTypeRegistry.allTypes());			

		annComposite.addAnnotationSelectionHandler(new AnnotationSelectionHandler() {
			@Override
			public void onAnnotationSelection(AnnotationSelectionEvent event) {
				imageComposite.selectAnnotation(event.getAnnotation(), event.isSelected());
			}
		});
		tabPanel.add(annComposite, getConstants().tabAnnotations());
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
	 * returns a request parameter. the initial request parameters provided to the 
	 * entry point are stored as javascript variables and are therefore accessible 
	 * through the dictionary. see also annotate.jsp.
	 * 
	 * @param parameterName
	 * @return parameter value
	 */
	private static String getRequestParameterValue(String parameterName) {
		String parameterValue;
		try {
			Dictionary parameters = Dictionary.getDictionary("parameters");
			parameterValue = parameters.get(parameterName);
			if (parameterValue.equals("null"))
				parameterValue = null;
		} catch (Exception e) {
			return null;
		}
		return parameterValue;
	}
	
	/**
	 * returns the provided name of the user 
	 * 
	 * @return user name
	 */
	public static String getUser() {
		String userName = authenticatedUser.getName();
		if(!userName.equalsIgnoreCase(Cookies.getCookie(LEMO_COOKIE_NAME))) {
			Cookies.setCookie(LEMO_COOKIE_NAME, userName, null, null, "/", false);
		}
		return userName;
	}
	
	/**
	 * set the authenticated user and create a cookie
	 * 
	 * @param user
	 */
	public static void setAuthenticatedUser(User user) {
		Cookies.setCookie(LEMO_COOKIE_NAME, user.getName(), null, null, "/", false);				
		authenticatedUser = user;
	}
	
	/**
	 * check if the given user is the currently authenticated user
	 * 
	 * @param user
	 * @return true if user is authenticated, otherwise false
	 */
	public static boolean isAuthenticatedUser(String user) {
		if(user==null || getUser()==null) return false;			
		return getUser().equalsIgnoreCase(user) || authenticatedUser.isAdmin();
	}
	
	/**
	 * authenticate admin user as user provided
	 * 
	 * @param user
	 */
	public static void authenticateAs(String user) {
		if(authenticatedUser.isAdmin()) {
			Cookies.setCookie(LEMO_COOKIE_NAME, user, null, null, "/", false);							
		}
	}
	
	/**
	 * return the url to the image
	 * 
	 * @return image url
	 */
	public static String getImageUrl() {
		String objectUrl=getRequestParameterValue("objectURL");
		if(objectUrl==null)
			objectUrl=getRequestParameterValue("imageURL");
		if(objectUrl==null)
			objectUrl=getRequestParameterValue("htmlURL");
		
		return objectUrl;
	}
	
	/**
	 * return the flagged id used in admin mode
	 * 
	 * @return annotation id
	 */
	public static String getFlaggedId() {
		return getRequestParameterValue("flaggedId");
	}
	
	/**
	 * returns the optional provided id of an external object that
	 * the annotated image is linked to.
	 * 
	 * @return id of the external object
	 */
	public static String getExternalObjectId() {
		return getRequestParameterValue("id");
	}
	
	/**
	 * returns the provided name of the database to use
	 * 
	 * @return database name
	 */
	public static String getDatabaseName() {
		return getRequestParameterValue("db");	
	}

	/**
	 * returns the base URL
	 * 
	 * @return base URL
	 */
	public static String getBaseUrl() {
		return getRequestParameterValue("baseURL");	
	}
	
	/**
	 * returns the bounding box 
	 * 
	 * @return base URL
	 */
	public static String getBbox() {
		return getRequestParameterValue("bbox");	
	}
	
	/**
	 * check for tile mode
	 * 
	 * @return true if in tile mode, otherwise false
	 */
	public static boolean isInTileMode() {
		return (getRequestParameterValue("tileView") != null);
		
	}
	/**
	 * returns the internationalized constants of this app
	 * 
	 * @return constants
	 */
	public static I18NConstants getConstants() {
		if(annotationConstants==null)
			annotationConstants=(I18NConstants)GWT.create(I18NConstants.class);
		return annotationConstants;
	}
	
	/**
	 * reload the application
	 */
	public static native void reload() /*-{
     	$wnd.location.reload();
  	}-*/;
	
	/**
	 * ideally you should never need this
	 * 
	 * @return user agent string
	 */
	public static native String getUserAgent() /*-{
		return navigator.userAgent.toLowerCase();
	}-*/;
}
