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

package at.ait.dme.yuma.suite.client.map.explore;

import at.ait.dme.yuma.suite.client.map.TiledImageComposite;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * Panel containing the GUI elements needed for the 'Exploration' KML overlay
 * feature (see {@link ExplorationComposite}).
 * 
 * @author Rainer Simon
 */
public class KMLOverlayPanel extends FlowPanel {

	/**
	 * Reference to the KML layer
	 */
	private KMLLayer kmlLayer;
	
	/**
	 * Radio button group name
	 */
	private static final String RADIO_GROUP = "kml";
	
	/**
	 * KML file URLs
	 */
	private static final String KML_TRADE_ROUTES = "http://dme.ait.ac.at/yuma/kml/traderoutes-mediterrenean-2nd-century.kml";
	private static final String KML_AGE_OF_DISCOVERY = "http://dme.ait.ac.at/yuma/kml/ageofdiscovery.kml";
	private static final String KML_COUNTRY_BORDERS = "http://dme.ait.ac.at/yuma/kml/country-borders.kml";
	private static final String KML_PTOLEMY = "http://dme.ait.ac.at/yuma/kml/ptolemy.kml";
	private static final String KML_CHINESE_ROADS = "http://dme.ait.ac.at/yuma/kml/prc_roads.kml";
	private static final String KML_RAINERS_LOCATION = "http://geekvault.no5.at/blogloc/kml/aboutgeo.kml";
	
	public KMLOverlayPanel(TiledImageComposite imageComposite) {
		this.kmlLayer = imageComposite.getKMLLayer();
		setStyleName("explore-KMLOverlay");
		
		this.add(createRadioButton(
					KML_TRADE_ROUTES,
					"2nd Century Trade Routes (<a href=\"" + KML_TRADE_ROUTES + "\">KML</a>)",
					false
				));
		
		this.add(createRadioButton(
				KML_AGE_OF_DISCOVERY,
				"16th Century Famous Explorers (<a href=\"" + KML_AGE_OF_DISCOVERY + "\">KML</a>)",
				false
			));
		
		this.add(createRadioButton(
				KML_COUNTRY_BORDERS,
				"Country Borders (<a href=\"" + KML_COUNTRY_BORDERS + "\">KML</a>)",
				false
			));

		this.add(createRadioButton(
				KML_PTOLEMY,
				"Ptolemy's Geographia (<a href=\"" + KML_PTOLEMY + "\">KML</a>)",
				false
			));
		
		this.add(createRadioButton(
				KML_CHINESE_ROADS,
				"Bejing Road Network (<a href=\"" + KML_CHINESE_ROADS + "\">KML</a>)",
				false
			));
		
		this.add(createRadioButton(
				KML_RAINERS_LOCATION,
				"Rainer's Live Location (<a href=\"" + KML_RAINERS_LOCATION + "\">KML</a>)",
				false
			));

		this.add(createRadioButton(
				null,
				"None",
				true
			));	
	}
	
	private HorizontalPanel createRadioButton(final String kml, String label, boolean preselected) {
		HorizontalPanel panel = new HorizontalPanel();
		RadioButton radio = new RadioButton(RADIO_GROUP);
		radio.setValue(preselected);
		radio.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (kml != null) {
					kmlLayer.showKml(kml);
				} else{
					kmlLayer.hideKml();
				}
			}
		});
		panel.add(radio);
		panel.add(new InlineHTML(label));
		return panel;
	}

}
