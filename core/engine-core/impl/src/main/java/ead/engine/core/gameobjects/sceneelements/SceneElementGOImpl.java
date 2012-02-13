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

package ead.engine.core.gameobjects.sceneelements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.google.inject.Inject;

import ead.common.interfaces.features.enums.Orientation;
import ead.common.model.elements.EAdAction;
import ead.common.model.elements.EAdEvent;
import ead.common.model.elements.ResourcedElementImpl;
import ead.common.model.elements.extra.EAdList;
import ead.common.model.elements.scene.EAdSceneElement;
import ead.common.model.elements.scenes.SceneElementDefImpl;
import ead.common.model.elements.scenes.SceneElementImpl;
import ead.common.model.elements.variables.EAdVarDef;
import ead.common.params.fills.EAdPaintImpl;
import ead.common.resources.EAdBundleId;
import ead.common.resources.StringHandler;
import ead.common.resources.assets.AssetDescriptor;
import ead.common.resources.assets.drawable.Drawable;
import ead.common.resources.assets.drawable.basics.animation.FramesAnimation;
import ead.common.resources.assets.drawable.compounds.OrientedDrawable;
import ead.common.resources.assets.drawable.compounds.StateDrawable;
import ead.common.resources.assets.drawable.filters.FilteredDrawable;
import ead.common.resources.assets.drawable.filters.FilteredDrawableImpl;
import ead.common.util.EAdPosition;
import ead.engine.core.game.GameLoop;
import ead.engine.core.game.GameState;
import ead.engine.core.game.ValueMap;
import ead.engine.core.gameobjects.DrawableGameObjectImpl;
import ead.engine.core.gameobjects.factories.EventGOFactory;
import ead.engine.core.gameobjects.factories.SceneElementGOFactory;
import ead.engine.core.gameobjects.go.EventGO;
import ead.engine.core.gameobjects.go.SceneElementGO;
import ead.engine.core.gameobjects.huds.ActionSceneElement;
import ead.engine.core.input.InputAction;
import ead.engine.core.platform.AssetHandler;
import ead.engine.core.platform.DrawableAsset;
import ead.engine.core.platform.GUI;
import ead.engine.core.platform.rendering.GenericCanvas;
import ead.engine.core.util.EAdTransformation;

