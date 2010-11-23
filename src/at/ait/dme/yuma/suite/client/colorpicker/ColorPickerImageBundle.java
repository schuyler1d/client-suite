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

package at.ait.dme.yuma.suite.client.colorpicker;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/* Images for the ColorPicker
    Copyright (c) 2007 John Dyer (http://johndyer.name)

    Permission is hereby granted, free of charge, to any person
    obtaining a copy of this software and associated documentation
    files (the "Software"), to deal in the Software without
    restriction, including without limitation the rights to use,
    copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the
    Software is furnished to do so, subject to the following
    conditions:

    The above copyright notice and this permission notice shall be
    included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
    OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
    NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
    HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
    WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
    FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
    OTHER DEALINGS IN THE SOFTWARE.
    */
public interface ColorPickerImageBundle extends ClientBundle
{
	public ImageResource bar_blue_bl();
	public ImageResource bar_blue_br();
	public ImageResource bar_blue_tl();
	public ImageResource bar_blue_tr();
	public ImageResource bar_brightness();
	public ImageResource bar_green_bl();
	public ImageResource bar_green_br();
	public ImageResource bar_green_tl();
	public ImageResource bar_green_tr();
	public ImageResource bar_red_bl();
	public ImageResource bar_red_br();
	public ImageResource bar_red_tl();
	public ImageResource bar_red_tr();
	public ImageResource bar_saturation();
	public ImageResource bar_hue();
	public ImageResource bar_white();
	public ImageResource rangearrows();
	public ImageResource map_blue_max();
	public ImageResource map_blue_min();
	public ImageResource map_brightness();
	public ImageResource map_green_max();
	public ImageResource map_green_min();
	public ImageResource map_hue();
	public ImageResource mappoint();
	public ImageResource map_red_max();
	public ImageResource map_red_min();
	public ImageResource map_saturation();
	public ImageResource map_saturation_overlay();
	public ImageResource map_white();
}