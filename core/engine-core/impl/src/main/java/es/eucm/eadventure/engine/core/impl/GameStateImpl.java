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

package es.eucm.eadventure.engine.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import es.eucm.eadventure.common.model.effects.EAdEffect;
import es.eucm.eadventure.common.model.elements.EAdSceneElementDef;
import es.eucm.eadventure.common.model.elements.EAdChapter;
import es.eucm.eadventure.common.model.elements.EAdScene;
import es.eucm.eadventure.common.model.elements.EAdSceneElement;
import es.eucm.eadventure.common.model.elements.impl.EAdSceneImpl;
import es.eucm.eadventure.common.model.variables.EAdVarDef;
import es.eucm.eadventure.common.model.variables.impl.SystemFields;
import es.eucm.eadventure.engine.core.GameState;
import es.eucm.eadventure.engine.core.ValueMap;
import es.eucm.eadventure.engine.core.evaluators.EvaluatorFactory;
import es.eucm.eadventure.engine.core.gameobjects.EffectGO;
import es.eucm.eadventure.engine.core.gameobjects.GameObjectFactory;
import es.eucm.eadventure.engine.core.gameobjects.SceneGO;
import es.eucm.eadventure.engine.core.guiactions.GUIAction;

@Singleton
public class GameStateImpl implements GameState {

	private SceneGO<?> scene;

	private List<EffectGO<?>> effects;

	private ValueMap valueMap;

	private GameObjectFactory gameObjectFactory;

	private Stack<EAdScene> previousSceneStack;

	private List<EAdSceneElementDef> removedActors;

	private List<EAdSceneElementDef> inventoryActors;

	private EAdChapter currentChapter;

	/**
	 * Queue for effects added
	 */
	private List<EAdEffect> effectsQueue;

	/**
	 * Queue for the actions linked to effects
	 */
	private List<GUIAction> actionsQueue;
	
	/**
	 * Queue for the scene elements linked to effects
	 */
	private List<EAdSceneElement> sceneElementsQueue;

	private static Logger logger = Logger.getLogger("GameState");

	private boolean paused;

	private EAdScene loadingScreen;

	private EvaluatorFactory evaluatorFactory;

	@Inject
	public GameStateImpl(@Named("LoadingScreen") EAdScene loadingScreen,
			GameObjectFactory gameObjectFactory, ValueMap valueMap,
			EvaluatorFactory evaluatorFactory) {
		logger.log(Level.INFO, "New instance");
		effects = new ArrayList<EffectGO<?>>();
		// effectsQueue = Collections.synchronizedList(new
		// ArrayList<EAdEffect>());
		effectsQueue = new ArrayList<EAdEffect>();
		actionsQueue = new ArrayList<GUIAction>();
		sceneElementsQueue = new ArrayList<EAdSceneElement>();
		this.loadingScreen = loadingScreen;
		this.valueMap = valueMap;
		this.gameObjectFactory = gameObjectFactory;
		this.previousSceneStack = new Stack<EAdScene>();
		this.evaluatorFactory = evaluatorFactory;
		removedActors = new ArrayList<EAdSceneElementDef>();
		inventoryActors = new ArrayList<EAdSceneElementDef>();
	}

