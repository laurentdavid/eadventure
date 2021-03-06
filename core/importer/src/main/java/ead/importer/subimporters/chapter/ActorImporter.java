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

package ead.importer.subimporters.chapter;

import com.google.inject.Inject;
import es.eucm.ead.model.assets.multimedia.EAdSound;
import es.eucm.ead.model.assets.multimedia.Sound;
import es.eucm.ead.model.elements.EAdCondition;
import es.eucm.ead.model.elements.EAdEffect;
import es.eucm.ead.model.elements.conditions.EmptyCond;
import es.eucm.ead.model.elements.effects.ActorActionsEf;
import es.eucm.ead.model.elements.effects.TriggerMacroEf;
import es.eucm.ead.model.elements.effects.text.SpeakEf;
import es.eucm.ead.model.elements.effects.variables.ChangeFieldEf;
import es.eucm.ead.model.elements.extra.EAdList;
import es.eucm.ead.model.elements.operations.BasicField;
import es.eucm.ead.model.elements.operations.EAdField;
import es.eucm.ead.model.elements.operations.ValueOp;
import es.eucm.ead.model.elements.scenes.EAdSceneElementDef;
import es.eucm.ead.model.elements.scenes.SceneElementDef;
import es.eucm.ead.model.params.guievents.MouseGEv;
import es.eucm.ead.model.params.text.EAdString;
import ead.importer.EAdElementImporter;
import ead.importer.annotation.ImportAnnotator;
import ead.importer.annotation.ImportAnnotator.Key;
import ead.importer.annotation.ImportAnnotator.Type;
import ead.importer.interfaces.EAdElementFactory;
import ead.importer.interfaces.ResourceImporter;
import es.eucm.ead.tools.StringHandler;
import es.eucm.eadventure.common.data.adventure.DescriptorData.DefaultClickAction;
import es.eucm.eadventure.common.data.chapter.Action;
import es.eucm.eadventure.common.data.chapter.conditions.Conditions;
import es.eucm.eadventure.common.data.chapter.elements.Description;
import es.eucm.eadventure.common.data.chapter.elements.Element;

import java.util.List;
import java.util.Map;

