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

package es.eucm.ead.engine.assets;

import es.eucm.ead.engine.assets.drawables.RuntimeDrawable;
import es.eucm.ead.engine.assets.fonts.FontHandler;
import es.eucm.ead.model.assets.AssetDescriptor;
import es.eucm.ead.model.assets.drawable.EAdDrawable;
import es.eucm.ead.model.assets.multimedia.EAdVideo;
import es.eucm.ead.model.elements.scenes.EAdScene;
import es.eucm.ead.model.interfaces.features.Resourced;
import es.eucm.ead.tools.TextFileReader;

import java.util.List;

/**
 * <p>
 * Handler of the assets in the eAdventure engine
 * </p>
 * <p>
 * The class that implements this interfaces is in charge of loading the
 * different assets into the system, and possibly performing some platform
 * specific optimizations as necessary.
 * </p>
 */
public interface AssetHandler extends TextFileReader {

	/**
	 * Terminate the asset handler, so resources are freed accordingly
	 */
	void terminate();

	/**
	 * Returns the runtime asset asset represented by the given id in the
	 * element for the selected bundle
	 * 
	 * @param element
	 *            The element with the asset
	 * @param bundleId
	 *            The selected bundle
	 * @param id
	 *            The id of the asset
	 * @return The platform-independent runtime asset
	 * @see RuntimeAsset
	 */
	RuntimeAsset<?> getRuntimeAsset(Resourced element, String bundleId,
			String id);

	/**
	 * Returns the runtime asset asset represented by the given id in the
	 * element, with no asset bundle
	 * 
	 * @param element
	 *            The element with the asset
	 * @param id
	 *            The id of the asset
	 * @return The platform-independent runtime asset
	 * @see RuntimeAsset
	 */
	RuntimeAsset<?> getRuntimeAsset(Resourced element, String id);

	/**
	 * Returns the runtime asset for a given asset descriptor
	 * 
	 * @param <T>
	 *            The type of the asset descriptor
	 * @param descriptor
	 *            The descriptor of the asset
	 * @return The runtime asset
	 * @see RuntimeAsset
	 * @see AssetDescriptor
	 */
	<T extends AssetDescriptor> RuntimeAsset<T> getRuntimeAsset(T descriptor);

	/**
	 * Returns the runtime asset for a given asset descriptor. It loads it if
	 * parameter load is true. Otherwise, asset must be loaded through
	 * {@link RuntimeAsset#loadAsset()}
	 * 
	 * @param descriptor
	 *            the asset descriptor
	 * @param load
	 *            if the asset must be loaded
	 * @return the runtime asset
	 */
	<T extends AssetDescriptor> RuntimeAsset<T> getRuntimeAsset(T descriptor,
			boolean load);

	<T extends EAdDrawable> RuntimeDrawable<T> getDrawableAsset(T descriptor);

	/**
	 * Sets the font handler. The Asset handler cleans it when needed
	 * @param fontHandler the font handler
	 */
	void setFontHandler(FontHandler fontHandler);

	/**
	 * Returns true if the adventure assets have been correctly loaded
	 * 
	 * @return true if assets loaded
	 */
	boolean isLoaded();

	/**
	 * Frees and removes all the assets contained in the cache, except for the
	 * ones in the exceptions list
	 * 
	 * @param exceptions
	 *            list with assets not to be deleted
	 */
	void clean(List<AssetDescriptor> exceptions);

	/**
	 * Sets the resources location
	 * 
	 * @param uri
	 *            uri point to the resources locatin root
	 */
	void setResourcesLocation(String uri);

	/**
	 * Returns a set of strings containing the text file in the given path.
	 * Useful to read some configuration files at multi-platform level
	 * 
	 * @param path
	 *            textFile path
	 * @return
	 */
	String getTextFile(String path);

	/**
	 * Loads a text and passes it to a handle. This method should only be used
	 * in special cases, wherever {@link AssetHandler#getTextFile(String)} is
	 * not available
	 * 
	 * @param path
	 * @param textHandler
	 */
	void getTextfileAsync(String path, TextHandler textHandler);

	/**
	 * Sets if the cache is enable for this asset handler. Cache is enabled by
	 * default.
	 * 
	 * @param enable
	 */
	void setCacheEnabled(boolean enable);

	/**
	 * Queues the scene to load all its assets. This method DOES NOT load the
	 * assets. {@link AssetHandler#loadStep()} must be used in order to do that.
	 * 
	 * @param scene
	 *            the scene whose assets must be loaded
	 */
	void queueSceneToLoad(EAdScene scene);

	/**
	 * Loads one asset of the queue.
	 * 
	 * @return if there are assets left to be loaded
	 */
	boolean loadStep();

	/**
	 * Clears the assets queue
	 */
	void clearAssetQueue();

	/**
	 * Returns if there's a file with the given path
	 * @param path
	 * @return
	 */
	boolean fileExists(String path);

	public static interface TextHandler {

		void handle(String text);
	}

	/**
	 * Refreshes the assets (normally used to refresh those assets that change
	 * with localization)
	 */
	void refresh();

	/**
	 * Removes the asset descriptor from the cache, and frees whatever resources
	 * its runtime asset had
	 * 
	 * @param assetDescriptor
	 */
	void remove(AssetDescriptor assetDescriptor);

	/**
	 * Sets the current language
	 * 
	 * @param currentLanguage
	 */
	void setLanguage(String currentLanguage);

	/**
	 * Returns how many elements are contained by the asset handler cache
	 * 
	 * @return
	 */
	int getCacheSize();

	/**
	 * Makes aware the asset handler of an existing video
	 * 
	 * @param v
	 *            video asset
	 */
	void addVideo(EAdVideo v);

	/**
	 * Preloads the video in a separate thread (when possible)
	 * 
	 * @return true if the preloading is possible
	 */
	boolean preloadVideos();

	/**
	 * Return if it is preloading videos
	 * 
	 * @return
	 */
	boolean isPreloadingVideos();

	/**
	 * Returns an special asset renderer (for example, for a video)
	 * 
	 * @param specialAsset
	 * @return
	 */
	<T extends AssetDescriptor> SpecialAssetRenderer<T, ?> getSpecialAssetRenderer(
			T specialAsset);

	void clean();

}
