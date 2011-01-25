package at.ait.dme.yuma.suite.apps.core.shared.model;

/**
 * A simple class representing a 'plain literal', 
 * i.e. a String with an optional language code.
 * 
 * @author Rainer Simon
 */
public class PlainLiteral {

	/**
	 * The alternative label
	 */
	private String value;
	
	/**
	 * The optional language
	 */
	private String lang = null;
	
	public PlainLiteral(String value) {
		this.value = value;
	}
	
	public PlainLiteral(String altLabel, String lang) {
		this.value = altLabel;
		this.lang = lang;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getLanguage() {
		return lang;
	}
	
}
