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

import es.eucm.ead.model.interfaces.Param;
import es.eucm.ead.model.elements.BasicElement;
import es.eucm.ead.model.elements.extra.EAdList;

public abstract class AbstractOperation extends BasicElement implements
		EAdOperation {

	/**
	 * List of the variables
	 */
	@Param
	protected EAdList<EAdOperation> operationsList;

	public AbstractOperation() {
		super();
		operationsList = new EAdList<EAdOperation>();
	}

	public EAdList<EAdOperation> getOperations() {
		return operationsList;
	}

	public void setOperationsList(EAdList<EAdOperation> operationsList) {
		this.operationsList = operationsList;
	}

	public void addOperation(EAdOperation operation) {
		this.operationsList.add(operation);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof AbstractOperation) {
			AbstractOperation ao = (AbstractOperation) o;
			if (ao.operationsList.size() == this.operationsList.size()) {
				for (int i = 0; i < ao.operationsList.size(); i++) {
					EAdOperation op1 = ao.operationsList.get(i);
					EAdOperation op2 = this.operationsList.get(i);
					if (!op1.equals(op2)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

}
