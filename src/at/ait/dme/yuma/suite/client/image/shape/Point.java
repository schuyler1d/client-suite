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

package at.ait.dme.yuma.suite.client.image.shape;

import java.io.Serializable;

/**
 * represents a point
 * 
 * @author Christian Sadilek
 */
public class Point implements Serializable {
	private static final long serialVersionUID = -8573004893208810833L;
	
	private int x,y;

	public Point() {}
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Point)) return false;
		if(this==obj) return true;
		
		Point point = (Point)obj;
		if(this.x!=point.getX()) return false;
		if(this.y!=point.getY()) return false;
		
		return true;
	
	}

	@Override
	public int hashCode() {
		return x ^ y;
	}
	
	@Override
	public String toString() {
		return "x:"+x+" y:"+y;
	}
	
}
