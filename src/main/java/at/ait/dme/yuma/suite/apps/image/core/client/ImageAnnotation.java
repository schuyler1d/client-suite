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

package at.ait.dme.yuma.suite.apps.image.core.client;

import at.ait.dme.yuma.suite.apps.core.client.datamodel.Annotation;
import at.ait.dme.yuma.suite.apps.image.core.client.shape.VoidShape;

/**
 * Represents an image annotation with an unique addressable URI as id.
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
public class ImageAnnotation extends Annotation {
		
	private static final long serialVersionUID = 7807008224822381714L;
	
	public ImageAnnotation() {
		super();
		setFragment(new ImageFragment(new VoidShape()));
	}
	
	public String toHtml() {
		StringBuffer buf = new StringBuffer();
		buf.append("<html>")		
		.append("<head><title>")
		.append(getTitle())
		.append("</title></head>")
		.append("<body>")	
		.append(getText())	
		.append("</body>")		
		.append("</html>");			
		return buf.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ImageAnnotation))
			return false;
		
		if (!super.equals(other))
			return false;
				
		return true;	
	}

}
