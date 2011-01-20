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

import java.util.Collection;

import org.gwt.mosaic.core.client.Dimension;
import org.gwt.mosaic.ui.client.MessageBox;
import org.gwt.mosaic.ui.client.layout.BorderLayout;
import org.gwt.mosaic.ui.client.layout.BorderLayoutData;
import org.gwt.mosaic.ui.client.layout.HasLayoutManager;
import org.gwt.mosaic.ui.client.layout.LayoutPanel;

import at.ait.dme.yuma.suite.apps.core.client.I18NErrorMessages;
import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.Annotation;
import at.ait.dme.yuma.suite.apps.core.client.gui.MediaViewer;
import at.ait.dme.yuma.suite.apps.core.client.gui.events.AnnotateClickHandler;
import at.ait.dme.yuma.suite.apps.core.client.gui.events.selection.AnnotationSelectionEvent;
import at.ait.dme.yuma.suite.apps.core.client.gui.events.selection.AnnotationSelectionHandler;
import at.ait.dme.yuma.suite.apps.core.client.gui.events.selection.HasAnnotationSelectionHandlers;
import at.ait.dme.yuma.suite.apps.core.client.server.annotation.AnnotationService;
import at.ait.dme.yuma.suite.apps.core.client.server.annotation.AnnotationServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The user interface component that shows the annotation tree.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class AnnotationPanel extends Composite
	implements HasLayoutManager, HasAnnotationSelectionHandlers {
	
	/**
	 * The handler manager 
	 */
	final protected HandlerManager handlerManager = new HandlerManager(this);	
	
	/**
	 * The parent container panel
	 */
	private LayoutPanel containerPanel = new LayoutPanel(new BorderLayout());
	
	/**
	 * The animated loading image
	 */
	private Image loadingImage = new Image("images/ajax-loader-small.gif");

	/**
	 * The scroll panel which containes the annotation tree
	 */
	private ScrollPanel scrollPanel = new ScrollPanel();	
	private int scrollPosition = 0;

	/**
	 * Panel for the annotation edit form
	 * TODO is this really needed?
	 */
	protected LayoutPanel editFormPanel = new LayoutPanel();
	
	/**
	 * The annotation edit form
	 */
	private AnnotationEditForm editForm = null;
	
	/**
	 * The annotation tree
	 */
	private AnnotationTree annotationTree;
	
	/**
	 * 'Annotate' buttons
	 */
	private PushButton annotateButton = new PushButton();
	private PushButton annotateFragmentButton = new PushButton();
	
	/**
	 * Reference to the media viewer
	 */
	private MediaViewer mediaViewer = null;

	public AnnotationPanel(MediaViewer mediaViewer, AnnotationEditForm editForm) {	
		this.mediaViewer = mediaViewer;
		this.editForm = editForm;
		
		containerPanel.setStyleName("imageAnnotation-composite");
		containerPanel.add(createHeader(), new BorderLayoutData(BorderLayout.Region.NORTH));

		annotationTree = new AnnotationTree(this, handlerManager);
		loadAnnotations();		
		
		/*
		mediaViewer.addAnnotationSelectionHandler(new AnnotationSelectionHandler() {
			@Override
			public void onAnnotationSelection(AnnotationSelectionEvent event) {
				final NewAnnotationTreeNode node = 
					annotationTree.getAnnotationNode(event.getAnnotation());
				if(node==null) return;
				
				annotationTree.selectAnnotationTreeNode(node, event.isSelected());
				if(event.isSelected()) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							scrollPanel.ensureVisible(node.getAnnotationTreeItem());		
						}
					});
				}			
			}
		});
		*/

		loadingImage.setStyleName("imageAnnotation-loading");
		enableLoadingImage();

		containerPanel.add(scrollPanel);
		initWidget(containerPanel);
	}
	
	@Override
	public void layout() {
		invalidate();
		if (editForm != null)
			editForm.layout();
		containerPanel.layout();
	}
	
	/**
	 * show hints and create link to help page
	 */
	protected Widget createHeader() {
		// The parent header panel
		FlowPanel header = new FlowPanel();
		
		// 'Add your Annotation' label
		Label addAnnotationLabel = new Label(YUMACoreProperties.getConstants().addAnnotation());
		addAnnotationLabel.setStyleName("imageAnnotation-add-annotation");
		header.add(addAnnotationLabel);
		
		// 'Loading' animation
		header.add(loadingImage);
		
		// 'Help' link
		HTML help = new HTML("<a target=\"_blank\" href=\"userguide_" + 
				LocaleInfo.getCurrentLocale().getLocaleName()+".html\">" + 
				YUMACoreProperties.getConstants().help() + "</a>" );
		help.setStyleName("imageAnnotation-help");
		header.add(help);		
		
		// Instructions text
		Label addAnnotationHint = new Label(YUMACoreProperties.getConstants().addAnnotationHint()); 
		addAnnotationHint.setStyleName("imageAnnotation-add-annotation-hint");
		header.add(addAnnotationHint);
		
		// Button panel
		HorizontalPanel buttons = new HorizontalPanel();
		
		// 'Annotate' button
		annotateButton.setStyleName("imageAnnotation-button");
		annotateButton.setText(YUMACoreProperties.getConstants().actionCreate());
		annotateButton.addClickHandler(
				new AnnotateClickHandler(this, null, null, false));
		annotateButton.setEnabled(!YUMACoreProperties.getUser().isEmpty());
		buttons.add(annotateButton);
		
		// 'Annotate Fragment' button
		annotateFragmentButton.setStyleName("imageAnnotation-button");
		annotateFragmentButton.setText(YUMACoreProperties.getConstants().actionCreateFragment());
		annotateFragmentButton.addClickHandler(
				new AnnotateClickHandler(this, null, null, true));
		annotateFragmentButton.setEnabled(!YUMACoreProperties.getUser().isEmpty());
		buttons.add(annotateFragmentButton);
		
		/* 'Show on Map' button
		showOnMapButton.setStyleName("imageAnnotation-button");
		showOnMapButton.setText(YumaImageClient.getConstants().actionShowOnMap());
		showOnMapButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				WindowPanel window = MinMaxWindowPanel.createMinMaxWindowPanel(550, 300, 500, 300);
				window.getHeader().setText("Map");
				window.setWidget(new GoogleMapsComposite(annotations));
				window.setResizable(false);
				window.show();
			}			
		});			
		showOnMapButton.setVisible(YumaImageClient.getBbox()!=null||YumaImageClient.isInTileMode());
		buttons.add(showOnMapButton);	*/
		
		header.add(buttons);	
		header.add(editFormPanel);
		return header;
	}
	
	public void editAnnotation(Annotation annotation, Annotation parent, boolean showFragmentEditor) {
		scrollPosition = scrollPanel.getScrollPosition();	
		annotateButton.setEnabled(false);
		editForm = editForm.newInstance(this, annotation, parent);
		
		if (annotation == null && parent == null) {
			// New annotation at root level
			editFormPanel.add(editForm);
			if(showFragmentEditor)
				mediaViewer.editAnnotation(null);	
		} else {
			if (annotation == null) {
				// Reply
				annotationTree.showAnnotationEditForm(parent, editForm);
			} else {
				// Update
				annotationTree.showAnnotationEditForm(annotation, editForm);
			}
				
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					scrollPanel.ensureVisible(editForm);	
				}
			});
			
			if(showFragmentEditor) {
				// if we update an existing annotation we first have to remove the
				/* existing fragment and show an active fragment panel instead.
				if(update && annotationTreeNode.getImageFragment()!=null) {					
					mediaViewer.editAnnotation(annotationTreeNode.getAnnotation());
					mediaViewer.hideAnnotation(annotationTreeNode.getAnnotation());
				} else {
					mediaViewer.editAnnotation(null);
				}*/
			}
		}
		layout();
	}
	
	/**
	 *  hides the annotation form, again either from above the annotation tree or from
	 *  underneath a tree node, also hides the active fragment panel. if in case of
	 *  an update the operation was canceled we have to restore a possible fragment.
	 *  
	 *  @param annotationTreeNode
	 * 	@param canceled true if the user canceled the operation, otherwise false
	 *  @see #showAnnotationForm(AnnotationTreeNode, boolean, boolean)
	 */
	public void stopEditing(Annotation annotation, boolean canceled) {
		if (annotation == null) {
			editFormPanel.clear();
			annotateButton.setEnabled(true);
			annotateFragmentButton.setEnabled(true);
		} else {
			annotationTree.hideAnnotationEditForm(annotation);
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					scrollPanel.setScrollPosition(scrollPosition);
				}
			});
		}
		
		mediaViewer.stopEditing();
		if (canceled && annotation != null && 
				annotation.getFragment() != null) { 
			mediaViewer.showAnnotation(annotation);
		}

		layout();
	}
	
	public void addAnnotation(Annotation annotation) {
		annotationTree.addAnnotation(annotation);
	}
	
	public void appendChild(Annotation parent, Annotation child) {
		annotationTree.appendChild(parent, child);
	}
	
	/**
	 * removes an annotation from the annotation tree
	 * 
	 * @param annotationNode
	 */
	public void removeAnnotation(Annotation annotation) {
		annotationTree.removeAnnotation(annotation);
	}
	
	/**
	 * makes a server call to retrieve all annotations for the image and displays the using a tree
	 */
	private void loadAnnotations() {
		AnnotationServiceAsync imageAnnotationService = (AnnotationServiceAsync) GWT
				.create(AnnotationService.class);

		imageAnnotationService.listAnnotations(YUMACoreProperties.getObjectURI(),
			new AsyncCallback<Collection<Annotation>>() {
				public void onFailure(Throwable caught) {
					I18NErrorMessages errorMessages = (I18NErrorMessages) GWT.create(I18NErrorMessages.class);
					MessageBox.error(errorMessages.error(), errorMessages.failedToReadAnnotations());
				}

				public void onSuccess(Collection<Annotation> foundAnnotations) {
					annotationTree.removeItems();
					for (Annotation a : foundAnnotations) {
						annotationTree.addAnnotation(a);
					}
					
					scrollPanel.add(annotationTree);				
					disableLoadingImage();
					layout();
				}
			});	
	}
	
	public void reload() {
		enableLoadingImage();
		annotationTree.removeItems();
		loadAnnotations();
	}

	public void refresh() {
		annotationTree.refresh();
	}
	
	/**
	 * show the loading image
	 */
	public void enableLoadingImage() {
		loadingImage.setVisible(true);
	}
	
	/**
	 * hide the loading image
	 */
	public void disableLoadingImage() {
		loadingImage.setVisible(false);
	}
	
	/**
	 * used by the click listeners to retrieve the image composite
	 * 
	 * @return image composite
	 */
	public MediaViewer getMediaViewer() {
		return mediaViewer;
	}

	/**
	 * set the size of this composite
	 */
	public void setSize(int width, int height) {
		if(height<15) return;
		if (YUMACoreProperties.getUserAgent().contains("firefox")) height = height - 2;
		this.setSize(new Integer(width).toString(), new Integer(height).toString());
		scrollPanel.setSize(new Integer(width).toString(), new Integer(
				Math.max(0, height-150)).toString());		
	}

	@Override
	public HandlerRegistration addAnnotationSelectionHandler(
			AnnotationSelectionHandler handler) {
		return handlerManager.addHandler(AnnotationSelectionEvent.getType(), handler);
	}

	@Override
	public Dimension getPreferredSize() {
		return null;
	}

	@Override
	public void invalidate() {
		containerPanel.invalidate();
	}

	@Override
	public void invalidate(Widget widget) {
		containerPanel.invalidate(widget);
	}

	@Override
	public boolean needsLayout() {
		return false;
	}

	@Override
	public void onResize() {
		layout();		
	}
	
}
