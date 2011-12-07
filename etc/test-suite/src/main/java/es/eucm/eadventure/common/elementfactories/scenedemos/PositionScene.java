package es.eucm.eadventure.common.elementfactories.scenedemos;

import es.eucm.eadventure.common.elementfactories.scenedemos.normalguy.NgCommon;
import es.eucm.eadventure.common.model.elements.impl.EAdBasicSceneElement;
import es.eucm.eadventure.common.model.elements.impl.EAdSceneElementDefImpl;
import es.eucm.eadventure.common.params.geom.impl.EAdPositionImpl;
import es.eucm.eadventure.common.params.geom.impl.EAdPositionImpl.Corner;
import es.eucm.eadventure.common.resources.assets.drawable.basics.impl.ImageImpl;

public class PositionScene extends EmptyScene {

	public PositionScene() {
		getBackground().getDefinition().getResources().addAsset(
				getBackground().getDefinition().getInitialBundle(),
				EAdSceneElementDefImpl.appearance,
				new ImageImpl("@drawable/centerbackground.png"));
		
		NgCommon.init();
		EAdBasicSceneElement e = new EAdBasicSceneElement( NgCommon.getMainCharacter());
		e.setScale(0.5f);
		e.setPosition(new EAdPositionImpl(Corner.BOTTOM_CENTER, 400, 300));
		
		this.getComponents().add(e);
	}

	@Override
	public String getSceneDescription() {
		return "A scene to tests coners in EAdPositionImpl";
	}

	public String getDemoName() {
		return "Positions Scene";
	}
}