	public SceneGO<?> getScene() {
		if (scene == null) {
			logger.log(Level.FINE, "null scene, Loading screen: "
					+ (loadingScreen != null));
			this.scene = (SceneGO<?>) gameObjectFactory.get(loadingScreen);
			previousSceneStack.push(loadingScreen);
		}
		return scene;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.eucm.eadventure.engine.core.GameState#setScene(es.eucm.eadventure.
	 * engine.core.gameobjects.SceneGO)
	 */
	@Override
	public void setScene(SceneGO<? extends EAdScene> newScene) {
		// When a scene changes, all current effects are erased
		for (EffectGO<?> go : effects) {
			go.finish();
		}
		effects.clear();
		// Clean cache
		gameObjectFactory.clean();
		if (this.scene != null && this.scene.getElement() != null) {
			valueMap.setValue(scene.getElement(),
					EAdSceneImpl.VAR_SCENE_LOADED, Boolean.FALSE);
			if (scene.getElement().isReturnable())
				previousSceneStack.push(scene.getElement());
		}
		this.scene = newScene;
		if (this.scene != null && this.scene.getElement() != null) {
			valueMap.setValue(scene.getElement(),
					EAdSceneImpl.VAR_SCENE_LOADED, Boolean.TRUE);
			for (Entry<EAdVarDef<?>, Object> e : scene.getElement().getVars()
					.entrySet()) {
				valueMap.setValue(e.getKey(), e.getValue(), scene.getElement());
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.eucm.eadventure.engine.core.GameState#getEffects()
	 */
	@Override
	public List<EffectGO<?>> getEffects() {
		return effects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.eucm.eadventure.engine.core.GameState#getValueMap()
	 */
	@Override
	public ValueMap getValueMap() {
		return valueMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.eucm.eadventure.engine.core.GameState#addEffect(es.eucm.eadventure
	 * .common.model.effects.EAdEffect)
	 */
	@Override
	public void addEffect(EAdEffect e, GUIAction action, EAdSceneElement parent) {
		addEffect(-1, e, action, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.eucm.eadventure.engine.core.GameState#addEffect(int,
	 * es.eucm.eadventure.common.model.effects.EAdEffect)
	 */
	@Override
	// TODO consider leaving effect initilization for later
	public void addEffect(int pos, EAdEffect e, GUIAction action, EAdSceneElement parent) {
		if (e != null && evaluatorFactory.evaluate(e.getCondition())) {
			if (e.isQueueable())
				synchronized (effectsQueue) {
					pos = pos == -1 ? effectsQueue.size() : pos;
					effectsQueue.add(pos, e);
					actionsQueue.add(action);
					sceneElementsQueue.add(parent);
				}
			else {
				EffectGO<?> effectGO = (EffectGO<?>) gameObjectFactory.get(e);
				effectGO.setGUIAction(action);
				effectGO.setParent(parent);
				effectGO.initilize();
				effectGO.update();
				effectGO.finish();
			}
		}

	}

	@Override
	public EAdScene getPreviousScene() {
		return previousSceneStack.pop();
	}

	@Override
	public List<EAdSceneElementDef> getRemovedActors() {
		return removedActors;
	}

	@Override
	public List<EAdSceneElementDef> getInventoryActors() {
		return inventoryActors;
	}

	@Override
	public EAdChapter getCurrentChapter() {
		return currentChapter;
	}

	@Override
	public void setCurrentChapter(EAdChapter currentChapter) {
		this.currentChapter = currentChapter;
	}

	@Override
	public boolean isPaused() {
		return paused;
	}

	@Override
	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateEffectsQueue() {
		int i = 0;
		synchronized (effectsQueue) {
			for (EAdEffect e : effectsQueue) {
				@SuppressWarnings("rawtypes")
				EffectGO effectGO = (EffectGO) gameObjectFactory.get(e);
				if (effectGO != null) {
					effectGO.setParent(sceneElementsQueue.get(i));
					effectGO.setGUIAction(actionsQueue.get(i));
					if (!effects.contains(effectGO)) {
						effectGO.setElement(e);
						effects.add(effectGO);
					}
					logger.info("Added " + effectGO);
				} else
					logger.severe("Null EffectGO for " + e);
				i++;
			}
			effectsQueue.clear();
			actionsQueue.clear();
			sceneElementsQueue.clear();
		}

	}

	@Override
	public void addEffect(EAdEffect e) {
		this.addEffect(e, null, null);
	}

	@Override
	public EAdSceneElement getActiveElement() {
		return valueMap.getValue(SystemFields.ACTIVE_ELEMENT);
	}

	@Override
	public void setActiveElement(EAdSceneElement activeElement) {
		valueMap.setValue(SystemFields.ACTIVE_ELEMENT, activeElement);
	}

	@Override
	public void setInitialScene(EAdScene initialScene) {
		((LoadingScreen) loadingScreen).setInitialScreen(initialScene);
	}

}
