/* Copyright 2008-2010 Austrian Institute of Technology
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

import java.util.Collection;

import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;
import at.ait.dme.yuma.suite.apps.core.shared.server.tagging.TagService;
import at.ait.dme.yuma.suite.apps.core.shared.server.tagging.TagServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;

/**
 * 'Add tag' input field with auto-suggest functionality based
 * on the TagService.
 *  
 * @author Miki Zehetner
 * @author Rainer Simon
 */
public class TagSuggestBox extends Composite {
	
	/**
	 * The Oracle
	 */
	private MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
	
	/**
	 * The SuggestBox
	 */
	private SuggestBox suggestBox = new SuggestBox(oracle);
	
	/**
	 * Maximum items shown in the suggestBox;
	 */
	private int limit;
	
	/**
	 * Reference to the RPC Tag Service
	 */
	private TagServiceAsync tagService;
	
	public TagSuggestBox(int maxItems) {
		this.limit = maxItems;
		
		tagService = (TagServiceAsync) 
		GWT.create(TagService.class);
		
		suggestBox.setLimit(limit);
		suggestBox.getTextBox().addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent evt) {
				// Don't handle arrow key events, backspace, etc.
				if (evt.getUnicodeCharCode() != 0) {
					tagService.getTagSuggestions(suggestBox.getText() + evt.getCharCode(), limit,
						new AsyncCallback<Collection<SemanticTag>>() {
							@Override
							public void onFailure(Throwable arg0) {
								// Ignore
							}
	
							@Override
							public void onSuccess(Collection<SemanticTag> tags) {
								for (SemanticTag t : tags) {
									oracle.add(t.getPrimaryLabel());
								}
								suggestBox.showSuggestionList();
							}
					});
				}
			}
		});
		initWidget(suggestBox);
	}
	
	@Override
	public void setStyleName(String style) {
		suggestBox.getTextBox().setStyleName(style);
	}

}
