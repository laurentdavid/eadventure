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

package es.eucm.ead.importer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import es.eucm.ead.importer.resources.ResourcesConverter;
import es.eucm.ead.importer.subconverters.ChapterConverter;
import es.eucm.ead.legacyplugins.model.BubbleNameEv;
import es.eucm.ead.legacyplugins.model.LegacyVars;
import es.eucm.ead.model.assets.drawable.basics.NinePatchImage;
import es.eucm.ead.model.assets.text.BasicFont;
import es.eucm.ead.model.elements.BasicAdventureModel;
import es.eucm.ead.model.elements.EAdAdventureModel;
import es.eucm.ead.model.elements.EAdChapter;
import es.eucm.ead.model.elements.effects.AddChildEf;
import es.eucm.ead.model.elements.events.SceneElementEv;
import es.eucm.ead.model.elements.events.enums.SceneElementEvType;
import es.eucm.ead.model.elements.extra.EAdMap;
import es.eucm.ead.model.elements.huds.BottomHud;
import es.eucm.ead.model.elements.scenes.BasicScene;
import es.eucm.ead.model.elements.scenes.GhostElement;
import es.eucm.ead.model.params.fills.ColorFill;
import es.eucm.ead.tools.java.JavaTextFileWriter;
import es.eucm.ead.tools.java.reflection.JavaReflectionProvider;
import es.eucm.ead.writer.StringWriter;
import es.eucm.ead.writer2.AdventureWriter;
import es.eucm.eadventure.common.data.adventure.AdventureData;
import es.eucm.eadventure.common.data.chapter.Chapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Random;

public class AdventureConverter {

	public static final String EFFECTS_GHOST_ID = "#engine.import.effects_ghost";

	static private Logger logger = LoggerFactory
			.getLogger(AdventureConverter.class);

	private OldReader oldReader;

	private ChapterConverter chapterConverter;

	private AdventureData adventureData;

	private ResourcesConverter resourceConverter;

	private ModelQuerier modelQuerier;

	private StringWriter stringWriter;

	private StringsConverter stringsConverter;

	private AdventureWriter writer;

	private ResourcesConverter resourcesConverter;

	private EAdAdventureModel model;

	private Random rand;

	public AdventureConverter() {
		Injector i = Guice.createInjector();
		oldReader = i.getInstance(OldReader.class);
		chapterConverter = i.getInstance(ChapterConverter.class);
		resourceConverter = i.getInstance(ResourcesConverter.class);
		modelQuerier = i.getInstance(ModelQuerier.class);
		stringsConverter = i.getInstance(StringsConverter.class);
		stringWriter = new StringWriter();
		writer = new AdventureWriter(new JavaReflectionProvider());
		resourcesConverter = i.getInstance(ResourcesConverter.class);
		rand = new Random();
	}

	public EAdAdventureModel getModel() {
		return model;
	}

	/**
	 * Converts the adventure in file to the new format, and stores the converted game in destinationFolder. If destinationFolder is null, a temp file is created
	 * @param file
	 * @param destinationFolder
	 * @return the destination folder path
	 */
	public String convert(String file, String destinationFolder) {

		if (destinationFolder == null) {
			String tempFolder = System.getProperty("java.io.tmpdir");
			File tmpDir = new File(tempFolder + File.separator
					+ "eAdventureTemp" + rand.nextInt());
			destinationFolder = tmpDir.getAbsolutePath();
		}

		// Reset attributes
		stringsConverter.clear();
		modelQuerier.clear();
		resourcesConverter.clear();

		logger.debug("Converting {}", file);
		resourceConverter.setPath(destinationFolder);

		adventureData = oldReader.loadGame(file);
		modelQuerier.setAdventureData(adventureData);

		// This element will be displayed whenever a set of effects is launched,
		// to avoid user interaction with other elements in the scene
		GhostElement effectsGhost = new GhostElement(true);
		effectsGhost.setId(EFFECTS_GHOST_ID);
		effectsGhost.setInitialVisible(false);

		AddChildEf addEffectsGhost = new AddChildEf(BottomHud.ID, effectsGhost);

		SceneElementEv initEvent = new SceneElementEv();
		initEvent.addEffect(SceneElementEvType.INIT, addEffectsGhost);

		model = new BasicAdventureModel();
		model.getEvents().add(initEvent);

		// Add some vars
		// [AD - Title]
		model.setVarInitialValue(BasicAdventureModel.GAME_TITLE, modelQuerier
				.getAventureData().getTitle());
		// Add some legacy vars
		model.setVarInitialValue(LegacyVars.FIRST_PERSON, modelQuerier
				.isFirstPersonGame());
		BubbleNameEv event = new BubbleNameEv();
		event.setBubble(new NinePatchImage("@drawable/bubblename.png", 15, 15,
				15, 15));
		event.setFont(new BasicFont("@binary/fonts/coolvetica-16"));
		event.setTextPaint(ColorFill.WHITE);
		model.getEvents().add(event);

		// Setting plugins
		EAdMap<String, String> eventPlugins = new EAdMap<String, String>();
		eventPlugins.put(BubbleNameEv.class.getName(),
				"es.eucm.ead.legacyplugins.engine.BubbleNameGO");
		// [GE - Arrow] [GE - Follow]
		EAdMap<String, String> sceneElementPlugins = new EAdMap<String, String>();
		sceneElementPlugins
				.put(BasicScene.class.getName(),
						"es.eucm.ead.legacyplugins.engine.sceneelements.DynamicSceneGO");

		model
				.setVarInitialValue(BasicAdventureModel.EVENTS_BINDS,
						eventPlugins);
		model.setVarInitialValue(BasicAdventureModel.SCENES_ELEMENT_BINDS,
				sceneElementPlugins);

		for (Chapter c : adventureData.getChapters()) {
			EAdChapter chapter = chapterConverter.convert(c);
			model.addChapter(chapter);
		}

		String path = destinationFolder
				+ (destinationFolder.charAt(destinationFolder.length() - 1) == '/' ? ""
						: "/");
		// Create data.xml
		writer.write(model, path, new JavaTextFileWriter());

		// Create strings.xml
		File f = new File(path, "strings.xml");
		try {
			stringWriter.write(f.getAbsolutePath(), stringsConverter
					.getStrings());
		} catch (Exception e) {
			logger.error("Error writing strings file while importing '{}'",
					file, e);
		}

		return destinationFolder;

	}

	public void setEnableSimplifications(boolean enableSimplifications) {
		writer.setEnableSimplifications(enableSimplifications);
	}
}
