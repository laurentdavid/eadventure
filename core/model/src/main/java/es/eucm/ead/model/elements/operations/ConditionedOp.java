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

package es.eucm.ead.model.elements.operations;

import es.eucm.ead.model.interfaces.Element;
import es.eucm.ead.model.interfaces.Param;
import es.eucm.ead.model.elements.EAdCondition;

@Element
public class ConditionedOp extends AbstractOperation {

	@Param
	private es.eucm.ead.model.elements.EAdCondition condition;

	@Param
	private EAdOperation opTrue;

	@Param
	private EAdOperation opFalse;

	public ConditionedOp() {

	}

	public ConditionedOp(EAdCondition c, EAdOperation opTrue,
			EAdOperation opFalse) {
		super();
		this.opTrue = opTrue;
		this.opFalse = opFalse;
		this.condition = c;
	}

	public EAdCondition getCondition() {
		return condition;
	}

	public EAdOperation getOpTrue() {
		return opTrue;
	}

	public EAdOperation getOpFalse() {
		return opFalse;
	}

	public void setCondition(EAdCondition condition) {
		this.condition = condition;
	}

	public void setOpTrue(EAdOperation opTrue) {
		this.opTrue = opTrue;
	}

	public void setOpFalse(EAdOperation opFalse) {
		this.opFalse = opFalse;
	}

	public String toString() {
		return condition + "?" + opTrue + ":" + opFalse;
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof ConditionedOp) {
			ConditionedOp op = (ConditionedOp) o;
			return op.condition.equals(condition) && op.opTrue.equals(opTrue)
					&& op.opFalse.equals(opFalse);
		}
		return false;
	}

}
