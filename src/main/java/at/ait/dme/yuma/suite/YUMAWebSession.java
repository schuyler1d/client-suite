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

package at.ait.dme.yuma.suite;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;

import at.ait.dme.yuma.suite.apps.core.shared.model.User;

public final class YUMAWebSession extends WebSession {
	
	private static final long serialVersionUID = -70708036400304230L;

	private User user;

	public YUMAWebSession(Request request) {
		super(request);
	}

	public final User getUser() {
		return user;
	}

	public final void setUser(User user) {
		this.user = user;
		this.bind();
	}

	public static YUMAWebSession get() {
		return (YUMAWebSession) Session.get();
	}
	
}
