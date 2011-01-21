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

package at.ait.dme.yuma.suite.apps.core.client;

import java.io.Serializable;

/**
 * A simple user object.
 * 
 * @author Rainer Simon
 */
public class User implements Serializable {

	private static final long serialVersionUID = -6027774847347220416L;
	
	/**
	 * Screen name of the anonymous user
	 */
	private static final String ANONYMOUS_NAME = "guest";
	
	/**
	 * An anonymous user
	 */
	public static final User ANONYMOUS = new User(null);
	
	/**
	 * The user 'singleton' user for this GWT app
	 */
	private static User instance = null;

	/**
	 * The username
	 */
	private String username = null;
	
	/**
	 * The user's Gravatar URL (if any)
	 */
	private String gravatarUrl = null;
	
	/**
	 * A URI for this user (if any)
	 */
	private String uri = null;
	
	public User() {}
	
	public User(String username) {
		this.username = username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		if (username == null)
			return ANONYMOUS_NAME;
		
		return username;
	}

	public void setGravatarHash(String hash) {
		this.gravatarUrl = "http://www.gravatar.com/avatar/"
			 + hash + "?s=20&d=mm";
	}

	public String getGravatarURL() {
		if (gravatarUrl == null)
			return "http://www.gravatar.com/avatar/?s=20&d=mm";
		
		return gravatarUrl;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
	
	public boolean isAnonymous() {
		return username == null;
	}
	
	public static void set(User user) {
		instance = user;
	}
	
	public static User get() {
		return instance;
	}
	
}
