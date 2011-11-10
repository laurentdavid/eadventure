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

package es.eucm.eadventure.common.model.effects.impl;

import es.eucm.eadventure.common.interfaces.Element;
import es.eucm.eadventure.common.interfaces.Param;
import es.eucm.eadventure.common.model.EAdElement;
import es.eucm.eadventure.common.model.variables.EAdField;
import es.eucm.eadventure.common.model.variables.EAdVarDef;
import es.eucm.eadventure.common.model.variables.impl.EAdFieldImpl;
import es.eucm.eadventure.common.model.variables.impl.operations.MathOperation;

/**
 * Effect that performs an interpolation between two values in an {@link EAdVar}
 * 
 * 
 */
@Element(detailed = EAdInterpolationEffect.class, runtime = EAdInterpolationEffect.class)
public class EAdInterpolationEffect extends AbstractEAdEffect {

	/**
	 * Loops types
	 * 
	 */
	public enum LoopType {
		/**
		 * No loop
		 */
		NO_LOOP,

		/**
		 * When interpolations ends, goes backwards
		 */
		REVERSE,

		/**
		 * When interpolation ends, it restarts
		 */
		RESTART
	};

	public enum InterpolationType {
		/**
		 * Linear interpolation
		 */
		LINEAR,

		/**
		 * Bounces in the end
		 */
		BOUNCE_END, ACCELERATE, DESACCELERATE;

	}

	@Param("element")
	private EAdElement element;

	@Param("var")
	private EAdVarDef<?> varDef;

	@Param("initialValue")
	private MathOperation initialValue;

	@Param("endValue")
	private MathOperation endValue;

	@Param("time")
	private int interpolationTime;

	@Param("delay")
	private int delay;

	@Param("loopType")
	private LoopType loopType;

	@Param("loops")
	private int loops;

	@Param("interpolation")
	private InterpolationType interpolationType;

	/**
	 * Private constructor
	 * 
	 * @param id
	 *            effect's id
	 * @param elementField
	 *            field containing the element holding the variable
	 * @param element
	 *            the element holding the variable
	 * @param varDef
	 *            the variable definition
	 * @param initialValue
	 *            the initial value for the interpolation
	 * @param endValue
	 *            the end value for the interpolation
	 * @param interpolationTime
	 *            the time for the interpolation
	 * @param delay
	 *            the delay until the interpolation begins
	 * @param loopType
	 *            the loop type
	 * @param loops
	 *            the number of loops. If loops < 0, is considered to be
	 *            infinite
	 * @param interpolationType
	 *            the interpolation type
	 */
	private EAdInterpolationEffect(String id, EAdElement element,
			EAdVarDef<?> varDef, MathOperation initialValue,
			MathOperation endValue, int interpolationTime, int delay,
			LoopType loopType, int loops, InterpolationType interpolationType) {
		super(id);
		this.element = element;
		this.varDef = varDef;
		this.initialValue = initialValue;
		this.endValue = endValue;
		this.interpolationTime = interpolationTime;
		this.delay = delay;
		this.loopType = loopType;
		this.loops = loops;
		this.interpolationType = interpolationType;
		setQueueable(true);
	}

	public EAdInterpolationEffect(EAdElement element, EAdVarDef<?> varDef,
			MathOperation initialValue, MathOperation endValue,
			int interpolationTime, int delay, LoopType loopType, int loops,
			InterpolationType interpolationType) {
		this("interpolation", element, varDef, initialValue, endValue,
				interpolationTime, delay, loopType, loops, interpolationType);
	}

	public EAdInterpolationEffect(EAdElement element, EAdVarDef<?> varDef,
			Number initialValue, Number endValue, int interpolationTime,
			int delay, LoopType loopType, int loops,
			InterpolationType interpolationType) {
		this("interpolation", element, varDef, new MathOperation(initialValue
				+ ""), new MathOperation(endValue + ""), interpolationTime,
				delay, loopType, loops, interpolationType);
	}

	public EAdInterpolationEffect(EAdElement element, EAdVarDef<?> varDef,
			Number initialValue, Number endValue, int interpolationTime) {
		this("interpolation", element, varDef, new MathOperation(initialValue
				+ ""), new MathOperation(endValue + ""), interpolationTime, 0,
				LoopType.NO_LOOP, 1, InterpolationType.LINEAR);
	}

	public EAdInterpolationEffect(String id) {
		super(id);
	}

	public EAdInterpolationEffect(EAdField<?> field, float startValue,
			float endValue, int time, LoopType loopType,
			InterpolationType interpolationType) {
		this(field.getElement(), field.getVarDefinition(), startValue,
				endValue, time, 0, loopType, -1, interpolationType);
	}

	public EAdInterpolationEffect(EAdFieldImpl<?> field, float start,
			float endValue, int time, LoopType loopType) {
		this(field, start, endValue, time, loopType, InterpolationType.LINEAR);
	}

	public EAdInterpolationEffect(EAdFieldImpl<?> field, int start,
			int end, int timeToFinish, LoopType loopType) {
		this(field, (float) start, (float) end, timeToFinish, loopType);
	}

	public EAdInterpolationEffect(EAdField<Integer> field,
			MathOperation start, MathOperation end, int time,
			LoopType loopType, InterpolationType interpolation) {
		this( field.getElement(), field.getVarDefinition(), start, end, time, 0, loopType, -1, interpolation);
	}

	public EAdElement getElement() {
		return element;
	}

	public void setElement(EAdElement element) {
		this.element = element;
	}

	public EAdVarDef<?> getVarDef() {
		return varDef;
	}

	public void setVarDef(EAdVarDef<?> varDef) {
		this.varDef = varDef;
	}

	public MathOperation getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(MathOperation initialValue) {
		this.initialValue = initialValue;
	}

	public MathOperation getEndValue() {
		return endValue;
	}

	public void setEndValue(MathOperation endValue) {
		this.endValue = endValue;
	}

	public int getInterpolationTime() {
		return interpolationTime;
	}

	public void setInterpolationTime(int interpolationTime) {
		this.interpolationTime = interpolationTime;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public LoopType getLoopType() {
		return loopType;
	}

	public void setLoopType(LoopType loopType) {
		this.loopType = loopType;
	}

	public int getLoops() {
		return loops;
	}

	public void setLoops(int loops) {
		this.loops = loops;
	}

	public InterpolationType getInterpolationType() {
		return interpolationType;
	}

	public void setInterpolationType(InterpolationType interpolationType) {
		this.interpolationType = interpolationType;
	}

}