public abstract class ActorImporter<P extends Element> implements
		EAdElementImporter<P, EAdSceneElementDef> {

	protected StringHandler stringHandler;

	protected ResourceImporter resourceImporter;

	protected Map<String, Object> objectClasses;

	protected Map<String, String> properties;

	protected EAdElementFactory elementFactory;

	protected ActionImporter actionImporter;

	protected P element;

	protected EAdElementFactory factory;

	protected ImportAnnotator annotator;

	protected EAdElementImporter<Conditions, EAdCondition> conditionsImporter;

	@Inject
	public ActorImporter(StringHandler stringHandler,
			ResourceImporter resourceImporter,
			EAdElementFactory elementFactory,
			EAdElementImporter<Action, EAdSceneElementDef> actionImporter,
			EAdElementFactory factory, ImportAnnotator annotator,
			EAdElementImporter<Conditions, EAdCondition> conditionsImporter) {
		this.stringHandler = stringHandler;
		this.resourceImporter = resourceImporter;
		this.elementFactory = elementFactory;
		this.actionImporter = (ActionImporter) actionImporter;
		this.factory = factory;
		this.annotator = annotator;
		this.conditionsImporter = conditionsImporter;
	}

	@Override
	public EAdSceneElementDef init(P oldObject) {
		this.element = oldObject;
		return new SceneElementDef();
	}

	@Override
	public EAdSceneElementDef convert(P oldObject, Object object) {
		SceneElementDef actor = (SceneElementDef) object;
		annotator.annotate(actor, Type.Open);

		if (actor.getId().equals(
				elementFactory.getCurrentOldChapterModel().getPlayer().getId())) {
			annotator.annotate(actor, Type.Entry, Key.Role, "actor.player");
		}

		EAdEffect[] sounds = setDocumentation(resourceImporter,
				conditionsImporter, stringHandler,
				oldObject.getDocumentation(), oldObject.getDescriptions(),
				actor);

		elementFactory.getCurrentChapterModel().getActors().add(actor);

		// Add resources
		initResourcesCorrespondencies();
		resourceImporter.importResources(actor, oldObject.getResources(),
				properties, objectClasses);

		// Add actions
		addDefaultBehavior(stringHandler, factory, actionImporter, oldObject
				.getActions(), actor, sounds);

		annotator.annotate(actor, ImportAnnotator.Type.Close);

		return actor;
	}

	public static EAdEffect[] setDocumentation(
			ResourceImporter resourceImporter,
			EAdElementImporter<Conditions, EAdCondition> conditionsImporter,
			StringHandler stringHandler, String docString,
			List<Description> descriptions, EAdSceneElementDef actor) {
		TriggerMacroEf[] soundEffects = new TriggerMacroEf[2];

		// Documentation
		EAdString documentation = stringHandler.generateNewString();
		stringHandler.setString(documentation, docString);
		actor.setDoc(documentation);

		TriggerMacroEf triggerMacro = new TriggerMacroEf();
		EAdField<EAdString> nameField = new BasicField<EAdString>(actor,
				SceneElementDef.VAR_DOC_NAME);
		EAdField<EAdString> descField = new BasicField<EAdString>(actor,
				SceneElementDef.VAR_DOC_DESC);
		EAdField<EAdString> detailedDesc = new BasicField<EAdString>(actor,
				SceneElementDef.VAR_DOC_DETAILED_DESC);
		for (Description d : descriptions) {
			EAdString[] strings = setStrings(stringHandler, d);
			EAdCondition c = null;
			if (d.getConditions() == null) {
				c = EmptyCond.TRUE;
			} else {
				c = conditionsImporter.init(d.getConditions());
				c = conditionsImporter.convert(d.getConditions(), c);
			}

			EAdList<EAdEffect> macro = new EAdList<EAdEffect>();
			macro.add(new ChangeFieldEf(nameField, new ValueOp(strings[0])));
			macro.add(new ChangeFieldEf(descField, new ValueOp(strings[1])));
			macro.add(new ChangeFieldEf(detailedDesc, new ValueOp(strings[2])));

			// Sound
			if (d.getNameSoundPath() != null
					&& !d.getNameSoundPath().equals("")) {
				EAdSound sound = (EAdSound) resourceImporter
						.getAssetDescritptor(d.getNameSoundPath(), Sound.class);
				//macro.add(new PlaySoundEf(sound, false));
			}

			if (d.getDescriptionSoundPath() != null
					&& !d.getDescriptionSoundPath().equals("")) {
				EAdSound sound = (EAdSound) resourceImporter
						.getAssetDescritptor(d.getDescriptionSoundPath(),
								Sound.class);
				if (soundEffects[0] == null) {
					soundEffects[0] = new TriggerMacroEf();
				}
				soundEffects[0].putEffects(c, new EAdList<EAdEffect>());
			}

			if (d.getDetailedDescriptionSoundPath() != null
					&& !d.getDescriptionSoundPath().equals("")) {
				EAdSound sound = (EAdSound) resourceImporter
						.getAssetDescritptor(d
								.getDetailedDescriptionSoundPath(), Sound.class);
				if (soundEffects[1] == null) {
					soundEffects[1] = new TriggerMacroEf();
				}
				soundEffects[1].putEffects(c, new EAdList<EAdEffect>());
			}

			triggerMacro.putEffects(c, macro);
		}

		// Generate default case (set to null all the strings)
		EAdList<EAdEffect> macro = new EAdList<EAdEffect>();
		macro.add(new ChangeFieldEf(nameField, new ValueOp(null)));
		macro.add(new ChangeFieldEf(descField, new ValueOp(null)));
		macro.add(new ChangeFieldEf(detailedDesc, new ValueOp(null)));
		triggerMacro.putEffects(EmptyCond.TRUE, macro);

		actor.addBehavior(MouseGEv.MOUSE_ENTERED, triggerMacro);

		return soundEffects;

	}

	private static EAdString[] setStrings(StringHandler stringHandler,
			Description d) {
		// FIXME translate texts
		EAdString name = stringHandler.generateNewString();
		EAdString desc = stringHandler.generateNewString();
		EAdString detailedDesc = stringHandler.generateNewString();

		stringHandler.setString(name, d.getName());
		stringHandler.setString(desc, d.getDescription());
		stringHandler.setString(detailedDesc, d.getDetailedDescription());

		return new EAdString[] { name, desc, detailedDesc };
	}

	public abstract void initResourcesCorrespondencies();

	public static void addDefaultBehavior(StringHandler stringHandler,
			EAdElementFactory factory,
			EAdElementImporter<Action, EAdSceneElementDef> actionImporter,
			List<Action> actions, SceneElementDef actor, EAdEffect[] sounds) {

		// add actions
		ActorActionsEf showActions = new ActorActionsEf(actor);
		actor.addBehavior(MouseGEv.MOUSE_RIGHT_PRESSED, showActions);

		if (factory.getOldDataModel().getDefaultClickAction() == DefaultClickAction.SHOW_ACTIONS) {
			actor.addBehavior(MouseGEv.MOUSE_LEFT_PRESSED, showActions);
		} else {
			SpeakEf showDescription = new SpeakEf(stringHandler
					.generateNewString());
			EAdField<EAdString> desc = new BasicField<EAdString>(actor,
					SceneElementDef.VAR_DOC_DESC);
			showDescription.getCaption().getOperations().add(desc);
			stringHandler.setString(showDescription.getCaption().getText(),
					"[0]");
			actor.addBehavior(MouseGEv.MOUSE_LEFT_PRESSED, showDescription);
			if (sounds[0] != null) {
				actor.addBehavior(MouseGEv.MOUSE_LEFT_PRESSED, sounds[0]);
			}
		}

		// add other actions
		((ActionImporter) actionImporter).addAllActions(actions, actor, false,
				sounds[1]);
	}

}
