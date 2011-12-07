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

package es.eucm.eadventure.common.impl.importer.resources;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import es.eucm.eadventure.common.EAdElementImporter;
import es.eucm.eadventure.common.GenericImporter;
import es.eucm.eadventure.common.data.animation.Animation;
import es.eucm.eadventure.common.data.animation.ImageLoaderFactory;
import es.eucm.eadventure.common.data.chapter.conditions.Conditions;
import es.eucm.eadventure.common.data.chapter.elements.Item;
import es.eucm.eadventure.common.data.chapter.resources.Resources;
import es.eucm.eadventure.common.impl.importer.interfaces.ResourceImporter;
import es.eucm.eadventure.common.interfaces.features.Evented;
import es.eucm.eadventure.common.interfaces.features.Resourced;
import es.eucm.eadventure.common.loader.InputStreamCreator;
import es.eucm.eadventure.common.loader.Loader;
import es.eucm.eadventure.common.model.conditions.impl.ANDCondition;
import es.eucm.eadventure.common.model.conditions.impl.NOTCondition;
import es.eucm.eadventure.common.model.elements.EAdCondition;
import es.eucm.eadventure.common.model.events.EAdConditionEvent;
import es.eucm.eadventure.common.model.events.enums.ConditionedEventType;
import es.eucm.eadventure.common.model.events.impl.EAdConditionEventImpl;
import es.eucm.eadventure.common.predef.model.effects.EAdChangeAppearance;
import es.eucm.eadventure.common.resources.EAdBundleId;
import es.eucm.eadventure.common.resources.assets.AssetDescriptor;
import es.eucm.eadventure.common.resources.assets.drawable.basics.impl.animation.Frame;
import es.eucm.eadventure.common.resources.assets.drawable.basics.impl.animation.FramesAnimation;

/**
 * Resource Importer
 * 
 * 
 */
@Singleton
public class ResourceImporterImpl implements ResourceImporter {

	/**
	 * Conditions importer
	 */
	private EAdElementImporter<Conditions, EAdCondition> conditionsImporter;

	/**
	 * Animation importer
	 */
	private GenericImporter<Animation, FramesAnimation> animationImporter;

	private ImageLoaderFactory imageLoader;

	private InputStreamCreator inputStreamCreator;

	/**
	 * Absolute path where the new adventure must be placed
	 */
	private String newAdventurePath;

	/**
	 * Stores correspondences between URIs at old adventure project and URIS at
	 * new adventure project
	 */
	private Map<String, String> urisCorrespondences;

	/**
	 * A map matching old uris with the new assets
	 */
	private Map<String, AssetDescriptor> assets;

	private static final Logger logger = Logger
			.getLogger("ResourceImporterImpl");

	@Inject
	public ResourceImporterImpl(
			EAdElementImporter<Conditions, EAdCondition> conditionsImporter,
			GenericImporter<Animation, FramesAnimation> animationImporter,
			ImageLoaderFactory imageLoader,
			InputStreamCreator inputStreamCreator) {
		this.imageLoader = imageLoader;
		this.inputStreamCreator = inputStreamCreator;
		this.conditionsImporter = conditionsImporter;
		this.animationImporter = animationImporter;
		urisCorrespondences = new HashMap<String, String>();
		assets = new HashMap<String, AssetDescriptor>();

	}

	@Override
	public void setPath(String newAdventurePath) {
		this.newAdventurePath = newAdventurePath;

		createFolders();
	}

	private void createFolders() {
		File f = new File(newAdventurePath + "/drawable");
		f.mkdirs();
		f = new File(newAdventurePath + "/binary");
		f.mkdirs();

	}

	@Override
	public String getURI(String oldURI) {
		String newURI = urisCorrespondences.get(oldURI);
		if (newURI == null) {

			String folder = getFolder(oldURI);

			String fileName = oldURI.replace("/", "_");
			newURI = folder + "/" + fileName;

			if (!copyFile(oldURI, newURI)) {
				logger.log(Level.SEVERE, "Missing resource: " + oldURI);
				return null;
			}

			newURI = "@" + newURI;
			urisCorrespondences.put(oldURI, newURI);
		}
		return newURI;
	}

	private String getFolder(String oldURI) {
		if (oldURI.endsWith(".png") || oldURI.endsWith(".jpg"))
			return DRAWABLE;
		else
			return BINARY;
	}

