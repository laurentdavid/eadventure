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

package ead.common.model.elements.effects;

import ead.common.interfaces.Element;
import ead.common.interfaces.Param;
import ead.common.model.EAdElement;
import ead.common.model.elements.scene.EAdScene;
import ead.common.model.elements.transitions.EAdTransition;
import ead.common.model.elements.transitions.EmptyTransition;

/**
 * <p>
 * Change the current scene, if the next scene is set to null, go to previous
 * possible scene.
 * </p>
 * 
 */
@Element(runtime = ChangeSceneEf.class, detailed = ChangeSceneEf.class)
public class ChangeSceneEf extends AbstractEffect {

	@Param("nextScene")
	private EAdElement nextScene;

	@Param("transition")
	private EAdTransition transition;

	/**
	 * Construct a new EAdChangeScene effect
	 * 
	 * @param id
	 *            The id of the effect
	 */
	public ChangeSceneEf() {
		this(null, EmptyTransition.instance());
	}

	/**
	 * Construct a new EAdChangeScene effect
	 * 
	 * @param id
	 *            The id of the effect
	 * @param nextScene
	 *            The next scene where to go, can be null to go back to previous
	 */
	public ChangeSceneEf(EAdScene nextScene, EAdTransition transition) {
		super();
		setId("changeScene");
		this.nextScene = nextScene;
		this.transition = transition;
		this.setQueueable(true);
	}

	public ChangeSceneEf(EAdScene scene) {
		this(scene, EmptyTransition.instance());
	}

	/**
	 * @return the nextScene. It could be a scene, or a field pointing to a
	 *         scene
	 */
	public EAdElement getNextScene() {
		return nextScene;
	}

	/**
	 * @param nextScene
	 *            the nextScene to set. It should be an EAdScene or a field with
	 *            EAdScene type. If it is neither, then the effect returns to
	 *            the previous scene
	 */
	public void setNextScene(EAdElement nextScene) {
		this.nextScene = nextScene;
	}

	public EAdTransition getTransition() {
		return transition;
	}

	public void setTransition(EAdTransition transition) {
		this.transition = transition;
	}

}
