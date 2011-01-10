package at.ait.dme.yuma.suite.server.tileset;

import java.io.IOException;
import java.io.Reader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import at.ait.dme.yuma.suite.client.map.Tileset;

/**
 * A utility class for parsing tile scheme descriptor files
 * 
 * @author Rainer Simon
 */
public class TileschemeParser {

	private static final String ERROR_MSG = "Could not parse descriptor file";
		
	public Tileset parseTMSDescriptor(String tilesetUrl, Reader reader) throws JDOMException, IOException {
		Integer width = null;
		Integer height = null;
		Integer zoomlevel = null;
		String extension = "png";
	
		Document doc = new SAXBuilder().build(reader);
		Element root = doc.getRootElement();
		if(root!=null) {
			// Bounding box
			Element boundingBox=root.getChild("BoundingBox");
			if(boundingBox!=null){
				height=Math.round(Math.abs(boundingBox.getAttribute("minx").getFloatValue()));
				width=Math.round(Math.abs(boundingBox.getAttribute("maxy").getFloatValue()));				
			}
			// Zoom levels
			Element tileSets=root.getChild("TileSets");
			zoomlevel=tileSets.getChildren("TileSet").size();
			
			// Image format
			Element tileFormat=root.getChild("TileFormat");
			if (tileFormat!=null){
				extension = tileFormat.getAttribute("extension").getValue();
			}

			return new Tileset(tilesetUrl, height, width, zoomlevel, extension, "tms");		
		}
		
		throw new IOException(ERROR_MSG);
	}
	
	public Tileset parseZoomifyDescriptor(String tilesetUrl, Reader reader) throws JDOMException, IOException {
		double width;
		double height;
		int zoomlevel;
		
		Document doc = new SAXBuilder().build(reader);
		Element root = doc.getRootElement();
		if(root!=null) {
			width = Math.round(Math.abs(root.getAttribute("WIDTH").getFloatValue()));
			height = Math.round(Math.abs(root.getAttribute("HEIGHT").getFloatValue()));
			
			int tileX = (int) Math.ceil(width / 256);
			int tileY = (int) Math.ceil(height / 256);
			int dim = (tileX > tileY) ? tileX : tileY;
			zoomlevel = (int) Math.ceil(Math.log(dim) / Math.log(2)) + 1;

			return new Tileset(tilesetUrl, new Integer((int) height), new Integer((int) width), new Integer((int) zoomlevel), "jpg", "zoomify");
		}
		
		throw new IOException(ERROR_MSG);
	}
	
}
