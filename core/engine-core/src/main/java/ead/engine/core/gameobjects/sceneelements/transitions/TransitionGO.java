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

package ead.engine.core.gameobjects.sceneelements.transitions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;

import ead.common.model.elements.scenes.EAdSceneElement;
import ead.common.model.elements.transitions.EAdTransition;
import ead.engine.core.factories.EventGOFactory;
import ead.engine.core.factories.SceneElementGOFactory;
import ead.engine.core.game.GameState;
import ead.engine.core.gameobjects.sceneelements.SceneGO;
import ead.engine.core.platform.GUI;
import ead.engine.core.platform.assets.AssetHandler;
import ead.engine.core.platform.rendering.GenericCanvas;

public abstract class TransitionGO<T extends EAdTransition> extends SceneGO {

	protected T transition;

	protected SceneGO previousScene;

	public TransitionGO(AssetHandler assetHandler,
			SceneElementGOFactory gameObjectFactory, GUI gui,
			GameState gameState, EventGOFactory eventFactory,
			GenericCanvas canvas) {
		super(assetHandler, gameObjectFactory, gui, gameState, eventFactory,
				canvas);
	}

	@SuppressWarnings("unchecked")
	public void setElement(EAdSceneElement e) {
		super.setElement(e);
		transition = (T) e;
	}

	public abstract void transition(SceneGO nextScene,
			TransitionListener transition);

	@Override
	public boolean handle(Event action) {
		action.cancel();
		return true;
	}

	public Actor hit(float x, float y, boolean touchable) {
		return this;
	}

	public void setPreviousScene(SceneGO scene) {
		getChildren().clear();
		this.previousScene = scene;
		addActor(scene);
	}

	public interface TransitionListener {

		void transitionEnded();
	}

}