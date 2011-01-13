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

package at.ait.dme.yuma.suite.core.server.util;

import java.io.UnsupportedEncodingException;

/**
 * Encodes a URL conforming to http://www.ietf.org/rfc/rfc3986.txt.
 * See Section 2.4: % => %25
 * 
 * @author Christian Sadilek
 */
public class URLEncoder {

	public static String encode(String url) throws UnsupportedEncodingException {
		// yes, this will always be UTF-8.
		return java.net.URLEncoder.encode(url,"UTF-8").replace("%", "%25");
	}
}
