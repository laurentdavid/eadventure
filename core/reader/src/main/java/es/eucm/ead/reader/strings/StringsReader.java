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

package es.eucm.ead.reader.strings;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import es.eucm.ead.model.params.text.EAdString;
import es.eucm.ead.tools.xml.XMLNode;
import es.eucm.ead.tools.xml.XMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

@Singleton
public class StringsReader {

	static private Logger logger = LoggerFactory.getLogger(StringsReader.class);

	private XMLParser xmlParser;

	@Inject
	public StringsReader(XMLParser xmlParser) {
		this.xmlParser = xmlParser;
	}

	public Map<EAdString, String> readStrings(String xml) {
		Map<EAdString, String> strings = new LinkedHashMap<EAdString, String>();
		try {
			XMLNode doc = xmlParser.parse(xml);
			if (doc.hasChildNodes()) {
				for (XMLNode n : doc.getChildren()) {
					String name = n.getAttributeValue("name");
					if (name != null) {
						String value = n.getNodeText();
						strings.put(new EAdString(name), value);
					}
				}
			}
		} catch (Exception e) {
			logger.error("Something went wrong:", e);
		}
		return strings;
	}
}
