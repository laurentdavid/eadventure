package es.eucm.eadventure.common.model.impl;

import com.gwtent.reflection.client.Reflectable;

import es.eucm.eadventure.common.model.EAdElement;

@Reflectable
public abstract class EAdElementImpl implements EAdElement {

	private static int ID_GENERATOR = 0;
	
	protected String id;

	public EAdElementImpl() {
		this.id = "EAdElement" + ID_GENERATOR++
				+ Math.round(Math.random() * 1000);
	}

	public EAdElementImpl(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public EAdElement copy() {
		// To implement in inherit classes
		return null;
	}

	@Override
	public EAdElement copy(boolean deepCopy) {
		// To implement in inherit classes
		return null;
	}
	
	public String toString(){
		return id;
	}

}
