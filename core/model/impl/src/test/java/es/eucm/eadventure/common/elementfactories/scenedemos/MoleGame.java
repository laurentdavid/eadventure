package es.eucm.eadventure.common.elementfactories.scenedemos;

import es.eucm.eadventure.common.model.effects.EAdEffect;
import es.eucm.eadventure.common.model.effects.impl.EAdInterpolationEffect;
import es.eucm.eadventure.common.model.effects.impl.enums.InterpolationType;
import es.eucm.eadventure.common.model.effects.impl.enums.InterpolationLoopType;
import es.eucm.eadventure.common.model.effects.impl.variables.EAdChangeFieldValueEffect;
import es.eucm.eadventure.common.model.elements.EAdSceneElement;
import es.eucm.eadventure.common.model.elements.impl.EAdBasicSceneElement;
import es.eucm.eadventure.common.model.events.EAdSceneElementTimedEvent;
import es.eucm.eadventure.common.model.events.enums.SceneElementTimedEventType;
import es.eucm.eadventure.common.model.events.impl.EAdSceneElementTimedEventImpl;
import es.eucm.eadventure.common.model.extra.EAdList;
import es.eucm.eadventure.common.model.extra.impl.EAdListImpl;
import es.eucm.eadventure.common.model.guievents.impl.EAdMouseEventImpl;
import es.eucm.eadventure.common.model.variables.EAdField;
import es.eucm.eadventure.common.model.variables.EAdVarDef;
import es.eucm.eadventure.common.model.variables.impl.EAdFieldImpl;
import es.eucm.eadventure.common.model.variables.impl.EAdVarDefImpl;
import es.eucm.eadventure.common.model.variables.impl.operations.BooleanOperation;
import es.eucm.eadventure.common.model.variables.impl.operations.ListOperation;
import es.eucm.eadventure.common.model.variables.impl.operations.MathOperation;
import es.eucm.eadventure.common.params.fills.impl.EAdColor;
import es.eucm.eadventure.common.resources.assets.drawable.Drawable;
import es.eucm.eadventure.common.resources.assets.drawable.basics.impl.ImageImpl;
import es.eucm.eadventure.common.resources.assets.drawable.basics.impl.animation.Frame;
import es.eucm.eadventure.common.resources.assets.drawable.basics.impl.animation.FramesAnimation;

@SuppressWarnings("rawtypes")
public class MoleGame extends EmptyScene {

	private Drawable holeImage = new ImageImpl("@drawable/hole.png");

	private FramesAnimation mole;

	private EAdChangeFieldValueEffect dissapearMole;

	private EAdVarDef<EAdSceneElement> moleVar = new EAdVarDefImpl<EAdSceneElement>(
			"moleVar", EAdSceneElement.class, null);

	private EAdVarDef<EAdList> listVar;

	private EAdField<EAdList> listField;

	private EAdField<EAdSceneElement> moleField;

	public MoleGame() {
		setBackgroundFill(EAdColor.DARK_BROWN);
		moleField = new EAdFieldImpl<EAdSceneElement>(this, moleVar);

		dissapearMole = new EAdChangeFieldValueEffect();
		dissapearMole.setId("dissaperMole");
		dissapearMole.setParentVar(EAdBasicSceneElement.VAR_VISIBLE);
		dissapearMole.setOperation(BooleanOperation.FALSE_OP);

		mole = new FramesAnimation();
		mole.addFrame(new Frame("@drawable/mole1.png", 5000));
		mole.addFrame(new Frame("@drawable/mole2.png", 500));

		EAdList<EAdSceneElement> list = new EAdListImpl<EAdSceneElement>(
				EAdSceneElement.class);

		int row = 7;
		int col = row;
		int size = 60;
		float scale = 0.15f;
		for (int i = 0; i < col; i++)
			for (int j = 0; j < row; j++) {
				EAdSceneElement mole = getMole(size * i, size * j);
				mole.setVarInitialValue(EAdBasicSceneElement.VAR_SCALE, scale);
				getElements().add(mole);
				list.add(mole);
				EAdSceneElement hole = getHole(size * i, size * j + 25);
				hole.setVarInitialValue(EAdBasicSceneElement.VAR_SCALE, scale);
				this.getElements().add(hole);

			}
		listVar = new EAdVarDefImpl<EAdList>("moleListVar", EAdList.class, list);
		listField = new EAdFieldImpl<EAdList>(this, listVar);
		initLoop();

	}

	private EAdSceneElement getHole(int x, int y) {
		EAdBasicSceneElement hole = new EAdBasicSceneElement();
		hole.setId("hole" + x);
		hole.getResources().addAsset(hole.getInitialBundle(),
				EAdBasicSceneElement.appearance, holeImage);
		hole.setPosition(x, y);
		hole.addBehavior(EAdMouseEventImpl.MOUSE_LEFT_PRESSED, (EAdEffect) null);
		return hole;
	}

	private EAdSceneElement getMole(int x, int y) {
		EAdBasicSceneElement mole = new EAdBasicSceneElement();
		mole.setId("mole" + x + "" + y);
		mole.getResources().addAsset(mole.getInitialBundle(),
				EAdBasicSceneElement.appearance, this.mole);
		mole.setPosition(x, y + 30);

		// EAdSceneElementEventImpl event = new EAdSceneElementEventImpl();
		// EAdFieldImpl<Integer> yField = new EAdFieldImpl<Integer>(mole,
		// EAdBasicSceneElement.VAR_Y);
		// EAdInterpolationEffect interpolation = new
		// EAdInterpolationEffect(mole,
		// EAdBasicSceneElement.VAR_Y, new MathOperation("[0]", yField),
		// new MathOperation("[0] + 30", yField), 2000,
		// (int) Math.round(Math.random() * 1000), LoopType.REVERSE, -1,
		// InterpolationType.LINEAR);
		// event.addEffect(SceneElementEvent.ADDED_TO_SCENE, interpolation);

		int initTime = (int) Math.round(Math.random() * 5500);
		mole.setVarInitialValue(EAdBasicSceneElement.VAR_TIME_DISPLAYED,
				initTime);

		mole.addBehavior(EAdMouseEventImpl.MOUSE_LEFT_PRESSED, dissapearMole);
		// mole.getEvents().add(event);

		return mole;

	}

	public void initLoop() {
		EAdSceneElementTimedEventImpl event = new EAdSceneElementTimedEventImpl();
		event.setId("moleUp");

		EAdChangeFieldValueEffect effect = new EAdChangeFieldValueEffect();
		effect.setId("selectMole");
		effect.setOperation(new ListOperation(listField,
				ListOperation.Operation.RANDOM_ELEMENT));
		effect.addField(moleField);

		EAdInterpolationEffect interpolation = new EAdInterpolationEffect(
				moleField, EAdBasicSceneElement.VAR_Y, new MathOperation("0"),
				new MathOperation("-30"), 500, 0, InterpolationLoopType.REVERSE, 2,
				InterpolationType.LINEAR);
		event.addEffect(
				SceneElementTimedEventType.END_TIME,
				effect);
		event.addEffect(
				SceneElementTimedEventType.END_TIME,
				interpolation);
		event.setTime(1100);
		this.getBackground().getEvents().add(event);

	}

	@Override
	public String getSceneDescription() {
		return "Mole game";
	}

	public String getDemoName() {
		return "Mole game";
	}

}
