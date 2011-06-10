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

package es.eucm.eadventure.common.model.effects.impl.text.extra;

import es.eucm.eadventure.common.interfaces.Element;
import es.eucm.eadventure.common.interfaces.Param;
import es.eucm.eadventure.common.model.behaviors.impl.EAdBehaviorImpl;
import es.eucm.eadventure.common.model.effects.EAdEffect;
import es.eucm.eadventure.common.model.effects.EAdMacro;
import es.eucm.eadventure.common.model.effects.impl.EAdMacroImpl;
import es.eucm.eadventure.common.model.effects.impl.EAdTriggerMacro;
import es.eucm.eadventure.common.model.effects.impl.variables.EAdChangeVarValueEffect;
import es.eucm.eadventure.common.model.elements.impl.EAdBasicSceneElement;
import es.eucm.eadventure.common.model.guievents.impl.EAdMouseEventImpl;
import es.eucm.eadventure.common.model.variables.impl.operations.LiteralExpressionOperation;
import es.eucm.eadventure.common.model.variables.impl.vars.IntegerVar;

/**
 * <p>
 * Answer element for the {@link EAdShowQuestion} effect
 * </p>
 */
@Element(runtime = EAdBasicSceneElement.class, detailed = Answer.class)
public class Answer extends EAdBasicSceneElement {

	@Param("macro")
	private EAdMacro macro;
	
	public Answer(String id) {
		super(id);
		macro = new EAdMacroImpl(id + "_macro");
	}

	/**
	 * Returns the effects triggered by this answer
	 * 
	 * @return the effects triggered by this answer
	 */
	public EAdMacro getMacro() {
		return macro;
	}
	
	public void setMacro(EAdMacro macro) {
		this.macro = macro;
	}
	
	public void setUpNewInstance(IntegerVar selectedAnswer, EAdEffect endEffect, int index) {
		behavior = new EAdBehaviorImpl(id + "_answer" + index + "Behavior");
		
		EAdChangeVarValueEffect selectEffect = new EAdChangeVarValueEffect("setSelected");
		selectEffect.setVar(selectedAnswer);
		selectEffect.setOperation(new LiteralExpressionOperation("exp", "" + index));
		behavior.addBehavior(EAdMouseEventImpl.MOUSE_ENTERED, selectEffect);
		
		EAdChangeVarValueEffect unselectEffect = new EAdChangeVarValueEffect("setUnselected");
		unselectEffect.setVar(selectedAnswer);
		unselectEffect.setOperation(new LiteralExpressionOperation("exp", "-1"));
		behavior.addBehavior(EAdMouseEventImpl.MOUSE_EXITED, unselectEffect);
		
		
		EAdTriggerMacro triggerMacro = new EAdTriggerMacro( id + "_triggerMacro");
		triggerMacro.setMacro(macro);
		behavior.addBehavior(EAdMouseEventImpl.MOUSE_LEFT_CLICK, endEffect);
		behavior.addBehavior(EAdMouseEventImpl.MOUSE_RIGHT_CLICK, endEffect);
		behavior.addBehavior(EAdMouseEventImpl.MOUSE_LEFT_CLICK, triggerMacro);
		behavior.addBehavior(EAdMouseEventImpl.MOUSE_RIGHT_CLICK, triggerMacro);
		
		//TODO Appearance change for selected/not selected
	}
	
	
	
}
