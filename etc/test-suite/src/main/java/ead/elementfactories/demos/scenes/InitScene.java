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

package ead.elementfactories.demos.scenes;

import java.util.ArrayList;
import java.util.List;

import com.gwtent.reflection.client.Reflectable;

import ead.common.model.elements.effects.ChangeSceneEf;
import ead.common.model.elements.effects.text.SpeakEf;
import ead.common.model.elements.guievents.EAdMouseEvent;
import ead.common.model.elements.scene.EAdSceneElementDef;
import ead.common.model.elements.scenes.SceneElementDefImpl;
import ead.common.model.elements.scenes.SceneElementImpl;
import ead.common.model.elements.transitions.DisplaceTransition;
import ead.common.model.elements.transitions.enums.DisplaceTransitionType;
import ead.common.model.predef.sceneelements.Button;
import ead.common.params.EAdFontImpl;
import ead.common.params.fills.EAdColor;
import ead.common.params.fills.EAdPaintImpl;
import ead.common.params.paint.EAdFill;
import ead.common.params.text.EAdFont;
import ead.common.resources.assets.drawable.basics.ImageImpl;
import ead.common.util.EAdPosition.Corner;
import ead.elementfactories.EAdElementsFactory;
import ead.elementfactories.StringFactory;
import ead.elementfactories.demos.SceneDemo;
import ead.elementfactories.demos.normalguy.NgMainScreen;

@Reflectable
public class InitScene extends EmptyScene {

	private List<SceneDemo> sceneDemos;

	private SceneElementImpl goBack;

	private EAdSceneElementDef infoButton;
	
	private EAdFill fill = new EAdColor(255, 255, 255, 200);
	
	private EAdFont font = new EAdFontImpl( 18 );

	private EAdPaintImpl speakPaint = new EAdPaintImpl(fill, EAdColor.LIGHT_GRAY, 5);

	public InitScene() {
		this.setBackground(new SceneElementImpl(new ImageImpl("@drawable/techdemo-bg.png")));
		getBackground().setId("background");
		initList();
		initGOBackButton();
		initInfoButton();
		int y = 200;
		int x = 120;
		StringFactory sf = EAdElementsFactory.getInstance().getStringFactory();
		for (SceneDemo s : sceneDemos) {
			Button b = new Button();
			sf.setString(b.getLabel(), s.getDemoName());
			b.setPosition(x, y);
			b.addBehavior(EAdMouseEvent.MOUSE_LEFT_PRESSED,
					new ChangeSceneEf( s, new DisplaceTransition(1000, DisplaceTransitionType.VERTICAL, true)));
			this.getComponents().add(b);
			s.getComponents().add(goBack);
			SceneElementImpl info = new SceneElementImpl(infoButton);
			info.setPosition(Corner.BOTTOM_LEFT, 80, 590);
			SpeakEf effect = new SpeakEf();
			effect.setId("infoSpeak");
			effect.setColor(EAdColor.GRAY, speakPaint);
			effect.setFont(font);
			sf.setString(effect.getString(), s.getSceneDescription());
			info.addBehavior(EAdMouseEvent.MOUSE_LEFT_PRESSED, effect);
			// info.setScale(0.5f);
			s.getComponents().add(info);
			y += 45;
			if (y > 520) {
				y = 200;
				x += 210;
			}
		}
	}

	private void initInfoButton() {
		infoButton = new SceneElementDefImpl(new ImageImpl("@drawable/infobutton.png"));
		infoButton.setId("info");
	}

	private void initGOBackButton() {
		goBack = new SceneElementImpl(new ImageImpl(
				"@drawable/goback.png"));
		goBack.setId("goBack");
		goBack.setPosition(Corner.BOTTOM_LEFT, 10, 590);
		goBack.addBehavior(EAdMouseEvent.MOUSE_LEFT_PRESSED,
				new ChangeSceneEf( this, new DisplaceTransition(1000, DisplaceTransitionType.HORIZONTAL, true)));
		goBack.setScale(0.5f);

	}

	private void initList() {
		sceneDemos = new ArrayList<SceneDemo>();
		sceneDemos.add(new EmptyScene());
		sceneDemos.add(new ShapeScene());
		// sceneDemos.add(new TextsScene());
		sceneDemos.add(new CharacterScene());
		sceneDemos.add(new SpeakAndMoveScene());
		sceneDemos.add(new ComplexElementScene());
		// sceneDemos.add(new SoundScene());
		sceneDemos.add(new DrawablesScene());
		// sceneDemos.add(new MoleGame());
		sceneDemos.add(new ShowQuestionScene());
		// sceneDemos.add(new TrajectoriesScene());
		// sceneDemos.add(new PhysicsScene());
		sceneDemos.add(new PhysicsScene2());
		sceneDemos.add(new DragDropScene());
		sceneDemos.add(new PositionScene());
		sceneDemos.add(new DepthZScene());
		sceneDemos.add(new SharingEffectsScene());
		sceneDemos.add(new InventoryScene());
		sceneDemos.add(new ScrollScene());
		sceneDemos.add(new FiltersDemo());
		// sceneDemos.add(new VideoScene());
		sceneDemos.add(new NgMainScreen());
		// sceneDemos.add(new NgRoom1());

	}

	@Override
	public String getSceneDescription() {
		return "A scene containing the demos scene";
	}

	public String getDemoName() {
		return "Scene Demo Chooser";
	}

}