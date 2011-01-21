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

package at.ait.dme.yuma.suite.apps.core.client.treeview;

import at.ait.dme.yuma.suite.apps.core.client.User;
import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.Annotation;
import at.ait.dme.yuma.suite.apps.core.client.datamodel.SemanticTag;
import at.ait.dme.yuma.suite.apps.core.client.events.AnnotateClickHandler;
import at.ait.dme.yuma.suite.apps.core.client.events.DeleteClickHandler;

import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AnnotationTreeNode extends Composite 
	implements HasMouseOutHandlers, HasMouseOverHandlers {
	
	/**
	 * Reference to the annotation panel
	 */
	protected AnnotationPanel panel;
	
	/**
	 * The annotation
	 */
	protected Annotation annotation;
	
	/**
	 * The parent annotation tree node (if any)
	 */
	protected AnnotationTreeNode parent;
	
	/**
	 * The parent node of this annotation tree node
	 */
	protected TreeItem treeItem;
	
	/**
	 * The container panel
	 */
	private VerticalPanel container = new VerticalPanel();
	
	/**
	 * Buttons
	 */
	private PushButton btnReply;
	private PushButton btnReplyFragment;
	private PushButton btnEdit;
	private PushButton btnDelete;
	
	/**
	 * This node's edit form
	 */
	private AnnotationEditForm editForm;

	public AnnotationTreeNode(AnnotationPanel panel, Annotation annotation, AnnotationTreeNode parent) {
		this.panel = panel;
		this.annotation = annotation;
		this.parent = parent;
		container.add(createHeader());
		
		Label title = new Label(annotation.getTitle());
		title.setStyleName("imageAnnotation-title");
		container.add(title);

		InlineHTML text = new InlineHTML(annotation.getText());
		text.setStyleName("imageAnnotation-text");
		container.add(text);
		
		if (annotation.hasTags()) {
			FlowPanel tagPanel = new FlowPanel();
			tagPanel.setStyleName("imageAnnotation-taglist");
			for (SemanticTag t : annotation.getTags()) {
				InlineHTML span = new InlineHTML("<a target=\"_blank\" href=\""
						+ t.getURI() + "\" title=\"" 
						+ t.getDescription() + "\">" 
						+ t.getLabel() + "</a>"
				);
				tagPanel.add(span);
			}
			container.add(tagPanel);
		}

		container.add(createActions());
		initWidget(container);
		deselect();
	}
	
	protected Panel createHeader() {
		HorizontalPanel headerPanel = new HorizontalPanel();
		headerPanel.setStyleName("imageAnnotation-header");

		Image avatar = new Image(User.ANONYMOUS.getGravatarURL());
		avatar.setStyleName("imageAnnotation-header-avatar");
		headerPanel.add(avatar);
		
		Label userLabel = new Label(annotation.getCreatedBy());
		userLabel.setStyleName("imageAnnotation-header-user");
		headerPanel.add(userLabel);

		Label dateLabel = new Label("(" +
					DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(annotation.getLastModified()) +
					")");
		dateLabel.setStyleName("imageAnnotation-header-date");
		
		headerPanel.add(dateLabel);
		return headerPanel;
	}
	
	protected Panel createActions() {
		HorizontalPanel actionsPanel = new HorizontalPanel();
		
		btnReply = new PushButton(YUMACoreProperties.getConstants().actionReply());
		btnReply.setStyleName("imageAnnotation-action");
		btnReply.addClickHandler(new AnnotateClickHandler(panel, null, this, false));
		btnReply.setEnabled(!User.get().isAnonymous());
		actionsPanel.add(btnReply);

		btnReplyFragment = new PushButton(YUMACoreProperties.getConstants().actionReplyFragment());
		btnReplyFragment.setStyleName("imageAnnotation-action");
		btnReplyFragment.addClickHandler(new AnnotateClickHandler(panel, null, this, true));
		btnReplyFragment.setEnabled(!User.get().isAnonymous());
		actionsPanel.add(btnReplyFragment);

		btnEdit = new PushButton(YUMACoreProperties.getConstants().actionEdit());
		btnEdit.setStyleName("imageAnnotation-action");
		btnEdit.setEnabled(User.get().getUsername().equals(annotation.getCreatedBy())
				&& !annotation.hasReplies());
		btnEdit.addClickHandler(new AnnotateClickHandler(panel, this, parent, annotation.hasFragment()));
		actionsPanel.add(btnEdit);

		btnDelete = new PushButton(YUMACoreProperties.getConstants().actionDelete());
		btnDelete.setStyleName("imageAnnotation-action");
		btnDelete.setEnabled(User.get().getUsername().equals(annotation.getCreatedBy())
				&& !annotation.hasReplies());
		btnDelete.addClickHandler(
				new DeleteClickHandler(panel, this, parent));
		actionsPanel.add(btnDelete);

		actionsPanel.setStyleName("imageAnnotation-actions");		
		return actionsPanel;
	}
	
	public void showAnnotationForm(AnnotationEditForm editForm) {
		this.editForm = editForm;
		container.add(editForm);
		btnReply.setEnabled(false);
		btnReplyFragment.setEnabled(false);
		btnEdit.setEnabled(false);
	}

	public void hideAnnotationForm() {
		this.editForm.setVisible(false);
		btnReply.setEnabled(!User.get().isAnonymous());
		btnReplyFragment.setEnabled(!User.get().isAnonymous());
		btnEdit.setEnabled(true);
	}
	
	public void refresh() {
		deselect();
		btnReply.setEnabled(!User.get().isAnonymous());
		btnReplyFragment.setEnabled(!User.get().isAnonymous());
		btnEdit.setEnabled(User.get().getUsername().equals(annotation.getCreatedBy())
			&& !annotation.hasReplies());
		btnDelete.setEnabled(User.get().getUsername().equals(annotation.getCreatedBy())
				&& !annotation.hasReplies());
	}
	
	public Annotation getAnnotation() {
		return annotation;
	}
	
	TreeItem getTreeItem() {
		return treeItem;
	}
	
	void setTreeItem(TreeItem treeItem) {
		this.treeItem = treeItem;
	}
	
	public void select() {
		setStyleName("imageAnnotation-selected");
	}

	public void deselect() {
		setStyleName("imageAnnotation");
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AnnotationTreeNode))
			return false;
		
		if (this == other)
			return true;

		AnnotationTreeNode node = (AnnotationTreeNode) other;
		return annotation.equals(node.getAnnotation());
	}
	
	public void clear() {
		container.clear();
	}

	@Override
	public int hashCode() {
		return annotation.hashCode();
	}
	
	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

}