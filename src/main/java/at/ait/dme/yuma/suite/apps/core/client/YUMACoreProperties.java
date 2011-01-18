package at.ait.dme.yuma.suite.apps.core.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;

public class YUMACoreProperties {
	
	private static I18NConstants annotationConstants = null;
	
	public static String getObjectURI() {
		return getValue("objectURI");
	}
	
	public static String getBaseUrl() {
		return getValue("baseURL");	
	}
	
	public static String getUser() {
		return getValue("user");
	}
	
	public static I18NConstants getConstants() {
		if (annotationConstants == null)
			annotationConstants = (I18NConstants) GWT.create(I18NConstants.class);
		
		return annotationConstants;
	}
	
	public static native String getUserAgent() /*-{
		return navigator.userAgent.toLowerCase();
	}-*/;

	private static String getValue(String key) {
		String value;
		try {
			Dictionary params = Dictionary.getDictionary("parameters");
			value = params.get(key);
			if (value.equals("null"))
				value = null;
		} catch (Exception e) {
			return null;
		}
		return value;
	}
	
}
