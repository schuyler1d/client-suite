package at.ait.dme.yuma.suite.apps.map.client.treeview;

import org.gwt.mosaic.ui.client.WindowPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;

import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationPanel;
import at.ait.dme.yuma.suite.apps.core.client.treeview.AnnotationTreeNode;
import at.ait.dme.yuma.suite.apps.core.client.widgets.MinMaxWindowPanel;
import at.ait.dme.yuma.suite.apps.core.shared.model.Annotation;
import at.ait.dme.yuma.suite.apps.image.core.client.treeview.ImageAnnotationTreeNode;
import at.ait.dme.yuma.suite.apps.image.core.shared.model.ImageAnnotation;
import at.ait.dme.yuma.suite.apps.map.client.widgets.GoogleMapsComposite;

public class MapAnnotationTreeNode extends ImageAnnotationTreeNode {
	
	public MapAnnotationTreeNode() { }

	public MapAnnotationTreeNode(AnnotationPanel panel, 
			Annotation annotation, AnnotationTreeNode parent) {

		super(panel, annotation, parent);
	}
	
	@Override
	protected Panel createHeader() {
		Panel headerPanel = super.createHeader();
		
		if (annotation.hasFragment()) {
			Image showOnMapIcon = new Image("images/earth.gif");
			showOnMapIcon.setStyleName("imageAnnotation-header-map");
			showOnMapIcon.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					WindowPanel window = MinMaxWindowPanel.createMinMaxWindowPanel(550, 300, 500, 300);
					window.setWidget(new GoogleMapsComposite((ImageAnnotation) annotation));
					window.show();
				}
			});
			headerPanel.add(showOnMapIcon);
		}
		
		return headerPanel;
	}
	
	@Override
	public AnnotationTreeNode newInstance(AnnotationPanel panel, 
			Annotation annotation, AnnotationTreeNode parent) {

		return new MapAnnotationTreeNode(panel, annotation, parent);
	}
	
}
