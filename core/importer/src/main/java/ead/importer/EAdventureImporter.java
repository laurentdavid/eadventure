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

package ead.importer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import ead.common.model.elements.EAdAdventureModel;
import ead.common.params.text.EAdString;
import ead.importer.auxiliar.inputstreamcreators.ImporterInputStreamCreator;
import ead.importer.interfaces.EAdElementFactory;
import ead.importer.interfaces.ResourceImporter;
import ead.tools.StringHandler;
import ead.tools.java.JavaToolsModule;
import ead.writer.EAdAdventureModelWriter;
import ead.writer.StringWriter;
import es.eucm.eadventure.common.data.adventure.AdventureData;
import es.eucm.eadventure.common.loader.InputStreamCreator;
import es.eucm.eadventure.common.loader.Loader;
import es.eucm.eadventure.common.loader.incidences.Incidence;

/**
 * An importer for old games from 1.x version
 * 
 */
public class EAdventureImporter {

	/**
	 * Sets if the importation must be done in debug mode. When this is
	 * activated, barrier, exits and active areas are not transparent
	 */
	public static final boolean IMPORTER_DEBUG = false;

	public static final String CURRENT_EAD_ENGINE_VERSION = "ead-200";

	private EAdElementImporter<AdventureData, EAdAdventureModel> adventureImporter;

	private ResourceImporter resourceImporter;

	private InputStreamCreator inputStreamCreator;

	private StringHandler stringsHandler;

	private StringWriter stringFileHandler;

	private EAdElementFactory elementFactory;

	private String destinyFile;

	private Random rand = new Random();

	private List<ImporterProgressListener> listeners;

	private static final Logger logger = LoggerFactory
			.getLogger("EAdventureImporter");

	public EAdventureImporter() {
		Injector i = Guice.createInjector(new ImporterModule(),
				new JavaToolsModule());

		this.adventureImporter = i
				.getInstance(Key
						.get(new TypeLiteral<EAdElementImporter<AdventureData, EAdAdventureModel>>() {
						}));
		this.resourceImporter = i.getInstance(ResourceImporter.class);
		this.inputStreamCreator = i.getInstance(InputStreamCreator.class);
		this.stringsHandler = i.getInstance(StringHandler.class);
		this.stringFileHandler = new StringWriter();
		this.elementFactory = i.getInstance(EAdElementFactory.class);
		this.listeners = new ArrayList<ImporterProgressListener>();
	}

	/**
	 * Imports and old game form 1.x version
	 * 
	 * @param eadFile
	 *            original ead file
	 * @param destiny
	 *            File where the import project will be stored. If {@code null},
	 *            import project won't be saved
	 * @param format
	 *            format for the output file. Can be "none" to make a folder
	 *            with the game, or "ead" or "zip" to pack the game in a zip
	 *            file
	 * @return An {@link EAdventureModel} complete with all game information
	 */
	public EAdAdventureModel importGame(String eadFile, String destiny,
			String format) {
		// Init importer
		updateProgress(0, "Starting importer...");
		stringsHandler.getStrings().clear();
		((ImporterInputStreamCreator) inputStreamCreator).setFile(eadFile);
		elementFactory.init();
		boolean zipped = format.equals("ead") || format.equals("zip");

		// Loading old game
		updateProgress(10, "Loading old game...");
		AdventureData adventureData = loadGame();

		if (adventureData == null) {
			updateProgress(100, "Error: model couldn't be loaded");
			return null;
		}

		updateProgress(40, "Creating temporary files...");

		// Set the destination folder (a temporary folder is used when the game
		// is going to be zipped)
		File destinyFolder = null;
		if (zipped || destiny == null) {

			String tempFolder = System.getProperty("java.io.tmpdir");
			File tmpDir = new File(tempFolder + File.separator
					+ "eAdventureTemp" + rand.nextInt());
			destinyFolder = tmpDir;
		} else {
			destinyFolder = new File(destiny);
		}

		if (destinyFolder.exists() == false) {
			destinyFolder.mkdir();
		}

		resourceImporter.setPath(destinyFolder.getAbsolutePath());
		destinyFile = destinyFolder.getAbsolutePath();

		updateProgress(50, "Converting old model...");
		EAdAdventureModel model = adventureImporter.init(adventureData);
		model = adventureImporter.convert(adventureData, model);

		if (destiny != null) {
			updateProgress(90, "Creating " + destiny);
			createGameFile(model, destinyFolder.getAbsolutePath(), destiny, "."
					+ format, "Imported version", zipped);
		}

		updateProgress(100, "Done.");

		return model;

	}

