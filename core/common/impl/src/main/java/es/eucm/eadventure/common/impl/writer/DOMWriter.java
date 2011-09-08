/**
 * eAdventure (formerly <e-Adventure> and <e-Game>) is a research project of the
 *    <e-UCM> research group.
 *
 *    Copyright 2005-2010 <e-UCM> research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    <e-UCM> is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure, version 2.0
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.eucm.eadventure.common.impl.writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.eucm.eadventure.common.model.EAdElement;
import es.eucm.eadventure.common.model.extra.EAdList;
import es.eucm.eadventure.common.model.extra.EAdMap;
import es.eucm.eadventure.common.params.EAdParam;
import es.eucm.eadventure.common.resources.EAdResources;
import es.eucm.eadventure.common.resources.assets.AssetDescriptor;

/**
 * Abstract implementation of a DOMWriter
 * 
 * @param <T>
 *            The type of the element writen by this DOMWriter
 */
public abstract class DOMWriter<T> {

	public static final String PACKAGE = "es.eucm.eadventure.common.model";

	/**
	 * The xml document builder factory
	 */
	protected DocumentBuilderFactory dbf;

	/**
	 * The xml document builder
	 */
	protected DocumentBuilder db;

	/**
	 * The xml document
	 */
	protected Document doc;

	/**
	 * The node element of this writer
	 */
	protected Element node;

	/**
	 * Element map
	 */
	protected static Map<EAdElement, String> elementMap = new HashMap<EAdElement, String>();

	protected static ArrayList<EAdElement> mappedElement = new ArrayList<EAdElement>();

	/**
	 * A map to store repeated params and save some space in XML
	 */
	protected static Map<String, String> paramsMap = new HashMap<String, String>();

	/**
	 * A map to store repeated assets and save some space in XML
	 */
	protected static ArrayList<AssetDescriptor> mappedAsset = new ArrayList<AssetDescriptor>();

	public static void initMaps() {
		elementMap.clear();
		mappedElement.clear();
		paramsMap.clear();
		mappedAsset.clear();
	}

	/**
	 * Initialize the elements of the DOMWriter
	 * 
	 * @throws ParserConfigurationException
	 */
	public void initilizeDOMWriter() throws ParserConfigurationException {
		dbf = DocumentBuilderFactory.newInstance();
		db = dbf.newDocumentBuilder();
		doc = db.newDocument();
	}

	/**
	 * <p>
	 * Build the actual node created by this DOMWriter
	 * </p>
	 * 
	 * @param data
	 *            The data to be placed in the node
	 * @return The xml node created by the DOMWriter
	 */
	public abstract Element buildNode(T data);

	public DOMWriter<?> getDOMWriter(Object o) {
		DOMWriter<?> writer = null;
		if (o instanceof EAdElement) {
			writer = new ElementDOMWriter();
		} else if (o instanceof EAdList) {
			writer = new ListDOMWriter();
		} else if (o instanceof EAdMap) {
			writer = new MapDOMWriter();
		} else if (o instanceof EAdParam) {
			writer = new ParamDOMWriter();
		} else if (o instanceof EAdResources) {
			writer = new ResourcesDOMWriter();
		} else if (o instanceof AssetDescriptor) {
			writer = new AssetDOMWriter();
		} else {
			writer = new DefaultDOMWriter();
		}
		return writer;
	}

	public String shortClass(String clazz) {
		return clazz.startsWith(PACKAGE) ? clazz.substring(PACKAGE.length())
				: clazz;
	}

}
