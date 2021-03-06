package es.eucm.ead.tests.techdemo.demos;

import es.eucm.ead.model.elements.BasicAdventureModel;
import es.eucm.ead.model.elements.BasicChapter;
import es.eucm.ead.model.elements.EAdAdventureModel;
import es.eucm.ead.model.elements.EAdChapter;
import es.eucm.ead.model.elements.scenes.EAdScene;
import es.eucm.ead.reader2.AdventureReader;
import es.eucm.ead.reader2.model.Manifest;
import es.eucm.ead.techdemo.elementfactories.scenes.scenes.InitScene;
import es.eucm.ead.tools.EAdUtils;
import es.eucm.ead.tools.java.JavaTextFileReader;
import es.eucm.ead.tools.java.JavaTextFileWriter;
import es.eucm.ead.tools.java.reflection.JavaReflectionProvider;
import es.eucm.ead.tools.java.utils.FileUtils;
import es.eucm.ead.tools.java.xml.SaxXMLParser;
import es.eucm.ead.writer2.AdventureWriter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class DemosToXMLTest {

	static private Logger logger = LoggerFactory
			.getLogger(DemosToXMLTest.class);

	@Test
	public void testWriteDemo() throws IOException {
		JavaTextFileWriter fileWriter = new JavaTextFileWriter();
		JavaTextFileReader fileReader = new JavaTextFileReader();

		InitScene scene = new InitScene();
		BasicChapter chapter = new BasicChapter(scene);
		EAdAdventureModel model = new BasicAdventureModel();
		model.getChapters().add(chapter);
		for (EAdScene s : scene.getScenes()) {
			chapter.addScene(s);
		}

		String path = "src/main/resources/";
		String path2 = "src/main/resources/techdemo2/";

		File folder2 = new File(path2);
		folder2.mkdirs();

		logger.debug("Writing demo model to {}", path);
		AdventureWriter writer = new AdventureWriter(
				new JavaReflectionProvider());
		AdventureReader reader = new AdventureReader(
				new JavaReflectionProvider(), new SaxXMLParser(), fileReader);

		writer.write(model, path, fileWriter);
		writer.write(model, path2, fileWriter);

		reader.setPath(path);
		Manifest manifest1 = reader.getManifest();
		Manifest manifest2 = reader.getManifest();
		reader.setPath(path2);
		Manifest manifest3 = reader.getManifest();

		assertTrue(EAdUtils.equals(manifest1, manifest2, false));
		assertTrue(EAdUtils.equals(manifest1, manifest3, false));

		for (String c : manifest1.getChapterIds()) {
			reader.setPath(path);
			EAdChapter chapter1 = reader.readChapter(c);
			reader.setPath(path2);
			EAdChapter chapter2 = reader.readChapter(c);
			assertTrue(EAdUtils.equals(chapter1, chapter2, false));
			for (String s : manifest2.getInitialScenesIds()) {
				reader.setPath(path);
				EAdScene scene1 = reader.readScene(s);
				reader.setPath(path2);
				EAdScene scene2 = reader.readScene(s);
				assertTrue(EAdUtils.equals(scene1, scene2, false));
			}
		}
		FileUtils.deleteRecursive(new File(path2));

	}
}
