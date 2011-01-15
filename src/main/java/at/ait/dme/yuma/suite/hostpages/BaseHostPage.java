package at.ait.dme.yuma.suite.hostpages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

public abstract class BaseHostPage extends WebPage {
	
	protected String user;
	
	protected String objectURI;
	
	public BaseHostPage(String title, String js, final PageParameters parameters) {
		add(new Label("title", title));	
		add(JavascriptPackageResource.getHeaderContribution(js));

		user = parameters.getString("user");
		objectURI = parameters.getString("objectURI");
		
		String dictionary = "var parameters = {\n" +
							"objectURL:	\"" + objectURI + "\",\n" +
							"imageURL:	\"" + objectURI + "\",\n" +
							"user:	\"" + user + "\" }\n";
		
		add(new Label("dictionary", dictionary).setEscapeModelStrings(false));
    }; 

}
