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

package ead.common.importer.subimporters.chapter.scene.elements;

import com.google.inject.Inject;

import ead.common.EAdElementImporter;
import ead.common.importer.interfaces.EAdElementFactory;
import ead.common.importer.interfaces.EffectsImporterFactory;
import ead.common.importer.interfaces.ResourceImporter;
import ead.common.importer.subimporters.effects.TriggerSceneImporter;
import ead.common.model.elements.EAdCondition;
import ead.common.model.elements.EAdEffect;
import ead.common.model.elements.conditions.NOTCond;
import ead.common.model.elements.effects.ChangeSceneEf;
import ead.common.model.elements.guievents.EAdMouseEvent;
import ead.common.model.elements.scene.EAdScene;
import ead.common.model.elements.scene.EAdSceneElement;
import ead.common.model.elements.scenes.SceneElementImpl;
import ead.common.model.elements.transitions.EAdTransition;
import ead.common.model.predef.effects.ChangeCursorEf;
import ead.common.params.text.EAdString;
import ead.common.resources.StringHandler;
import ead.common.resources.assets.drawable.basics.Image;
import ead.common.resources.assets.drawable.basics.ImageImpl;
import es.eucm.eadventure.common.data.adventure.AdventureData;
import es.eucm.eadventure.common.data.chapter.Exit;
import es.eucm.eadventure.common.data.chapter.ExitLook;
import es.eucm.eadventure.common.data.chapter.conditions.Conditions;
import es.eucm.eadventure.common.data.chapter.effects.Effect;

public class ExitImporter extends ElementImporter<Exit> {

	private static int ID_GENERATOR = 0;
	private EffectsImporterFactory effectsImporterFactory;
	private ResourceImporter resourceImporter;

	@Inject
	public ExitImporter(
			EAdElementImporter<Conditions, EAdCondition> conditionsImporter,
			EAdElementFactory factory,
			EffectsImporterFactory effectsImporterFactory,
			StringHandler stringHandler, ResourceImporter resourceImporter) {
		super(factory, conditionsImporter, stringHandler);
		this.effectsImporterFactory = effectsImporterFactory;
		this.resourceImporter = resourceImporter;
	}

	public EAdSceneElement init(Exit oldObject) {
		SceneElementImpl newExit = new SceneElementImpl();
		newExit.setId("exit" + ID_GENERATOR++);
		return newExit;
	}

	@Override
	public EAdSceneElement convert(Exit oldObject, Object object) {
		SceneElementImpl newExit = (SceneElementImpl) object;
		newExit.setPropagateGUIEvents(false);

		// Shape
		setShape(newExit, oldObject);

		// Enable condition
		EAdCondition enableCondition = getEnableCondition(oldObject
				.getConditions());

		// If the exit has not-effects, it is always visible
		if (!oldObject.isHasNotEffects())
			super.addVisibleEvent(newExit, enableCondition);

		// Add influence area
		addInfluenceArea(newExit, oldObject, oldObject.getInfluenceArea());

		// Add get to the exit
		ChangeSceneEf changeScene = addGoToExit(newExit, oldObject,
				enableCondition);

		// Add appearance (name and cursor)
		addAppearance(newExit, oldObject);

		// Effects
		addEffects(newExit, oldObject, enableCondition, changeScene);

		return newExit;
	}

	private ChangeSceneEf addGoToExit(SceneElementImpl newExit, Exit oldObject,
			EAdCondition enableCondition) {

		// FIXME this should stop effects util later (set blockig is not
		// enough?)
		// Change scene effect
		EAdScene scene = (EAdScene) factory.getElementById(oldObject
				.getNextSceneId());

		EAdTransition transition = TriggerSceneImporter.getTransition(
				oldObject.getTransitionType(), oldObject.getTransitionTime());

		ChangeSceneEf changeScene = new ChangeSceneEf(scene, transition);
		changeScene.setId("change_screen_" + newExit.getId());
		changeScene.setCondition(enableCondition);

		// Post effects
		for (Effect e : oldObject.getPostEffects().getEffects()) {
			EAdEffect eadEffect = effectsImporterFactory.getEffect(e);
			changeScene.getNextEffects().add(eadEffect);
		}

		addGoToExit(newExit, oldObject, enableCondition, changeScene);
		return changeScene;

	}

	private void addAppearance(SceneElementImpl newExit, Exit oldObject) {
		// Add name
		ExitLook exitLook = oldObject.getDefaultExitLook();

		EAdString name = EAdString.newEAdString("exitLookName");
		stringHandler.setString(name, exitLook.getExitText());
		newExit.setVarInitialValue(SceneElementImpl.VAR_NAME, name);

		// Change cursor
		Image cursor = null;
		if (exitLook.getCursorPath() == null)
			// Default
			cursor = factory.getDefaultCursor(AdventureData.EXIT_CURSOR);
		else
			cursor = (ImageImpl) resourceImporter.getAssetDescritptor(
					exitLook.getCursorPath(), ImageImpl.class);
		
		ChangeCursorEf changeCursor = new ChangeCursorEf(cursor);
		ChangeCursorEf changeCursorBack = new ChangeCursorEf(factory.getDefaultCursor(AdventureData.DEFAULT_CURSOR));

		newExit.addBehavior(EAdMouseEvent.MOUSE_ENTERED, changeCursor);
		newExit.addBehavior(EAdMouseEvent.MOUSE_EXITED, changeCursorBack);
	}

	private void addEffects(SceneElementImpl newExit, Exit oldObject,
			EAdCondition enableCondition, EAdEffect changeSceneEffect) {
		// Normal effects
		for (Effect e : oldObject.getEffects().getEffects()) {
			EAdEffect eadEffect = effectsImporterFactory.getEffect(e);
			eadEffect.setCondition(enableCondition);
			changeSceneEffect.getNextEffects().add(eadEffect);
		}

		// No effects
		for (Effect e : oldObject.getNotEffects().getEffects()) {
			EAdEffect eadEffect = effectsImporterFactory.getEffect(e);
			eadEffect.setCondition(new NOTCond(enableCondition));
			newExit.addBehavior(EAdMouseEvent.MOUSE_LEFT_CLICK, eadEffect);
		}
	}

}