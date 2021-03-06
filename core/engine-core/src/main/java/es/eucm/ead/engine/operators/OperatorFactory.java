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

package es.eucm.ead.engine.operators;

import es.eucm.ead.engine.factories.mapproviders.OperatorsMapProvider;
import es.eucm.ead.engine.game.GameState;
import es.eucm.ead.engine.operators.evaluators.EvaluatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.eucm.ead.model.elements.operations.EAdOperation;
import es.eucm.ead.tools.AbstractFactory;
import es.eucm.ead.tools.StringHandler;
import es.eucm.ead.tools.reflection.ReflectionProvider;

/**
 * A factory with all {@link Operator} for all {@link EAdOperation}. The Game
 * State is the only class that should access to this factory. If you're using
 * it somewhere else, try to use
 */
public class OperatorFactory extends AbstractFactory<Operator<?>> {

	static private Logger logger = LoggerFactory
			.getLogger(OperatorFactory.class);

	public OperatorFactory(ReflectionProvider interfacesProvider,
			GameState gameState, StringHandler sh) {
		super(null, interfacesProvider);
		setMap(new OperatorsMapProvider(this, gameState, new EvaluatorFactory(
				reflectionProvider, this), interfacesProvider, sh));
	}

	/**
	 * <p>
	 * Calculates the result of the given {@link EAdOperation} with the current
	 * values in the {@link es.eucm.ead.engine.game.interfaces.ValueMap}
	 * </p>
	 * The value should be stored in the {@link es.eucm.ead.engine.game.interfaces.ValueMap} by the actual
	 * {@link Operator}
	 *
	 * @param <T>
	 * @param eAdVar
	 *            the class for the result
	 * @param eAdOperation
	 *            operation to be done
	 * @return operation's result. If operation is {@code null}, a null is
	 *         returned.
	 */
	@SuppressWarnings("unchecked")
	public <T extends EAdOperation, S> S operate(Class<S> clazz, T operation) {
		if (operation == null) {
			logger
					.warn(
							"Null operation attempted: null returned as class {} for operation {}",
							new Object[] { clazz, operation });
			return null;
		}
		Operator<T> operator = (Operator<T>) get(operation.getClass());
		return operator.operate(clazz, operation);
	}

}
