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

package es.eucm.eadventure.engine.core.gameobjects;

import java.util.logging.Logger;

import com.google.inject.Inject;

import es.eucm.eadventure.common.model.elements.EAdEffect;
import es.eucm.eadventure.common.model.elements.VideoScene;
import es.eucm.eadventure.common.resources.StringHandler;
import es.eucm.eadventure.common.resources.assets.multimedia.Video;
import es.eucm.eadventure.engine.core.game.GameState;
import es.eucm.eadventure.engine.core.gameobjects.factories.EventGOFactory;
import es.eucm.eadventure.engine.core.gameobjects.factories.SceneElementGOFactory;
import es.eucm.eadventure.engine.core.gameobjects.go.SceneGO;
import es.eucm.eadventure.engine.core.gameobjects.sceneelements.SceneElementGOImpl;
import es.eucm.eadventure.engine.core.input.InputAction;
import es.eucm.eadventure.engine.core.platform.AssetHandler;
import es.eucm.eadventure.engine.core.platform.GUI;
import es.eucm.eadventure.engine.core.platform.SpecialAssetRenderer;
import es.eucm.eadventure.engine.core.util.EAdTransformation;

public class VideoSceneGO extends SceneElementGOImpl<VideoScene> implements
		SceneGO<VideoScene> {

	private static final Logger logger = Logger.getLogger("VideoScreenGOImpl");

	private SpecialAssetRenderer<Video, ?> specialAssetRenderer;

	private Object component;

	@Inject
	public VideoSceneGO(AssetHandler assetHandler, StringHandler stringsReader,
			SceneElementGOFactory gameObjectFactory, GUI gui,
			GameState gameState,
			SpecialAssetRenderer<Video, ?> specialAssetRenderer,
			EventGOFactory eventFactory) {
		super(assetHandler, stringsReader, gameObjectFactory, gui, gameState,
				eventFactory);
		logger.info("New instance");
		this.specialAssetRenderer = specialAssetRenderer;
		this.component = null;
	}

	public void doLayout(EAdTransformation transformation) {
		if (component == null)
			component = specialAssetRenderer.getComponent((Video) element
					.getDefinition().getAsset(VideoScene.video));
		if (specialAssetRenderer.isFinished()) {
			gui.showSpecialResource(null, 0, 0, true);
			component = null;
		} else
			gui.showSpecialResource(component, 0, 0, true);
	}

	@Override
	public boolean acceptsVisualEffects() {
		return false;
	}

	@Override
	public void update() {
		super.update();
		if (specialAssetRenderer.isFinished()) {
			gui.showSpecialResource(null, 0, 0, true);
			for (EAdEffect e : element.getFinalEffects()) {
				gameState.addEffect(e);
			}
		} else {
			specialAssetRenderer.start();
		}
	}

	@Override
	public boolean contains(int x, int y) {
		return false;
	}

	@Override
	public boolean processAction(InputAction<?> action) {
		return false;
	}

}