public abstract class SceneElementGOImpl<T extends EAdSceneElement> extends
		DrawableGameObjectImpl<T> implements SceneElementGO<T> {

	private static final Logger logger = Logger.getLogger("SceneElementGOImpl");

	private EventGOFactory eventFactory;

	protected EAdPosition position;

	protected float scale;
	
	protected float scaleX;
	
	protected float scaleY;

	protected Orientation orientation;

	protected String state;

	protected float rotation;

	private int width;

	private int height;

	private int timeDisplayed;

	protected float alpha;

	protected boolean visible;

	private ArrayList<EventGO<?>> eventGOList;

	@Inject
	public SceneElementGOImpl(AssetHandler assetHandler,
			StringHandler stringHandler,
			SceneElementGOFactory gameObjectFactory, GUI gui,
			GameState gameState, EventGOFactory eventFactory) {
		super(assetHandler, stringHandler, gameObjectFactory, gui, gameState);
		logger.info("New instance");
		eventGOList = new ArrayList<EventGO<?>>();
		this.eventFactory = eventFactory;
	}

	@Override
	public abstract boolean processAction(InputAction<?> action);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.eucm.eadventure.engine.core.gameobjects.impl.AbstractGameObject#setElement
	 * (es.eucm.eadventure.common.model.EAdElement)
	 * 
	 * Should be implemented to get position, scale, orientation and other
	 * values
	 */
	@Override
	public void setElement(T element) {
		super.setElement(element);

		gameState.getValueMap().remove(element);

		gameState.getValueMap().setValue(element,
				ResourcedElementImpl.VAR_BUNDLE_ID,
				element.getDefinition().getInitialBundle());
		gameState.getValueMap().setValue(element.getDefinition(),
				SceneElementDefImpl.VAR_SCENE_ELEMENT, element);

		for (Entry<EAdVarDef<?>, Object> entry : element.getVars().entrySet()) {
			gameState.getValueMap().setValue(element, entry.getKey(),
					entry.getValue());
		}

		// Scene element events
		if (element.getEvents() != null) {
			for (EAdEvent event : element.getEvents()) {
				EventGO<?> eventGO = eventFactory.get(event);
				eventGO.setParent(element);
				eventGO.initialize();
				eventGOList.add(eventGO);
			}
		}

		// Definition events
		if (element.getEvents() != null) {
			for (EAdEvent event : element.getDefinition().getEvents()) {
				EventGO<?> eventGO = eventFactory.get(event);
				eventGO.setParent(element);
				eventGO.initialize();
				eventGOList.add(eventGO);
			}

		}

		position = new EAdPosition(0, 0);
		updateVars();
		setVars();
		// To load dimensions
		getRenderAsset();
	}

	/**
	 * Read vars values
	 */
	protected void updateVars() {
		ValueMap valueMap = gameState.getValueMap();
		enable = valueMap.getValue(element, SceneElementImpl.VAR_ENABLE);
		visible = valueMap.getValue(element, SceneElementImpl.VAR_VISIBLE);
		rotation = valueMap.getValue(element, SceneElementImpl.VAR_ROTATION);
		scale = valueMap.getValue(element, SceneElementImpl.VAR_SCALE);
		scaleX = valueMap.getValue(element, SceneElementImpl.VAR_SCALE_X);
		scaleY = valueMap.getValue(element, SceneElementImpl.VAR_SCALE_Y);
		alpha = valueMap.getValue(element, SceneElementImpl.VAR_ALPHA);
		orientation = valueMap.getValue(element,
				SceneElementImpl.VAR_ORIENTATION);
		state = valueMap.getValue(element, SceneElementImpl.VAR_STATE);
		timeDisplayed = valueMap.getValue(element,
				SceneElementImpl.VAR_TIME_DISPLAYED);
		position.setX(valueMap.getValue(element, SceneElementImpl.VAR_X));
		position.setY(valueMap.getValue(element, SceneElementImpl.VAR_Y));
		position.setDispX(valueMap.getValue(element,
				SceneElementImpl.VAR_DISP_X));
		position.setDispY(valueMap.getValue(element,
				SceneElementImpl.VAR_DISP_Y));

		updateTransformation();
	}

	/**
	 * Sets some variables
	 */
	protected void setVars() {
		int scaleW = (int) (width * scale * scaleX);
		int scaleH = (int) (height * scale * scaleY);
		int x = position.getJavaX(scaleW);
		int y = position.getJavaY(scaleH);
		gameState.getValueMap().setValue(element, SceneElementImpl.VAR_LEFT, x);
		gameState.getValueMap().setValue(element, SceneElementImpl.VAR_RIGHT,
				x + scaleW);
		gameState.getValueMap().setValue(element, SceneElementImpl.VAR_TOP, y);
		gameState.getValueMap().setValue(element, SceneElementImpl.VAR_BOTTOM,
				y + scaleH);
		gameState.getValueMap().setValue(element,
				SceneElementImpl.VAR_CENTER_X, x + scaleW / 2);
		gameState.getValueMap().setValue(element,
				SceneElementImpl.VAR_CENTER_Y, y + scaleH / 2);

	}

	protected void updateTransformation() {
		transformation.setAlpha(alpha);
		transformation.setVisible(visible);
		transformation.getMatrix().setIdentity();
		int x = position.getJavaX(width);
		int y = position.getJavaY(height);
		transformation.getMatrix().translate(x, y, true);
		int deltaX = position.getX() - x;
		int deltaY = position.getY() - y;

		transformation.getMatrix().translate(deltaX, deltaY, true);
		transformation.getMatrix().rotate(rotation, true);
		transformation.getMatrix().scale(scale * scaleX, scale * scaleY, true);
		transformation.getMatrix().translate(-deltaX, -deltaY, true);

	}

	@Override
	public SceneElementGO<?> getDraggableElement() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.eucm.eadventure.engine.core.gameobjects.impl.AbstractGameObject#update
	 * (es.eucm.eadventure.engine.core.GameState)
	 * 
	 * Should update the state of all sub-elements and resources
	 */
	@Override
	public void update() {

		if (eventGOList != null)
			for (EventGO<?> eventGO : eventGOList)
				eventGO.update();

		gameState.getValueMap().setValue(element,
				SceneElementImpl.VAR_TIME_DISPLAYED,
				timeDisplayed + GameLoop.SKIP_MILLIS_TICK);

		if (getAsset() != null)
			getAsset().update();

		updateVars();
		setVars();
	}

	@Override
	public T getElement() {
		return super.getElement();
	}

	@Override
	public void setPosition(EAdPosition position) {
		ValueMap valueMap = gameState.getValueMap();
		valueMap.setValue(element, SceneElementImpl.VAR_X, position.getX());
		valueMap.setValue(element, SceneElementImpl.VAR_Y, position.getY());
		valueMap.setValue(element, SceneElementImpl.VAR_DISP_X,
				position.getDispX());
		valueMap.setValue(element, SceneElementImpl.VAR_DISP_Y,
				position.getDispY());
	}

	@Override
	public void setOrientation(Orientation orientation) {
		gameState.getValueMap().setValue(element,
				SceneElementImpl.VAR_ORIENTATION, orientation);
	}

	@Override
	public Orientation getOrientation() {
		return orientation;
	}

	@Override
	public AssetDescriptor getCurrentAssetDescriptor() {

		AssetDescriptor a = element.getDefinition().getResources()
				.getAsset(getCurrentBundle(), SceneElementDefImpl.appearance);

		return getCurrentAssetDescriptor(a);
	}

	protected AssetDescriptor getCurrentAssetDescriptor(AssetDescriptor a) {
		if (a == null)
			return null;

		// FIXME this is horrible
		if (a instanceof FilteredDrawable) {
			return new FilteredDrawableImpl(
					(Drawable) getCurrentAssetDescriptor(((FilteredDrawable) a)
							.getDrawable()),
					((FilteredDrawable) a).getFilter());
		}
		// Check state
		if (a instanceof StateDrawable) {
			StateDrawable stateDrawable = (StateDrawable) a;
			return getCurrentAssetDescriptor(stateDrawable.getDrawable(state));
		}
		// Check orientation
		else if (a instanceof OrientedDrawable) {
			return getCurrentAssetDescriptor(((OrientedDrawable) a)
					.getDrawable(orientation));
		}
		// Check frame animation
		else if (a instanceof FramesAnimation) {
			return ((FramesAnimation) a).getFrameFromTime(timeDisplayed)
					.getDrawable();
		} else {
			return a;
		}
	}

	@Override
	public DrawableAsset<?, ?> getAsset() {
		DrawableAsset<?, ?> r = (DrawableAsset<?, ?>) assetHandler
				.getRuntimeAsset(getCurrentAssetDescriptor());
		if (r != null && !r.isLoaded())
			r.loadAsset();
		return r;
	}

	@Override
	public DrawableAsset<?, ?> getRenderAsset() {
		DrawableAsset<?, ?> r = getAsset();
		if (r instanceof DrawableAsset && r.isLoaded()) {
			setWidth(r.getWidth());
			setHeight(r.getHeight());
			return r.getDrawable();
		}
		return r;
	}

	@Override
	public List<AssetDescriptor> getAssets(List<AssetDescriptor> assetList,
			boolean allAssets) {
		List<EAdBundleId> bundles = new ArrayList<EAdBundleId>();
		if (allAssets)
			bundles.addAll(getElement().getDefinition().getResources()
					.getBundles());
		else
			bundles.add(getCurrentBundle());

		for (EAdBundleId bundle : bundles) {
			AssetDescriptor a = getElement().getDefinition().getResources()
					.getAsset(bundle, SceneElementDefImpl.appearance);
			getAssetsRecursively(a, assetList, true);
		}

		for (EAdAction a : getActions())
			sceneElementFactory.get(new ActionSceneElement(a)).getAssets(
					assetList, true);

		return assetList;
	}

	protected void getAssetsRecursively(AssetDescriptor a,
			List<AssetDescriptor> assetList, boolean allAssets) {
		if (a == null)
			return;

		if (a instanceof StateDrawable) {
			if (!allAssets)
				getAssetsRecursively(((StateDrawable) a).getDrawable(state),
						assetList, allAssets);
			else {
				for (String s : ((StateDrawable) a).getStates()) {
					getAssetsRecursively(((StateDrawable) a).getDrawable(s),
							assetList, allAssets);
				}

			}

		} else if (a instanceof OrientedDrawable) {
			if (!allAssets)
				getAssetsRecursively(
						((OrientedDrawable) a).getDrawable(orientation),
						assetList, allAssets);
			else {

				for (Orientation o : Orientation.values()) {
					getAssetsRecursively(((OrientedDrawable) a).getDrawable(o),
							assetList, allAssets);
				}
			}
		} else if (a instanceof FramesAnimation) {
			if (!allAssets)
				getAssetsRecursively(
						((FramesAnimation) a).getFrameFromTime(timeDisplayed)
								.getDrawable(), assetList, allAssets);
			else {
				for (int i = 0; i < ((FramesAnimation) a).getFrameCount(); i++) {
					getAssetsRecursively(((FramesAnimation) a)
							.getFrameFromTime(i).getDrawable(), assetList,
							allAssets);
				}
			}
		} else
			assetList.add(a);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void setScale(float scale) {
		gameState.getValueMap().setValue(element, SceneElementImpl.VAR_SCALE,
				scale);
	}

	public void setWidth(int width) {
		this.width = width;
		gameState.getValueMap().setValue(element, SceneElementImpl.VAR_WIDTH,
				width);
	}

	public void setHeight(int height) {
		this.height = height;
		gameState.getValueMap().setValue(element, SceneElementImpl.VAR_HEIGHT,
				height);
	}

	@Override
	public int getCenterX() {
		float[] f = transformation.getMatrix().multiplyPoint(width / 2,
				height / 2, true);
		return (int) f[0];
	}

	@Override
	public int getCenterY() {
		float[] f = transformation.getMatrix().multiplyPoint(width / 2,
				height / 2, true);
		return (int) f[1];
	}

	public EAdPosition getPosition() {
		return position;
	}

	@Override
	public EAdList<EAdAction> getActions() {
		return element.getDefinition().getActions();
	}

	public float getScale() {
		return scale;
	}

	public boolean isEnable() {
		return enable;
	}

	@Override
	public boolean contains(int x, int y) {
		if (this.getRenderAsset() != null)
			return this.getRenderAsset().contains(x, y);
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void render(GenericCanvas c) {
		if (this.getRenderAsset() != null)
			// FIXME fix me! no suppress warnings
			getRenderAsset().render(c);
		else {
			// FIXME Improve, when has no asset
			c.setPaint(EAdPaintImpl.BLACK_ON_WHITE);
			c.fillRect(0, 0, width, height);
		}
	}

	@Override
	public void doLayout(EAdTransformation transformation) {

	}

	public EAdBundleId getCurrentBundle() {
		EAdBundleId current = gameState.getValueMap().getValue(element,
				ResourcedElementImpl.VAR_BUNDLE_ID);
		if (current == null) {
			current = element.getDefinition().getInitialBundle();
			gameState.getValueMap().setValue(element,
					ResourcedElementImpl.VAR_BUNDLE_ID, current);
		}
		return current;
	}

	public void setCurrentBundle(EAdBundleId bundle) {
		gameState.getValueMap().setValue(element,
				ResourcedElementImpl.VAR_BUNDLE_ID, bundle);
	}

	public void setX(int x) {
		this.position.set(x, position.getY());
		gameState.getValueMap().setValue(element, SceneElementImpl.VAR_X, x);
	}

	public void setY(int y) {
		this.position.set(position.getX(), y);
		gameState.getValueMap().setValue(element, SceneElementImpl.VAR_Y, y);
	}

	public void setAlpha(float alpha) {
		gameState.getValueMap().setValue(element, SceneElementImpl.VAR_ALPHA,
				alpha);
		this.alpha = alpha;
	}
	
	public void setEnabled(boolean enable){
		gameState.getValueMap().setValue(element, SceneElementImpl.VAR_ENABLE,
				enable);
		this.enable = enable;
	}
}
