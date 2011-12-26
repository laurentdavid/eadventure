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

package es.eucm.eadventure.common.model.elements;

import es.eucm.eadventure.common.interfaces.features.Conditioned;
import es.eucm.eadventure.common.model.EAdElement;
import es.eucm.eadventure.common.model.elements.extra.EAdList;

/**
 * <p>An effect in the eAdventure model represents any set of
 * actions to be taken in the game, both modifying the runtime model
 * (e.g. increasing a variable) or modifying the view (e.g. starting
 * an animation).</p>
 * 
 */
public interface EAdEffect extends EAdElement, Conditioned {

	/**
	 * If returns {@code true} means no subsequent effects will be triggered
	 * until this effect is finished. If returns {@code false}, other effects
	 * can be executed in parallel with this one
	 * 
	 * @return
	 */
	boolean isBlocking();

	/**
	 * If returns {@code true} means that GUI events will be only processed for
	 * this effect or those which are over it
	 * 
	 * @return
	 */
	boolean isOpaque();

	boolean isQueueable();
	
	void setQueueable( boolean queueable );
	
	void setBlocking( boolean blocking );
	
	void setOpaque( boolean opaque );
	
	/**
	 * Returns the effects to be launched when this effect ends
	 * @return
	 */
	EAdList<EAdEffect> getNextEffects();

}