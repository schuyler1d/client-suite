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

package at.ait.dme.yuma.suite.server.georeferencer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import at.ait.dme.yuma.suite.client.map.Tileset;
import at.ait.dme.yuma.suite.client.map.annotation.WGS84Coordinate;
import at.ait.dme.yuma.suite.client.map.annotation.XYCoordinate;
import at.ait.dme.yuma.suite.server.map.transformation.ControlPoint;

/**
 * A servlet which acts as the landing URL for requests from georeferencer.org. 
 * The request URL looks like this:
 * 
 * http://dme.ait.ac.at/yuma/georeferencer?id=4I8A6MZxOzQeiWpo2S37aZ&rev=201009211223-ANwdJGv
 * 
 * Based on map id and revision, the servlet downloads map metadata from here:
 * 
 * http://www.georeferencer.org/map/4I8A6MZxOzQeiWpo2S37aZ/201009211223-ANwdJGv.json
 *  
 * @author Rainer Simon
 */
public class LandingPageServlet extends HttpServlet  {

	private static final long serialVersionUID = 5638999113254449027L;
	
	private static final String BASE_URL = "http://www.georeferencer.org/map/";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		String rev = request.getParameter("rev");
		
		if ((id != null) && (rev != null)) {
	        HttpClient client = new HttpClient();
	        HttpMethod get = new GetMethod(BASE_URL + id + "/" + rev + ".json");
	        int statusCode = client.executeMethod(get);
	        if (statusCode == HttpStatus.SC_OK) {
	        	// Zoomify tileset
	        	JSONObject document = JSONObject.fromObject(get.getResponseBodyAsString()).getJSONObject("document");
	        	JSONObject pyramid = document.getJSONObject("pyramid");
	        	
	        	int height = (int) document.getDouble("height");
	        	
	        	Tileset tileset = new Tileset(
	        			document.getString("url"),
	        			height,
	        			(int) document.getDouble("width"),
	        			pyramid.getInt("n_levels"),
	        			pyramid.getString("tile_format"),
	        			"zoomify");
	        	
				request.getSession().setAttribute("tileset", tileset);
	        	
	        	// Control points
	        	ArrayList<ControlPoint> controlPoints = new ArrayList<ControlPoint>();
	        	
				@SuppressWarnings("rawtypes")
				Iterator gcps = JSONArray.toCollection(document.getJSONArray("gcps")).iterator();
				while (gcps.hasNext()) {
					JSONObject jso = JSONObject.fromObject(gcps.next());
					controlPoints.add(new ControlPoint(
							(String) jso.get("address"),
							new XYCoordinate((int) jso.getDouble("pixel"), height - (int) jso.getDouble("line")),
							new WGS84Coordinate(jso.getDouble("y"), jso.getDouble("x"))
					));
				}
				
				request.getSession().setAttribute("controlPoints", controlPoints);		
				
				response.sendRedirect("annotate.jsp?tileView=yes&user=test&objectURL=http://georeferencer/" + id);
	        } else {
	        	response.sendError(statusCode);
	        }
		} else {
			response.sendError(400, "No id/revision specified!");
		}
	}

}
