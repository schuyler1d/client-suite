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

package at.ait.dme.yuma.suite.apps.image.core.client.treeview;

import at.ait.dme.yuma.suite.apps.core.client.YUMACoreProperties;
import at.ait.dme.yuma.suite.apps.core.client.events.AnnotateClickHandler;
import at.ait.dme.yuma.suite.apps.core.client.events.DeleteClickHandler;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationEditForm;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationPanel;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationTreeNode;
import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation;
import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;
import at.ait.dme.yuma.suite.apps.core.shared.model.User;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The annotation tree node, i.e. the panel element that represents
 * one annotation in the annotation tree.
 *  
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class ImageAnnotationTreeNode extends AnnotationTreeNode {
	
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
	
	public ImageAnnotationTreeNode() { }

	public ImageAnnotationTreeNode(AnnotationPanel panel, 
			Annotation annotation, AnnotationTreeNode parent) {
		
		super(panel, annotation, parent);
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
						+ t.getPrimaryDescription() + "\">" 
						+ t.getPrimaryLabel() + "</a>"
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

		Image avatar = new Image(annotation.getCreatedBy().getGravatarURL());
		avatar.setStyleName("imageAnnotation-header-avatar");
		headerPanel.add(avatar);
		
		Label userLabel = new Label(annotation.getCreatedBy().getUsername());
		userLabel.setStyleName("imageAnnotation-header-user");
		headerPanel.add(userLabel);

		Label dateLabel = new Label("(" +
					DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(annotation.getLastModified()) +
					")");
		dateLabel.setStyleName("imageAnnotation-header-date");
		
		headerPanel.add(dateLabel);
		return headerPanel;
	}
	
	private Panel createActions() {
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
		btnEdit.setEnabled(User.get().equals(annotation.getCreatedBy())
				&& !annotation.hasReplies());
		btnEdit.addClickHandler(new AnnotateClickHandler(panel, this, parent, annotation.hasFragment()));
		actionsPanel.add(btnEdit);

		btnDelete = new PushButton(YUMACoreProperties.getConstants().actionDelete());
		btnDelete.setStyleName("imageAnnotation-action");
		btnDelete.setEnabled(User.get().equals(annotation.getCreatedBy())
				&& !annotation.hasReplies());
		btnDelete.addClickHandler(
				new DeleteClickHandler(panel, this, parent));
		actionsPanel.add(btnDelete);

		actionsPanel.setStyleName("imageAnnotation-actions");		
		return actionsPanel;
	}
	
	@Override
	public AnnotationTreeNode newInstance(AnnotationPanel panel, 
			Annotation annotation, AnnotationTreeNode parent) {

		return new ImageAnnotationTreeNode(panel, annotation, parent);
	}
	
	@Override
	public void showAnnotationForm(AnnotationEditForm editForm) {
		this.editForm = editForm;
		addStyleName("imageAnnotation-edit");
		container.add(editForm);
		btnReply.setEnabled(false);
		btnReplyFragment.setEnabled(false);
		btnEdit.setEnabled(false);
	}

	@Override
	public void hideAnnotationForm() {
		this.editForm.setVisible(false);
		removeStyleName("imageAnnotation-edit");
		btnReply.setEnabled(!User.get().isAnonymous());
		btnReplyFragment.setEnabled(!User.get().isAnonymous());
		btnEdit.setEnabled(true);
	}
	
	@Override
	public void refresh() {
		deselect();
		btnReply.setEnabled(!User.get().isAnonymous());
		btnReplyFragment.setEnabled(!User.get().isAnonymous());
		btnEdit.setEnabled(User.get().equals(annotation.getCreatedBy())
			&& !annotation.hasReplies());
		btnDelete.setEnabled(User.get().equals(annotation.getCreatedBy())
				&& !annotation.hasReplies());
	}
	
	@Override	
	public void clear() {
		container.clear();
	}

}