	/**
	 * Creates a game file.
	 * 
	 * @param model
	 *            to save
	 * @param path
	 *            to save it to
	 * @param destination
	 *            file name within path
	 * @param forceExtension
	 *            extension to set on destination; may be null
	 * @param propertiesComment
	 *            comment to set on properties file
	 */
	public boolean createGameFile(EAdAdventureModel model, String path,
			String destination, String forceExtension,
			String propertiesComment, boolean zipped) {

		boolean ok = true;

		// Create data.xml
		EAdAdventureModelWriter writer = new EAdAdventureModelWriter();

		OutputStream os = null;
		try {
			os = new FileOutputStream(new File(path, "data.xml"));
			writer.write(model, os);
		} catch (Exception e) {
			logger.error("Error writing data.xml while importing to '{}'",
					destination, e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (Exception e) {
					logger.error("Error closing data.xml "
							+ "while importing '{}", destination, e);
					ok = false;
				}
			}
		}

		// Create strings.xml
		File f = new File(path, "strings.xml");
		try {
			stringFileHandler.write(f.getAbsolutePath(),
					stringsHandler.getStrings());
		} catch (Exception e) {
			logger.error("Error writing strings file while importing '{}'",
					destination, e);
			ok = false;
		}

		// ead.properties
		File propertiesFile = new File(path, "ead.properties");
		Properties properties = new Properties();
		properties.setProperty("targetEngine", CURRENT_EAD_ENGINE_VERSION);

		try {
			FileOutputStream output = new FileOutputStream(propertiesFile);
			properties.store(output, propertiesComment);
		} catch (Exception e) {
			logger.error("Error writing properties file '{}'",
					propertiesFile.getAbsolutePath(), e);
			ok = false;
		}

		if (zipped) {
			// Create final zip file with name destination+extension
			// with everything (recursive) in 'path'
			String fileName = (forceExtension == null || destination
					.endsWith(forceExtension)) ? destination : destination
					+ forceExtension;
			File outputZipFile = new File(fileName);
			ZipOutputStream out = null;
			try {
				out = new ZipOutputStream(new BufferedOutputStream(
						new FileOutputStream(outputZipFile)));
				addFolderToZip(out, new File(path), false);
				logger.debug("Zip file {} complete",
						outputZipFile.getAbsolutePath());
			} catch (Exception e) {
				logger.error("Error outputting zip to {}", destination, e);
				ok = false;
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException ioe) {
						logger.error(
								"Could not close zip file writing to '{}'",
								fileName, ioe);
					}
				}
			}
		}
		return ok;
	}

	/**
	 * Adds all files in folder to the supplied zipOutputStream. Optionally
	 * includes their full paths too.
	 * 
	 * @param zip
	 *            destination stream
	 * @param folder
	 *            folder to add
	 * @param addPath
	 *            whether to include full path information or not
	 * @throws IOException
	 *             if any error while adding
	 */
	public void addFolderToZip(ZipOutputStream zip, File folder, boolean addPath)
			throws IOException {

		logger.debug("adding folder {} to zip", folder);
		if (folder == null || !folder.isDirectory()) {
			throw new IllegalArgumentException("not a folder: " + folder);
		}

		byte data[] = new byte[1024];
		File files[] = folder.listFiles();
		for (File f : files) {
			try {
				if (f.isDirectory()) {
					addFolderToZip(zip, f, true);
				} else {
					String entryName = (addPath ? f.getParentFile().getName()
							+ "/" : "")
							+ f.getName();

					zip.putNextEntry(new ZipEntry(entryName));

					BufferedInputStream in = new BufferedInputStream(
							new FileInputStream(f), data.length);
					int count;
					while ((count = in.read(data, 0, data.length)) != -1) {
						zip.write(data, 0, count);
					}
					zip.closeEntry();
					in.close();
				}
			} catch (IOException e) {
				logger.error("Error adding folder {} to zip", folder, e);
				throw e;
			}
		}
	}

	/**
	 * Loads an old model AdventureData
	 * 
	 */
	public AdventureData loadGame() {
		ArrayList<Incidence> incidences = new ArrayList<Incidence>();
		AdventureData data = null;
		try {
			data = Loader.loadAdventureData(inputStreamCreator, incidences);
		} catch (Exception e) {
			logger.error("Exception while reading old <e-Adventure> game", e);
		}
		if (data == null) {
			logger.warn("Invalid <e-Adventure> game");
		}

		if (incidences.size() > 0) {
			logger.info("There were the following incidences during loading:");
			for (Incidence i : incidences) {
				logger.info(i.getMessage());
			}
		}
		return data;
	}

	public String getDestinyFile() {
		return destinyFile;
	}

	public Map<EAdString, String> getStrings() {
		return stringsHandler.getStrings();
	}

	public void addProgressListener(ImporterProgressListener progressListener) {
		listeners.add(progressListener);
	}

	public void removeProgressListener(ImporterProgressListener progressListener) {
		listeners.remove(progressListener);
	}

	public void updateProgress(int progress, String text) {
		logger.debug("Importer progress update: {}", text);
		for (ImporterProgressListener l : listeners) {
			l.update(progress, text);
		}
	}

	public static interface ImporterProgressListener {

		// TODO It would be interesting to pass two values, current
		// and max, so the progress bar can extrapolate between the
		// two and give the appearance of work most of the time
		public void update(int progress, String text);

	}

	public EAdAdventureModel importGame(String absolutePath, String destiny) {

		return importGame(absolutePath, destiny, "none");
	}
}
