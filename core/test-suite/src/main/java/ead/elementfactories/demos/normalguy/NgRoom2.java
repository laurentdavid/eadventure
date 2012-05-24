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


package ead.elementfactories.demos.normalguy;


import ead.common.model.elements.conditions.OperationCond;
import ead.common.model.elements.effects.ChangeSceneEf;
import ead.common.model.elements.effects.enums.PhShape;
import ead.common.model.elements.effects.enums.PhType;
import ead.common.model.elements.effects.physics.PhApplyImpluseEf;
import ead.common.model.elements.effects.physics.PhysicsEffect;
import ead.common.model.elements.effects.sceneelements.MoveSceneElementEf;
import ead.common.model.elements.effects.text.SpeakEf;
import ead.common.model.elements.events.ConditionedEv;
import ead.common.model.elements.events.enums.ConditionedEvType;
import ead.common.model.elements.guievents.DragGEv;
import ead.common.model.elements.guievents.MouseGEv;
import ead.common.model.elements.guievents.enums.DragGEvType;
import ead.common.model.elements.scene.EAdScene;
import ead.common.model.elements.scenes.BasicScene;
import ead.common.model.elements.scenes.SceneElement;
import ead.common.model.elements.trajectories.SimpleTrajectoryDefinition;
import ead.common.model.elements.transitions.FadeInTransition;
import ead.common.model.elements.variables.BasicField;
import ead.common.model.elements.variables.SystemFields;
import ead.common.model.elements.variables.operations.MathOp;
import ead.common.model.predef.effects.SpeakSceneElementEf;
import ead.common.params.fills.ColorFill;
import ead.common.params.fills.LinearGradientFill;
import ead.common.resources.assets.drawable.basics.Image;
import ead.common.resources.assets.drawable.basics.shapes.BezierShape;
import ead.common.resources.assets.drawable.basics.shapes.CircleShape;
import ead.common.util.EAdPosition;
import ead.common.util.EAdPosition.Corner;
import ead.elementfactories.EAdElementsFactory;
import ead.elementfactories.demos.scenes.EmptyScene;

/**
 * Room 2. Shape & Physics scene together. When principal character turns on the fan, the balls fall down and clears
 * the area for the user to play the puzzle (Drag & Drop and questions)
 */
public class NgRoom2 extends EmptyScene{
	
	private SceneElement ng;
	private SceneElement door;
	private SceneElement wallpaper;
	private SceneElement fan;
	private SceneElement topFan;
	
	
	public NgRoom2() {
		NgCommon.init();
		setBackground(new SceneElement(new Image("@drawable/ng_room2_bg.png")));
		getBackground().setId("ng_room2_bg");
		
		// Set up character's initial position
		ng = new SceneElement(NgCommon.getMainCharacter());
		ng.setPosition(Corner.TOP_LEFT , 700, 235);
		ng.setInitialScale(0.8f);
		
		// Character can talk in the scene
		SpeakEf effect = new SpeakSceneElementEf(ng);
		EAdElementsFactory
				.getInstance()
				.getStringFactory()
				.setString(
						effect.getString(),
						"Oh... this is getting weird... where the heck am I?");

		ng.addBehavior(MouseGEv.MOUSE_LEFT_PRESSED, effect);
		
		// Area where the character can walk
		SimpleTrajectoryDefinition d = new SimpleTrajectoryDefinition(false);
		d.setLimits(150, 380, 800, 600);
		setTrajectoryDefinition(d);
		
		// Sets up character's movement
		MoveSceneElementEf move = new MoveSceneElementEf();
		move.setId("moveCharacter");
		move.setTargetCoordiantes(SystemFields.MOUSE_SCENE_X,
				SystemFields.MOUSE_SCENE_Y);
		move.setSceneElement(ng);
		move.setUseTrajectory(true);
		getBackground().addBehavior(MouseGEv.MOUSE_LEFT_PRESSED, move);
		
		createElements();
		addElementsInOrder();

	}
	
	/**
	 * Generates the SceneElements
	 */
	private void createElements() {
		door = new SceneElement(new Image("@drawable/ng_room2_door.png"));
		door.setId("ng_room2_door");
		door.setPosition(Corner.TOP_LEFT, 615, 165);
		
		/* Falta crear:
		 * private SceneElement wallpaper;
		private SceneElement fan;
		private SceneElement topFan;*/

	}
	
	/**
	 * Adds the SceneElements in the correct order
	 */
	private void addElementsInOrder() {
		getSceneElements().add(door);
		getSceneElements().add(ng);
	}
	
	/**
	 * Sets door behavior
	 */
	public void setDoor(EAdScene corridor) {
		ChangeSceneEf goToPreviousScene = new ChangeSceneEf(corridor, new FadeInTransition(1000));
		door.addBehavior(MouseGEv.MOUSE_LEFT_PRESSED, goToPreviousScene);
	}
	
	private void setPhysics() {
		PhysicsEffect effect = new PhysicsEffect();
		
		BezierShape circle = new CircleShape(20, 20, 20, 60);
		circle.setPaint(new LinearGradientFill(ColorFill.GREEN,
				new ColorFill(0, 100, 0), 40, 40));

		SceneElement b = new SceneElement( circle);
		b.setId("ball");
		b.setPosition(new EAdPosition(Corner.CENTER, 500, 0));
		getSceneElements().add(b, 0);
		effect.addSceneElement(b);
		b.setVarInitialValue(PhysicsEffect.VAR_PH_TYPE, PhType.DYNAMIC);
		getBackground().addBehavior(
				MouseGEv.MOUSE_LEFT_CLICK,
				new PhApplyImpluseEf(b, new MathOp(
						 "0"), new MathOp(
						 "-1")));
		b.setVarInitialValue(PhysicsEffect.VAR_PH_RESTITUTION, 0.3f);
		b.setVarInitialValue(PhysicsEffect.VAR_PH_SHAPE, PhShape.CIRCULAR);

		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 5; j++) {
				SceneElement e = new SceneElement( circle);
				e.setId("ball" + i + "_" + j);
				e.setPosition(new EAdPosition(Corner.CENTER, i * 60 + 200,j * 60 + 200));
				getSceneElements().add(e);
				effect.addSceneElement(e);
				e.setVarInitialValue(PhysicsEffect.VAR_PH_TYPE, PhType.DYNAMIC);
				getBackground().addBehavior(
						MouseGEv.MOUSE_LEFT_CLICK,
						new PhApplyImpluseEf(e, new MathOp(
								 "0"),
								new MathOp(
										"-100")));
				e.setVarInitialValue(PhysicsEffect.VAR_PH_RESTITUTION, 0.3f);
				e.setVarInitialValue(PhysicsEffect.VAR_PH_SHAPE, PhShape.CIRCULAR);
			}

		ConditionedEv event = new ConditionedEv();
		OperationCond condition = new OperationCond(new BasicField<Boolean>(
				this, BasicScene.VAR_SCENE_LOADED));
		event.setCondition(condition);
		event.addEffect(ConditionedEvType.CONDITIONS_MET, effect);

		getEvents().add(event);
	}
	
	@Override
	public String getSceneDescription() {
		return "A scene with a character moving and talking. Press anywhere in the scene to move the character there. Press on the character to make him talk.";
	}

	public String getDemoName() {
		return "Speak and Move Scene";
	}
}
