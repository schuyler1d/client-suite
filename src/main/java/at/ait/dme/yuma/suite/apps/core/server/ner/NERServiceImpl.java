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

package at.ait.dme.yuma.suite.apps.core.server.ner;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import at.ait.dme.yuma.suite.apps.core.server.Config;
import at.ait.dme.yuma.suite.apps.core.shared.model.PlainLiteral;
import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;
import at.ait.dme.yuma.suite.apps.core.shared.server.ner.NERService;
import at.ait.dme.yuma.suite.apps.core.shared.server.ner.NERServiceException;
import at.ait.dme.yuma.suite.apps.core.shared.server.ner.SemanticTagSuggestions;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the {@link NERService}
 * 
 * @author Manuel Gay
 * @author Rainer Simon
 */
public class NERServiceImpl extends RemoteServiceServlet implements NERService {

    private static final long serialVersionUID = 828296400911475297L;

    /**
     * OpenCalais endpoint base URL
     */
	private static final String OPENCALAIS_API_URL = "http://api.opencalais.com/enlighten/rest";
	
	/**
	 * OpenCalais API key property name
	 */
	private static final String OPENCALAIS_API_KEY_PROPERTY = "openCalaisLicenceID";
	
	/**
	 * OpenCalais licence ID 
	 */
	private static String openCalaisLicenceID;
	
	/**
	 * DBpediaLookup endpoint base URL
	 */
	private static final String DBPEDIA_LOOKUP_URL = "http://lookup.dbpedia.org/";
	
	/**
	 * Tags used in DBpedia lookup response
	 */
	private static final String DBPEDIA_RESULT = "Result";
	private static final String DBPEDIA_LABEL = "Label";
	private static final String DBPEDIA_DESCRIPTION = "Description";
	private static final String DBPEDIA_URI = "URI";

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        Config config = new Config(servletConfig, 
        		getClass().getResourceAsStream("enrichment-service.properties"));

        openCalaisLicenceID = config.getStringProperty(OPENCALAIS_API_KEY_PROPERTY);
    }
    
	@Override
	public Collection<SemanticTagSuggestions> getTagSuggestions(String text) 
			throws NERServiceException {
		
		return getTagSuggestionsOpenCalaisDBpedia(text);
	}
	
	private Collection<SemanticTagSuggestions> getTagSuggestionsOpenCalaisDBpedia(String text) 
			throws NERServiceException {
		
		// First, resolve named entities using OpenCalais
		ClientResponse<String> response = getOpenCalaisEndpoint().findEntities(openCalaisLicenceID, text);

        if (response.getStatus() != HttpResponseCodes.SC_OK)
        	throw new NERServiceException(response.getStatus());
		
		// Then, try obtaining links for each entity via DBpediaLookup
		DBpediaLookupEndpoint dbpedia = getDBpediaLookupEndpoint();
		ArrayList<SemanticTagSuggestions> tagSuggestions = new ArrayList<SemanticTagSuggestions>();
		try {
			for (String namedEntity : parseOpenCalaisResponse(response.getEntity())) {
				response = dbpedia.keyWordSearch(namedEntity, "any", "4");
				
				if (response.getStatus() != HttpResponseCodes.SC_OK)
					throw new NERServiceException(response.getStatus());
				
				SemanticTagSuggestions ambiguousTags = new SemanticTagSuggestions(); 
				ambiguousTags.setTitle(namedEntity);
				ambiguousTags.setTags(parseDBpediaLookupResponse(namedEntity, response.getEntity()));
				tagSuggestions.add(ambiguousTags);
			}
		} catch (Exception e) {
			throw new NERServiceException(e.getMessage());
		}
		return tagSuggestions;
	}

  
	private List<String> parseOpenCalaisResponse(String xml) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        
        ArrayList<String> namedEntities = new ArrayList<String>();
        NodeList nodes = doc.getElementsByTagName("c:name");
        String name; 
        for (int i=0; i<nodes.getLength(); i++) {
        	name = nodes.item(i).getTextContent();
        	if (!name.contains(",") && !namedEntities.contains(name)) namedEntities.add(name);
        }
		return namedEntities;
	}

	private Collection<SemanticTag> parseDBpediaLookupResponse(String term, String xml) 
			throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        ArrayList<SemanticTag> tags = new ArrayList<SemanticTag>();
        NodeList results = doc.getElementsByTagName(DBPEDIA_RESULT);
        for (int i=0; i<results.getLength(); i++) {
        	NodeList children = results.item(i).getChildNodes();
        	String title = null;
    		String description = null;
    		String uri = null;
        	for (int j=0; j<children.getLength(); j++) {
        		if (children.item(j).getNodeName().equals(DBPEDIA_LABEL)) {
        			title = children.item(j).getTextContent();
        		} else if (children.item(j).getNodeName().equals(DBPEDIA_DESCRIPTION)) {
        			description = children.item(j).getTextContent();
        		} else if (children.item(j).getNodeName().equals(DBPEDIA_URI)) {
        			uri =  children.item(j).getTextContent();
        		}
        	}
        	
    		if (description != null && uri != null && title != null && title.equalsIgnoreCase(term))
    			tags.add(new SemanticTag(title, new ArrayList<PlainLiteral>(), "", "en", description, uri));
        }
        
        return tags;
	}
    
    private OpenCalaisEndpoint getOpenCalaisEndpoint() {
    	HttpClient client = new HttpClient();
        return ProxyFactory.create(OpenCalaisEndpoint.class, OPENCALAIS_API_URL, 
        		new ApacheHttpClientExecutor(client)); 
    }
    
    private DBpediaLookupEndpoint getDBpediaLookupEndpoint() {
    	HttpClient client = new HttpClient();
        return ProxyFactory.create(DBpediaLookupEndpoint.class, DBPEDIA_LOOKUP_URL,
        		new ApacheHttpClientExecutor(client));
    }
}