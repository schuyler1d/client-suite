package at.ait.dme.yuma.suite.apps.core.client.gui.treeview;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ait.dme.yuma.suite.apps.core.client.datamodel.Annotation;
import at.ait.dme.yuma.suite.apps.core.client.gui.events.selection.AnnotationSelectionEvent;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class NewAnnotationTree extends Tree {
	
	private NewAnnotationPanel panel;
	
    private HandlerManager handlerManager;	
    
    private Map<Annotation, NewAnnotationTreeNode> nodes = new HashMap<Annotation, NewAnnotationTreeNode>();
    
    private Map<TreeItem, Annotation> annotations = new HashMap<TreeItem, Annotation>();
    
    public NewAnnotationTree(NewAnnotationPanel panel, HandlerManager handlerManager) {
    	this.panel = panel;
    	this.handlerManager = handlerManager;
    	this.setStyleName("imageAnnotation-tree");
    }
    
    /**
     * Workaround for http://code.google.com/p/google-web-toolkit/issues/detail?id=369
     */
	public void onBrowserEvent(Event event) {
    	if (DOM.eventGetType(event) == Event.ONMOUSEDOWN
        		|| DOM.eventGetType(event) == Event.ONMOUSEUP
        		|| DOM.eventGetType(event) == Event.ONCLICK
        		|| DOM.eventGetType(event) == Event.ONKEYDOWN
        		|| DOM.eventGetType(event) == Event.ONKEYUP
        		|| DOM.eventGetType(event) == Event.ONKEYPRESS)
        		
        	return;

         super.onBrowserEvent(event);
	}
	
	public void appendChild(Annotation parent, Annotation child) {
		NewAnnotationTreeNode parentNode = getParentNode(parent);
		NewAnnotationTreeNode childNode = new NewAnnotationTreeNode(panel, child);
		addAnnotation(childNode, parentNode);
	}
	
	public void addAnnotation(Annotation annotation) {
		NewAnnotationTreeNode annotationNode = new NewAnnotationTreeNode(panel, annotation);	
		NewAnnotationTreeNode parentNode = getParentNode(annotation);
		addAnnotation(annotationNode, parentNode);
	}
	
	public void addAnnotation(final NewAnnotationTreeNode annotation, NewAnnotationTreeNode parent) {
		TreeItem treeItem;
		if (parent == null) {
			treeItem = this.insertItem(0, annotation);
		} else {
			treeItem = parent.getTreeItem().addItem(annotation);
		}
		annotation.setTreeItem(treeItem);
	
		annotation.addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				annotation.select();
				handlerManager.fireEvent(new AnnotationSelectionEvent(annotation.getAnnotation(), true));
			}
		});
		annotation.addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				annotation.deselect();
				handlerManager.fireEvent(new AnnotationSelectionEvent(annotation.getAnnotation(), false));
			}
		});
				
		setSelectedItem(annotation.getTreeItem());
		ensureSelectedItemVisible();
		
		if(annotation.getAnnotation().hasReplies()) {
			List<Annotation> replies = sort(annotation.getAnnotation().getReplies());
			
			for(Annotation reply : replies) {
				NewAnnotationTreeNode node = new NewAnnotationTreeNode(panel, reply);	
				addAnnotation(node, annotation);				
			}			
		}
		
		nodes.put(annotation.getAnnotation(), annotation);
		annotations.put(treeItem, annotation.getAnnotation());
	}
	
	public void removeAnnotation(Annotation annotation) {
		this.remove(nodes.get(annotation));
	}
	
	public void showAnnotationEditForm(Annotation annotation, NewAnnotationEditForm editForm) {		
		nodes.get(annotation).showAnnotationForm(editForm);
	}
	
	public void hideAnnotationEditForm(Annotation annotation) {
		nodes.get(annotation).hideAnnotationForm();
	}
	
	private List<Annotation> sort(List<Annotation> annotations) {
		Collections.sort(annotations, new Comparator<Annotation>() {
			public int compare(Annotation o1, Annotation o2) {
				return o1.getCreated().compareTo(o2.getCreated());
			}					
		});
		return annotations;
	}
	
	public void selectAnnotation(Annotation annotation, boolean selected) {
		final NewAnnotationTreeNode node = nodes.get(annotation);
		if(selected) {
			node.select();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					setSelectedItem(node.getTreeItem());
					ensureSelectedItemVisible();
				}
			});
		} else {
			node.deselect();
		}
	}
	
	private NewAnnotationTreeNode getParentNode(Annotation annotation) {
		if (annotation == null)
			return null;
		
		return nodes.get(annotation);
	}
	
	public Annotation getParentAnnotation(Annotation annotation) {		
		NewAnnotationTreeNode node = getParentNode(annotation);
		if (node == null)
			return null;
		
		TreeItem parentItem = node.getTreeItem().getParentItem();
		if (parentItem == null)
			return null;
		
		return annotations.get(parentItem);
	}
	
	@Override
	public void removeItems() {
		nodes.clear();
		annotations.clear();
		super.removeItems();
	}

}
