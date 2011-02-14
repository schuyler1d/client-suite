package at.ait.dme.yuma.suite.apps.core.server.tagging;

import java.util.ArrayList;
import java.util.Collection;

import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;
import at.ait.dme.yuma.suite.apps.core.shared.server.tagging.TagService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TagServiceImpl extends RemoteServiceServlet implements TagService {

	private static final long serialVersionUID = -7818579036186851298L;

	@Override
	public Collection<SemanticTag> getTagSuggestions(String text, int limit) {
		ArrayList<SemanticTag> suggestions = new ArrayList<SemanticTag>(); 
		
		return suggestions;
	}

}
