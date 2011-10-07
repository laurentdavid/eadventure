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

package es.eucm.eadventure.common.impl.importer.subimporters.conditions;

import com.google.inject.Inject;

import es.eucm.eadventure.common.EAdElementImporter;
import es.eucm.eadventure.common.data.chapter.conditions.Condition;
import es.eucm.eadventure.common.data.chapter.conditions.FlagCondition;
import es.eucm.eadventure.common.impl.importer.interfaces.EAdElementFactory;
import es.eucm.eadventure.common.model.conditions.impl.OperationCondition;
import es.eucm.eadventure.common.model.variables.EAdField;

public class FlagConditionImporter
		implements
		EAdElementImporter<FlagCondition, OperationCondition> {

	private EAdElementFactory factory;

	@Inject
	public FlagConditionImporter(EAdElementFactory factory) {
		this.factory = factory;
	}

	@SuppressWarnings("unchecked")
	public OperationCondition init(es.eucm.eadventure.common.data.chapter.conditions.FlagCondition oldObject) {
		EAdField<Boolean> var = (EAdField<Boolean>) factory.getVarByOldId(oldObject.getId(),
				Condition.FLAG_CONDITION);
		OperationCondition f = new OperationCondition(var);
		if (oldObject.isActiveState())
			f.setOp2(OperationCondition.TRUE);
		else
			f.setOp2(OperationCondition.FALSE);
		return f;
	}
	
	@Override
	public OperationCondition convert(FlagCondition oldObject, Object object) {
		return (OperationCondition) object;
	}

}
