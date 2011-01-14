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

package at.ait.dme.yuma.suite.map.client.server.exception;

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