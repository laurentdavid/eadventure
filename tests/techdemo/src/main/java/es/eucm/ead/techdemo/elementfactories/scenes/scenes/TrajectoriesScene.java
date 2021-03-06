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

package es.eucm.ead.techdemo.elementfactories.scenes.scenes;

import es.eucm.ead.model.assets.drawable.basics.Caption;
import es.eucm.ead.model.elements.effects.sceneelements.MoveSceneElementEf;
import es.eucm.ead.model.elements.effects.variables.ChangeFieldEf;
import es.eucm.ead.model.elements.events.SceneElementEv;
import es.eucm.ead.model.elements.events.enums.SceneElementEvType;
import es.eucm.ead.model.elements.operations.BasicField;
import es.eucm.ead.model.elements.operations.SystemFields;
import es.eucm.ead.model.elements.operations.ValueOp;
import es.eucm.ead.model.elements.predef.effects.MakeActiveElementEf;
import es.eucm.ead.model.elements.predef.effects.MoveActiveElementToMouseEf;
import es.eucm.ead.model.elements.scenes.BasicScene;
import es.eucm.ead.model.elements.scenes.SceneElement;
import es.eucm.ead.model.elements.trajectories.EAdTrajectory;
import es.eucm.ead.model.elements.trajectories.NodeTrajectory;
import es.eucm.ead.model.elements.trajectories.Side;
import es.eucm.ead.model.params.fills.ColorFill;
import es.eucm.ead.model.params.fills.LinearGradientFill;
import es.eucm.ead.model.params.guievents.MouseGEv;
import es.eucm.ead.model.params.util.Position;
import es.eucm.ead.model.params.util.Position.Corner;
import es.eucm.ead.techdemo.elementfactories.scenes.normalguy.NgCommon;

public class TrajectoriesScene extends EmptyScene {

	public TrajectoriesScene() {
		this.setId("TrajectoriesScene");
		NgCommon.init();
		setBackgroundFill(new LinearGradientFill(ColorFill.DARK_GRAY,
				ColorFill.LIGHT_GRAY, 800, 600, true));

		SceneElement element = new SceneElement(NgCommon.getMainCharacter());

		element.setPosition(new Position(Corner.BOTTOM_CENTER, 400, 300));

		MakeActiveElementEf effect = new MakeActiveElementEf(element);

		SceneElementEv event = new SceneElementEv();
		event.addEffect(SceneElementEvType.INIT, effect);

		element.getEvents().add(event);

		getSceneElements().add(element);

		getBackground().addBehavior(MouseGEv.MOUSE_LEFT_PRESSED,
				new MoveActiveElementToMouseEf());

		ChangeFieldEf changeSide = new ChangeFieldEf();
		changeSide.addField(new BasicField<Side>(element,
				NodeTrajectory.VAR_CURRENT_SIDE));
		changeSide.setOperation(new ValueOp(null));

		// Sets up character's movement
		MoveSceneElementEf move = new MoveSceneElementEf();
		move.setTargetCoordiantes(SystemFields.MOUSE_SCENE_X,
				SystemFields.MOUSE_SCENE_Y);
		move.setSceneElement(element);
		move.setUseTrajectory(true);
		getBackground().addBehavior(MouseGEv.MOUSE_LEFT_PRESSED, move);

		createTrajectory1();
		createTrajectory2();
		createTrajectory3();

	}

	private String calculateNodeId(int i, int j, int max) {
		return "" + (max * i + j);
	}

	private void createTrajectory1() {
		NodeTrajectory trajectory = new NodeTrajectory();
		trajectory.addNode("0", 50, 300, 3.0f);
		trajectory.addNode("1", 750, 300, 1.0f);
		trajectory.addSide("0", "1", 700);

		ChangeFieldEf effect = new ChangeFieldEf();
		effect.addField(new BasicField<EAdTrajectory>(this,
				BasicScene.VAR_TRAJECTORY_DEFINITION));
		effect.setOperation(new ValueOp(trajectory));

		Caption c = new Caption("techDemo.TrajectoriesScene.1");
		c.setBubblePaint(ColorFill.LIGHT_GRAY);
		SceneElement change = new SceneElement(c);
		change.addBehavior(MouseGEv.MOUSE_LEFT_PRESSED, effect);
		setTrajectoryDefinition(trajectory);
		this.getSceneElements().add(change);
	}

	private void createTrajectory2() {
		NodeTrajectory trajectory = new NodeTrajectory();
		int margin = 60;
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 6; j++)
				trajectory.addNode(calculateNodeId(i, j, 6), j * 100 + margin,
						i * 150 + margin, 1.0f);

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 6; j++) {
				String currentNode = calculateNodeId(i, j, 6);
				// North node
				if (i > 0) {
					trajectory.addSide(currentNode,
							calculateNodeId(i - 1, j, 6), 20);
				}
				// South node
				if (i < 3) {
					trajectory.addSide(currentNode,
							calculateNodeId(i + 1, j, 6), 20);
				}

				// West node
				if (j < 0) {
					trajectory.addSide(currentNode,
							calculateNodeId(i, j - 1, 6), 20);
				}

				// East node
				if (j < 5) {
					trajectory.addSide(currentNode,
							calculateNodeId(i, j + 1, 6), 20);
				}

			}

		trajectory.setInitial("0");

		ChangeFieldEf effect = new ChangeFieldEf();
		effect.addField(new BasicField<EAdTrajectory>(this,
				BasicScene.VAR_TRAJECTORY_DEFINITION));
		effect.setOperation(new ValueOp(trajectory));

		Caption c = new Caption("techDemo.TrajectoriesScene.2");
		c.setBubblePaint(ColorFill.LIGHT_GRAY);
		SceneElement change = new SceneElement(c);
		change.setPosition(50, 0);
		change.addBehavior(MouseGEv.MOUSE_LEFT_PRESSED, effect);
		this.getSceneElements().add(change);
	}

	private void createTrajectory3() {
		NodeTrajectory trajectory = new NodeTrajectory();
		trajectory.addNode("0", 50, 200, 3.0f);
		trajectory.addNode("1", 750, 200, 1.0f);
		trajectory.addNode("2", 350, 400, 1.0f);
		trajectory.addSide("0", "2", 700);
		trajectory.addSide("1", "2", 700);
		trajectory.addSide("1", "0", 700);

		ChangeFieldEf effect = new ChangeFieldEf();
		effect.addField(new BasicField<EAdTrajectory>(this,
				BasicScene.VAR_TRAJECTORY_DEFINITION));
		effect.setOperation(new ValueOp(trajectory));

		Caption c = new Caption("techDemo.TrajectoriesScene.3");
		c.setBubblePaint(ColorFill.LIGHT_GRAY);
		SceneElement change = new SceneElement(c);
		change.setPosition(100, 0);
		change.addBehavior(MouseGEv.MOUSE_LEFT_PRESSED, effect);

		this.getSceneElements().add(change);
	}

}
