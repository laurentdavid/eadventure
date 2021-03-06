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

package es.eucm.ead.model.elements.scenes;

import es.eucm.ead.model.interfaces.features.Evented;
import es.eucm.ead.model.interfaces.features.Variabled;
import es.eucm.ead.model.interfaces.features.WithBehavior;
import es.eucm.ead.model.elements.EAdElement;
import es.eucm.ead.model.params.util.Position;

/**
 * 
 * A scene element is the minimal unit to build games. A scene element is
 * anything displayed in the game.
 * 
 */
public interface EAdSceneElement extends EAdElement, WithBehavior, Variabled,
		Evented {

	/**
	 * Returns the definition for this scene element
	 * 
	 * @return the definition for this scene element
	 */
	EAdSceneElementDef getDefinition();

	/**
	 * Sets the position for the scene element
	 * 
	 * @param corner
	 *            center
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordiante
	 */
	void setPosition(Position.Corner topLeft, float i, float j);

	/**
	 *  If for the contains method, must be used only the scene element bounds
	 */
	public boolean isContainsBounds();

	void setInitialBundle(String name);

}
