package es.eucm.eadventure.engine.html;

import playn.core.PlayN;
import playn.html.HtmlAssetManager;
import playn.html.HtmlGame;
import playn.html.HtmlPlatform;

import com.google.gwt.core.client.GWT;

import es.eucm.eadventure.common.elementfactories.EAdElementsFactory;
import es.eucm.eadventure.common.model.effects.impl.EAdChangeScene;
import es.eucm.eadventure.common.model.elements.EAdAdventureModel;
import es.eucm.eadventure.common.model.elements.EAdScene;
import es.eucm.eadventure.common.model.elements.impl.EAdBasicSceneElement;
import es.eucm.eadventure.common.model.elements.impl.EAdSceneImpl;
import es.eucm.eadventure.common.model.elements.impl.EAdVideoScene;
import es.eucm.eadventure.common.model.impl.EAdAdventureModelImpl;
import es.eucm.eadventure.common.model.impl.EAdChapterImpl;
import es.eucm.eadventure.common.resources.StringHandler;
import es.eucm.eadventure.common.resources.assets.drawable.basics.Image;
import es.eucm.eadventure.common.resources.assets.drawable.basics.impl.ImageImpl;
import es.eucm.eadventure.common.resources.assets.multimedia.Video;
import es.eucm.eadventure.common.resources.assets.multimedia.impl.VideoImpl;
import es.eucm.eadventure.engine.core.EAdEngine;
import es.eucm.eadventure.engine.core.Game;
import es.eucm.eadventure.engine.core.platform.GUI;
import es.eucm.eadventure.engine.core.platform.impl.PlayNGinInjector;

public class EAdEngineHtml extends HtmlGame {

	private final static PlayNGinInjector injector = GWT
			.create(PlayNGinInjector.class);

	@Override
	public void start() {

		HtmlAssetManager assets = HtmlPlatform.register().assetManager();
		assets.setPathPrefix("");

		injector.getPlatformLauncher();
		Game game = injector.getGame();
		game.loadGame();

		EAdAdventureModel model = new EAdAdventureModelImpl();
		EAdChapterImpl chapter = new EAdChapterImpl("chapter1");

		model.getChapters().add(chapter);
		EAdScene s = new EAdSceneImpl("scene");
		Image i = new ImageImpl("@drawable/background1.png");
		s.getBackground()
				.getResources()
				.addAsset(s.getBackground().getInitialBundle(),
						EAdBasicSceneElement.appearance, i);


		EAdVideoScene s2 = new EAdVideoScene("videoScene");
		Video video = new VideoImpl("@binary/bbb_trailer_400p.ogv");
		s2.getResources().addAsset(s2.getResources().getInitialBundle(), "video", video);
		s2.setUpForEngine();
		
		EAdChangeScene changeScene = new EAdChangeScene("changeScene");
		changeScene.setNextScene(s2);

//		EAdScene s2 = new CharacterScene();

		//EAdScene s2 = new ShapeScene();

//		 EAdScene s2 = new SpeakAndMoveScene();
		
//		EAdScene s2 = new NgMainScreen();
		/*
		 * getBackground().getResources().addAsset(getBackground().getInitialBundle
		 * (), EAdBasicSceneElement.appearance, new
		 * ImageImpl("@drawable/loading.png"));
		 */
		chapter.getScenes().add(s2);
		chapter.setInitialScene(s2);

		game.setGame(model, chapter);
		
		// String handler after creating the scene
		StringHandler stringHandler = injector.getStringHandler();
		stringHandler.addStrings(EAdElementsFactory.getInstance()
				.getStringFactory().getStrings());

		GUI gui = injector.getGUI();

		PlayN.run(new EAdEngine(game, gui, injector.getAssetHandler(), injector
				.getMouseState(), injector.getPlatformConfiguration()));
	}

}
