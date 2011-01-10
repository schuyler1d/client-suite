package at.ait.dme.yuma.suite.client.server.exception;

import java.io.Serializable;

/**
 * Thrown by the tile service when the tileset URL points to a
 * tileset of unsupported format.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class UnsupportedTileSchemeException extends Exception implements Serializable {

	private static final long serialVersionUID = 3770669097079130889L;

	public UnsupportedTileSchemeException() { }
	
	public UnsupportedTileSchemeException(String message) {
		super(message);
	}	
	
	public UnsupportedTileSchemeException(Throwable cause) {
		super(cause);
	}
	
}
