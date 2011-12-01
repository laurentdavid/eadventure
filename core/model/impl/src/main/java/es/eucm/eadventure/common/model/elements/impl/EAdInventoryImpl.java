package es.eucm.eadventure.common.model.elements.impl;

import es.eucm.eadventure.common.interfaces.Element;
import es.eucm.eadventure.common.interfaces.Param;
import es.eucm.eadventure.common.model.elements.EAdInventory;
import es.eucm.eadventure.common.model.elements.EAdSceneElementDef;
import es.eucm.eadventure.common.model.extra.EAdList;
import es.eucm.eadventure.common.model.extra.impl.EAdListImpl;
import es.eucm.eadventure.common.model.impl.EAdElementImpl;

@Element(detailed = EAdInventoryImpl.class, runtime = EAdInventoryImpl.class )
public class EAdInventoryImpl extends EAdElementImpl implements EAdInventory {
	
	@Param("initialItems")
	private EAdList<EAdSceneElementDef> initialItems;
	
	public EAdInventoryImpl( ){
		this.setId("inventory");
		initialItems = new EAdListImpl<EAdSceneElementDef>(EAdSceneElementDef.class);
	}

	@Override
	public EAdList<EAdSceneElementDef> getInitialItems() {
		return initialItems;
	}

}
