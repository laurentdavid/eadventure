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

package es.eucm.eadventure.engine.core.gameobjects.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import es.eucm.eadventure.common.MapProvider;
import es.eucm.eadventure.common.interfaces.Element;
import es.eucm.eadventure.common.model.EAdElement;
import es.eucm.eadventure.engine.core.gameobjects.EffectGO;
import es.eucm.eadventure.engine.core.gameobjects.GameObject;
import es.eucm.eadventure.engine.core.gameobjects.GameObjectFactory;

/**
 * <p>
 * Default implementation of the {@link GameObjectFactor}.
 * </p>
 */
@Singleton
public class GameObjectFactoryImpl implements GameObjectFactory {

	private static final Logger logger = Logger
			.getLogger("GameObjectFactoryImpl");

	/**
	 * The map of instances of {@link GameObject}s for each {@link EAdElement}
	 * in the eAdventure game model
	 */
	Map<EAdElement, GameObject<?>> objectMap;

	/**
	 * A class map of the {@link EAdElement} in the eAdventure model and the
	 * {@link GameObject} classes in the engine runtime
	 */
	Map<Class<? extends EAdElement>, Class<? extends GameObject<?>>> classMap;

	/**
	 * The guice injector
	 */
	Injector injector;

	@Inject
	public GameObjectFactoryImpl(
			MapProvider<Class<? extends EAdElement>, Class<? extends GameObject<?>>> map,
			Injector injector) {
		this.classMap = map.getMap();
		this.injector = injector;
		this.objectMap = new HashMap<EAdElement, GameObject<?>>();
		logger.info("New instance");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.eucm.eadventure.engine.core.gameobjects.GameObjectFactory#get(es.eucm
	 * .eadventure.common.model.EAdElement)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T extends EAdElement> GameObject<?> get(T element) {
		GameObject<T> temp = (GameObject<T>) objectMap.get(element);

		if (temp != null)
			return temp;

		Class<? extends GameObject<?>> tempClass = classMap.get(element.getClass());
		if (tempClass == null) {
			Element annotation = element.getClass().getAnnotation(Element.class);
			if (annotation == null) {
				logger.log(Level.SEVERE, "No element annotation for class " + element.getClass());
				return null;
			}
			Class<?> runtimeClass = annotation.runtime();
			tempClass = classMap.get(runtimeClass);
		}
		if (tempClass == null) {
			logger.log(Level.SEVERE, "No game element mapped for class " + element.getClass());
		} else {
			temp = (GameObject<T>) injector.getInstance(tempClass);
			temp.setElement(element);
			
			//TODO temporary code, to allow effect running multiple times
			if (!(temp instanceof EffectGO))
				objectMap.put(element, temp);
		}
		return temp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.eucm.eadventure.engine.core.gameobjects.GameObjectFactory#get(java
	 * .lang.Class)
	 */
	@Override
	public <T extends GameObject<?>> T get(Class<T> gameObjectClass) {
		// TODO maybe use a cache
		return injector.getInstance(gameObjectClass);
	}

}
