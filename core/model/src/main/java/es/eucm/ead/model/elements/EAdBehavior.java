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

package es.eucm.ead.model.elements;

import es.eucm.ead.model.elements.extra.EAdList;
import es.eucm.ead.model.params.guievents.EAdGUIEvent;

/**
 * In eAdventure, we call behavior to effects lists associated with GUI events
 * 
 * 
 */
public interface EAdBehavior extends EAdElement {

	/**
	 * Returns a list with the attached effects to the given event, if exists.
	 * If not, returns {@code null}
	 * 
	 * @param event
	 *            the GUI event
	 * @return a list with the attached effects to the given event, if exists.
	 *         If not, returns {@code null}
	 */
	EAdList<EAdEffect> getEffects(EAdGUIEvent event);

	/**
	 * Adds an effect associated with an event
	 * 
	 * @param event
	 *            the event
	 * @param effect
	 *            the effect associated
	 */
	void addBehavior(EAdGUIEvent event, EAdEffect effect);

	/**
	 * Adds an effect associated with an event
	 * 
	 * @param event
	 *            the event
	 * @param effects
	 *            a list of effects
	 */
	void addBehavior(EAdGUIEvent event, EAdList<EAdEffect> effects);

	/**
	 * Returns all the effects contained for this behavior. This list must NOT
	 * be modified
	 * 
	 * @return
	 */
	EAdList<EAdEffect> getAllEffects();

	/**
	 * Returns if this behavior is empty
	 * @return
	 */
	boolean isEmpty();

}
