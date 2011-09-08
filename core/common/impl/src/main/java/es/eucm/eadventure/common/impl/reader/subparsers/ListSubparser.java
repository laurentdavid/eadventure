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

package es.eucm.eadventure.common.impl.reader.subparsers;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.Attributes;

import es.eucm.eadventure.common.model.extra.EAdList;
import es.eucm.eadventure.common.model.extra.impl.EAdListImpl;

/**
 * Subparser for the list element.
 */
public class ListSubparser extends Subparser {

	private static final Logger logger = Logger.getLogger("ListSubparser");

	/**
	 * The list of elements.
	 */
	protected EAdList<Object> elementList;

	public ListSubparser(Object parent, Attributes attributes) {
		init(parent, attributes);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void init(Object parent, Attributes attributes) {
		String paramValue = attributes.getValue("param");
		if (paramValue != null) {
			Field field = getField(parent, paramValue);
			try {
				field.setAccessible(true);
				elementList = (EAdList<Object>) field.get(parent);
				field.setAccessible(false);
			} catch (IllegalArgumentException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			} catch (IllegalAccessException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		else {
			String clazzName = attributes.getValue("class");
			try {
				Class<?>  clazz = ClassLoader.getSystemClassLoader().loadClass(clazzName);
				elementList = new EAdListImpl(clazz);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.eucm.eadventure.common.impl.reader.subparsers.Subparser#endElement()
	 */
	@Override
	public void endElement() {
		// DO NOTHING
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.eucm.eadventure.common.impl.reader.subparsers.Subparser#addElement
	 * (es.eucm.eadventure.common.model.EAdElement)
	 */
	@Override
	public void addChild(Object element) {
		elementList.add(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.eucm.eadventure.common.impl.reader.subparsers.Subparser#characters
	 * (char[], int, int)
	 */
	@Override
	public void characters(char[] buf, int offset, int len) {
		// DO NOTHING
	}

	public Object getObject() {
		return elementList;
	}

}
