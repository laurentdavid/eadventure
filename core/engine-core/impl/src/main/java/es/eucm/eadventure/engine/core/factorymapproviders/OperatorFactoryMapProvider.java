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

package es.eucm.eadventure.engine.core.factorymapproviders;

import java.util.HashMap;
import java.util.Map;

import es.eucm.eadventure.common.ReflectionProvider;
import es.eucm.eadventure.common.model.elements.variables.EAdField;
import es.eucm.eadventure.common.model.elements.variables.operations.BooleanOp;
import es.eucm.eadventure.common.model.elements.variables.operations.ConditionedOp;
import es.eucm.eadventure.common.model.elements.variables.operations.ListOp;
import es.eucm.eadventure.common.model.elements.variables.operations.MathOp;
import es.eucm.eadventure.common.model.elements.variables.operations.ValueOp;
import es.eucm.eadventure.engine.core.evaluators.EvaluatorFactory;
import es.eucm.eadventure.engine.core.game.ValueMap;
import es.eucm.eadventure.engine.core.operator.Operator;
import es.eucm.eadventure.engine.core.operator.OperatorFactory;
import es.eucm.eadventure.engine.core.operators.BooleanOperator;
import es.eucm.eadventure.engine.core.operators.ConditionedOperator;
import es.eucm.eadventure.engine.core.operators.FieldOperator;
import es.eucm.eadventure.engine.core.operators.ListOperator;
import es.eucm.eadventure.engine.core.operators.MathOperator;
import es.eucm.eadventure.engine.core.operators.ValueOperator;

public class OperatorFactoryMapProvider extends AbstractMapProvider<Class<?>, Operator<?>> {

	private static Map<Class<?>, Operator<?>> tempMap = new HashMap<Class<?>, Operator<?>>();
	
	private EvaluatorFactory evaluatorFactory;
	
	private ReflectionProvider reflectionProvider;
	
	private ValueMap valueMap;
	
	private OperatorFactory operatorFactory;
	
	public OperatorFactoryMapProvider(OperatorFactory operatorFactory, EvaluatorFactory evaluatorFactory, ValueMap valueMap,
			ReflectionProvider reflectionProvider) {
		super();
		this.valueMap = valueMap;
		this.evaluatorFactory = evaluatorFactory;
		this.reflectionProvider = reflectionProvider;
		this.operatorFactory = operatorFactory;
	}
	
	@Override
	public Map<Class<?>, Operator<?>> getMap() {
		factoryMap.put(MathOp.class, new MathOperator(valueMap));
		factoryMap.put(BooleanOp.class, new BooleanOperator(evaluatorFactory));
		factoryMap.put(ValueOp.class, new ValueOperator(reflectionProvider));
		factoryMap.put(EAdField.class, new FieldOperator(valueMap));
		factoryMap.put(ListOp.class, new ListOperator(valueMap));
		factoryMap.put(ConditionedOp.class, new ConditionedOperator(evaluatorFactory, operatorFactory));
		factoryMap.putAll(tempMap);
		return super.getMap();
	}
	
	public static void add(Class<?> operation, Operator<?> operator) {
		tempMap.put(operation, operator);
	}

	
}