	@Override
	public boolean copyFile(String oldURI, String newURI) {

		File toResourceFile = new File(newAdventurePath, newURI);

		try {
			InputStream in = inputStreamCreator.buildInputStream(oldURI);
			if (in == null)
				return false;
			OutputStream out = new FileOutputStream(toResourceFile);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();

		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;

	}

	public void importResources(Resourced element, List<Resources> resources,
			Map<String, String> resourcesStrings,
			Map<String, Object> resourcesObjectClasses) {
		int i = 0;
		EAdCondition previousCondition = null;

		// We iterate for the resources. Each resource is associated to some
		// conditions. These
		// conditions are transformed in ConditionedEvents.
		for (Resources r : resources) {
			EAdBundleId bundleId;
			if (i == 0) {
				bundleId = element.getInitialBundle();
			} else {
				bundleId = new EAdBundleId("bundle_" + i);
				element.getResources().addBundle(bundleId);
			}

			for (String resourceType : resourcesStrings.keySet()) {
				String assetPath = r.getAssetPath(resourceType);
				Object o = resourcesObjectClasses.get(resourceType);
				AssetDescriptor asset = null;
				if (o instanceof Class) {
					asset = this.getAssetDescritptor(assetPath, (Class<?>) o);
				} else if (o instanceof List) {
					asset = (AssetDescriptor) ((List<?>) o).get(i);
				} else if (o instanceof AssetDescriptor) {
					asset = (AssetDescriptor) o;
				}

				if (asset != null) {
					String propertyName = resourcesStrings.get(resourceType);
					element.getResources().addAsset(bundleId, propertyName,
							asset);
				}

			}

			if (element instanceof Evented) {

				EAdConditionEvent conditionEvent = new EAdConditionEventImpl();
				conditionEvent
						.setId(bundleId.getBundleId() + "_condition_" + i);

				EAdCondition condition = conditionsImporter.init(r
						.getConditions());
				condition = conditionsImporter.convert(r.getConditions(),
						condition);

				if (previousCondition == null) {
					previousCondition = new NOTCondition(condition);
				} else {
					EAdCondition temp = condition;
					condition = new ANDCondition(condition, previousCondition);
					previousCondition = new ANDCondition(previousCondition,
							new NOTCondition(temp));
				}
				conditionEvent.setCondition(condition);

				EAdChangeAppearance changeAppereance = new EAdChangeAppearance(
						null, bundleId);
				changeAppereance.setId(conditionEvent.getId()
						+ "change_appearence");
				conditionEvent.addEffect(ConditionedEventType.CONDITIONS_MET,
						changeAppereance);

				((Evented) element).getEvents().add(conditionEvent);
			}

			i++;
		}

	}

	@Override
	public AssetDescriptor getAssetDescritptor(String assetPath, Class<?> clazz) {
		if (assetPath == null)
			return null;

		if (assets.containsKey(assetPath)) {
			return assets.get(assetPath);
		}

		AssetDescriptor asset = null;

		// Special case
		if (assetPath.startsWith("assets/special/EmptyAnimation")) {
			if (!(assetPath.endsWith(".eaa") || assetPath.endsWith("_01.png")))
				if (fileExists(assetPath + ".eaa"))
					assetPath += ".eaa";
				else if (fileExists(assetPath + "_01.png")) {
					assetPath += "_01.png";
				} else if (fileExists(assetPath + "_01.jpg")) {
					assetPath += "_01.jpg";
				} else {
					logger.info("There was a problem with EmptyAnimation");
				}
		}
		if (assetPath.startsWith("assets/animation")
				|| assetPath.startsWith("assets/special/EmptyAnimation")) {
			if (assetPath.endsWith(".eaa")) {
				Animation a = Loader.loadAnimation(inputStreamCreator,
						assetPath, imageLoader);
				asset = animationImporter.init(a);
				asset = animationImporter.convert(a, asset);
			} else {
				asset = importImagesAnimation(assetPath);
			}
		} else {
			String newAssetPath = getURI(assetPath);

			try {
				asset = (AssetDescriptor) clazz.getConstructor(String.class)
						.newInstance(newAssetPath);

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}

		if (asset != null)
			assets.put(assetPath, asset);
		return asset;

	}

	/**
	 * Imports an animation in the form _01.png, _02.png, or _01.jpg, _02.jpg,
	 * etc.
	 * 
	 * @param assetPath
	 *            the root asset path
	 * @return the asset
	 */
	private AssetDescriptor importImagesAnimation(String assetPath) {
		String fileExtension = ".png";
		if (!fileExists(assetPath + "_01" + fileExtension)) {
			fileExtension = ".jpg";
		}
		FramesAnimation frames = new FramesAnimation();
		int frame = 1;
		int frameTime = 500;
		String oldPath = assetPath + "_0" + frame++ + fileExtension;
		while (fileExists(oldPath)) {
			String newPath = getURI(oldPath);
			frames.addFrame(new Frame(newPath, frameTime));
			oldPath = assetPath + "_0" + frame++ + fileExtension;
		}
		return frames;
	}

	@Override
	public String getNewProjecFolder() {
		return newAdventurePath;
	}

	public boolean fileExists(String oldURI) {
		boolean exists = false;
		InputStream is = inputStreamCreator.buildInputStream(oldURI);
		if (is != null) {
			exists = true;
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return exists;

	}

	public BufferedImage loadImage(String oldUri) {
		try {
			return ImageIO.read(inputStreamCreator.buildInputStream(oldUri));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Dimension getDimensions(String imageUri) {
		if (imageUri != null) {
			BufferedImage image = loadImage(imageUri);
			if (image != null) {
				Dimension d = new Dimension(image.getWidth(), image.getHeight());
				image.flush();
				return d;
			}
		}
		return new Dimension(100, 100);
	}

}
