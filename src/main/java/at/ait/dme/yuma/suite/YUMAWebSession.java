package at.ait.dme.yuma.suite;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;

import at.ait.dme.yuma.suite.apps.core.client.User;

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
