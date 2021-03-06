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

package es.eucm.ead.engine.game;

import aurelienribon.tweenengine.TweenAccessor;
import es.eucm.ead.model.elements.operations.EAdField;
import es.eucm.ead.model.interfaces.features.Identified;
import es.eucm.ead.model.interfaces.features.Variabled;
import es.eucm.ead.model.params.variables.EAdVarDef;
import es.eucm.ead.tools.StringHandler;
import es.eucm.ead.tools.reflection.ReflectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ValueMap implements TweenAccessor<EAdField<?>> {

	static private Logger logger = LoggerFactory.getLogger(ValueMap.class);

	/**
	 * Reflection provider
	 */
	protected ReflectionProvider reflectionProvider;

	/**
	 * String handler
	 */
	protected StringHandler stringHandler;

	/**
	 * Contains all variables values
	 */
	protected Map<String, Map<EAdVarDef<?>, Object>> valuesMap;

	/**
	 * Contains the elements with variables updated
	 */
	protected ArrayList<Object> updateList;

	/**
	 * If the update list is enable
	 */
	private boolean updateEnable;

	public ValueMap(ReflectionProvider reflectionProvider,
			StringHandler stringHandler) {
		this.stringHandler = stringHandler;
		this.reflectionProvider = reflectionProvider;
		valuesMap = new HashMap<String, Map<EAdVarDef<?>, Object>>();
		logger.info("New instance");
		updateList = new ArrayList<Object>();
		updateEnable = true;
	}

	/**
	 * Sets the field to given value
	 *
	 * @param field the field
	 * @param value the value to the field
	 */
	public <S> void setValue(EAdField<S> field, S value) {
		setValue(field.getElement(), field.getVarDef(), value);
	}

	@SuppressWarnings("all")
	/**
	 * Sets the value a variable in a element
	 *
	 * @param element
	 *            the element
	 * @param varDef
	 *            the var definition
	 * @param value
	 */
	public <S> void setValue(Identified element, EAdVarDef<S> varDef, S value) {
		if (value == null
				|| reflectionProvider.isAssignableFrom(varDef.getType(), value
						.getClass())) {

			Map<EAdVarDef<?>, Object> valMap = valuesMap
					.get(element == null ? null : maybeDecodeField(element)
							.getId());
			if (valMap == null) {
				valMap = new HashMap<EAdVarDef<?>, Object>();
				valuesMap.put(element == null ? null : element.getId(), valMap);

				// Sets initial values, if any
				addInitVariables(element, valMap);
			}

			valMap.put(varDef, (S) value);
			if (updateEnable)
				/**
				 * If the value map contains values for this element
				 *
				 * @param element
				 * @return
				 */
				addUpdatedElement(element);

		} else {
			logger.warn("setValue failed: Impossible to cast "
					+ varDef.getType() + " to " + value.getClass()
					+ " for element " + element + " of class "
					+ element.getClass());
		}
	}

	private void addInitVariables(Object o, Map<EAdVarDef<?>, Object> initVars) {
		if (o instanceof Variabled) {
			initVars.putAll(((Variabled) o).getVars());
		}
	}

	/**
	 * Returns the value of the field
	 *
	 * @param <S>   field type
	 * @param field the field to be consulted
	 * @return the value of the field
	 */
	public <S> S getValue(EAdField<S> field) {
		return getValue(field.getElement(), field.getVarDef());
	}

	@SuppressWarnings("unchecked")
	/**
	 * Returns the value of the variable in the given element
	 *
	 *
	 *
	 * @param element
	 *            the element. If the element is {@code null}, is considered as
	 *            a system variable
	 * @param varDef
	 *            the variable definition to be consulted
	 * @return the variable's value
	 */
	public <S> S getValue(Identified element, EAdVarDef<S> varDef) {
		Map<EAdVarDef<?>, Object> valMap = valuesMap.get(element == null ? null
				: maybeDecodeField(element).getId());

		if (valMap == null) {
			valMap = new HashMap<EAdVarDef<?>, Object>();
			valuesMap.put(element == null ? null : element.getId(), valMap);

			// Sets initial values, if any
			addInitVariables(element, valMap);
		}
		Object value = valMap.get(varDef);
		// If the variable has not been set, returns the initial value

		// reflectionProvider.isAssignableFrom is not used, because types are
		// checked in setValue
		return value == null ? varDef.getInitialValue() : (S) value;

	}

	/**
	 * Returns the variables associated to an element, whose values are
	 * different from the defaults
	 *
	 * @param element the element. If the element is null, it returns system vars
	 * @return a map with the variables
	 */
	public Map<EAdVarDef<?>, Object> getElementVars(Identified element) {
		return valuesMap.get(maybeDecodeField(element).getId());
	}

	/**
	 * Returns the final element associated to the given element. It could be
	 * the element itself, but if the element is a field (with type
	 * {@link es.eucm.ead.model.elements.EAdElement}), the element pointed by the field will be returned,
	 *
	 * @param element the element
	 * @return the final element pointed by the element
	 */
	public Identified maybeDecodeField(Identified element) {
		if (element != null && element instanceof EAdField<?>) {
			EAdField<?> field = (EAdField<?>) element;
			Object result = getValue(field.getElement(), field.getVarDef());
			return maybeDecodeField((Identified) result);
		}
		return element;
	}

	/**
	 * Checks if the value map contains updated variables' values for the given
	 * element. If it does, true is returned, and the element checking for
	 * updates should read the variables he is interested in. The element is
	 * deleted for the update list of the value map until another of its fields
	 * is updated
	 *
	 * @param element the element
	 * @return if any element's field has been updated since last check
	 */
	public boolean checkForUpdates(Object element) {
		if (updateList.contains(element)) {
			updateList.remove(element);
			return true;
		}
		return false;
	}

	/**
	 * Adds an element to the update list
	 *
	 * @param element the element to add
	 */
	private void addUpdatedElement(Object element) {
		if (element != null && !updateList.contains(element)) {
			updateList.add(element);
		}
	}

	/**
	 * Sets if the updates list is enable and it is recording all fields changes
	 *
	 * @param enable if it's enable or not
	 */
	public void setUpdateListEnable(boolean enable) {
		updateEnable = enable;
	}

	/**
	 * Removes all fields associated to the given element
	 *
	 * @param element the element
	 */
	public void remove(Identified element) {
		valuesMap.remove(maybeDecodeField(element).getId());
	}

	/**
	 * @param element the element to check
	 * @return If the value map contains values for this element
	 */
	public boolean contains(Identified element) {
		return valuesMap.get(element.getId()) != null;
	}

	@Override
	public int getValues(EAdField<?> eAdField, int i, float[] floats) {
		return 0;
	}

	@Override
	public void setValues(EAdField<?> eAdField, int i, float[] floats) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
