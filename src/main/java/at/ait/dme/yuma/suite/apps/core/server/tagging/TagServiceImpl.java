package at.ait.dme.yuma.suite.apps.core.server.tagging;

import java.util.Collection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.httpclient.HttpClient;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import at.ait.dme.yuma.suite.apps.core.server.Config;
import at.ait.dme.yuma.suite.apps.core.server.annotation.JSONAnnotationHandler;
import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;
import at.ait.dme.yuma.suite.apps.core.shared.server.tagging.TagService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TagServiceImpl extends RemoteServiceServlet implements TagService {

	private static final long serialVersionUID = -7818579036186851298L;

	private static String TAG_SERVER_URL;
	
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        Config config = new Config(servletConfig);
        TAG_SERVER_URL = config.getStringProperty("tag.server.url");
    }
	
	@Override
	public Collection<SemanticTag> getTagSuggestions(String text, int limit) {
    	HttpClient client = new HttpClient();
        TagserverEndpoint tagserver = 
        	ProxyFactory.create(TagserverEndpoint.class, TAG_SERVER_URL,
        		new ApacheHttpClientExecutor(client));
        
        ClientResponse<String> response = tagserver.getTagSuggestions(text, limit);
        JSONArray json = (JSONArray) JSONValue.parse(response.getEntity());
        return JSONAnnotationHandler.parseSemanticTags(json);
	}

}
