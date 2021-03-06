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

package es.eucm.ead.model.elements.effects.physics;

import es.eucm.ead.model.interfaces.Element;
import es.eucm.ead.model.interfaces.Param;
import es.eucm.ead.model.elements.effects.AbstractEffect;
import es.eucm.ead.model.elements.effects.enums.PhShape;
import es.eucm.ead.model.elements.effects.enums.PhType;
import es.eucm.ead.model.elements.extra.EAdList;
import es.eucm.ead.model.elements.scenes.EAdSceneElement;
import es.eucm.ead.model.params.variables.EAdVarDef;
import es.eucm.ead.model.params.variables.VarDef;

@Element
public class PhysicsEf extends AbstractEffect {

	public static final EAdVarDef<PhType> VAR_PH_TYPE = new VarDef<PhType>(
			"ph_type", PhType.class, PhType.STATIC);

	public static final EAdVarDef<PhShape> VAR_PH_SHAPE = new VarDef<PhShape>(
			"ph_shape", PhShape.class, PhShape.RECTANGULAR);

	public static final EAdVarDef<Float> VAR_PH_FRICTION = new VarDef<Float>(
			"ph_friction", Float.class, 0.3f);

	public static final EAdVarDef<Float> VAR_PH_RESTITUTION = new VarDef<Float>(
			"ph_restitution", Float.class, 0.1f);

	public static final EAdVarDef<Float> VAR_PH_DENSITY = new VarDef<Float>(
			"ph_restitution", Float.class, 0.001f);

	/**
	 * Elements that are affect by the physics
	 */
	@Param
	private EAdList<EAdSceneElement> elements;

	@Param
	private EAdList<EAdSceneElement> joints;

	public PhysicsEf() {
		super();
		elements = new EAdList<EAdSceneElement>();
		joints = new EAdList<EAdSceneElement>();
	}

	public void addSceneElement(EAdSceneElement element) {
		this.elements.add(element);
	}

	public void addJoint(EAdSceneElement e1, EAdSceneElement e2) {
		joints.add(e1);
		joints.add(e2);
	}

	public EAdList<EAdSceneElement> getElements() {
		return elements;
	}

	public EAdList<EAdSceneElement> getJoints() {
		return joints;
	}

	public void setElements(EAdList<EAdSceneElement> elements) {
		this.elements = elements;
	}

	public void setJoints(EAdList<EAdSceneElement> joints) {
		this.joints = joints;
	}

}
