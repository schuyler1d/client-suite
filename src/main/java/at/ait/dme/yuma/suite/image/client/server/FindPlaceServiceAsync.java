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

package at.ait.dme.yuma.suite.image.client.server;

import at.ait.dme.yuma.suite.image.client.map.annotation.WGS84Coordinate;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FindPlaceServiceAsync {
	void findPlace(String mapUrl, String query, AsyncCallback<FindPlaceResponse> callback);
	void findPlace(String mapUrl, WGS84Coordinate latlon, AsyncCallback<FindPlaceResponse> callback);
}
