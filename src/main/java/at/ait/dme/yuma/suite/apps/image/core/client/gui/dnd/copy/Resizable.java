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

package at.ait.dme.yuma.suite.apps.image.core.client.gui.dnd.copy;

public interface Resizable {
	/**
	 * move the widget.
	 * 
	 * @param right
	 * @param down
	 */
	public void moveBy(int right, int down);

	/**
	 * set the size of the widget
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height);
	
	/**
	 * returns the width of the shape.
	 * 
	 * @return width
	 */
	public int getWidth();
	
	/**
	 * returns the height of the shape.
	 * 
	 * @return height
	 */
	public int getHeight();
	
	/**
	 * returns the absolute left position.
	 * 
	 * @return height
	 */
	public int getAbsoluteLeft();
	
	/**
	 * returns the absolute top position;
	 * 
	 * @return height
	 */
	public int getAbsoluteTop();
	
	/**
	 * returns the relative (to boundary) left position.
	 * 
	 * @return height
	 */
	public int getRelativeLeft();
	
	/**
	 * returns the relative (to boundary) top position;
	 * 
	 * @return height
	 */
	public int getRelativeTop();	
}
