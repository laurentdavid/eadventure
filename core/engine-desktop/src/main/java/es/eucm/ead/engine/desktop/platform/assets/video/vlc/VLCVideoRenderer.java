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

package es.eucm.ead.engine.desktop.platform.assets.video.vlc;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.io.File;

import es.eucm.ead.engine.desktop.platform.assets.GdxDesktopAssetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jna.NativeLibrary;

import es.eucm.ead.model.assets.multimedia.EAdVideo;
import es.eucm.ead.engine.assets.SpecialAssetRenderer;
import es.eucm.ead.engine.game.SoundManager;

/**
 * <p>
 * Video renderer for desktop (and applets) using vlcj library {@link http
 * ://code.google.com/p/vlcj/}
 * </p>
 *
 *
 */
@Singleton
public class VLCVideoRenderer implements
		SpecialAssetRenderer<EAdVideo, Component> {

	/**
	 * Logger
	 */
	static private Logger logger = LoggerFactory
			.getLogger(VLCVideoRenderer.class);

	/**
	 * The vlcj media player (controls, etc.)
	 */
	private static EmbeddedMediaPlayer mediaPlayer;

	/**
	 * The vlcj surface for the video
	 */
	private static CanvasVideoSurface videoSurface;

	/**
	 * True if finished
	 */
	protected boolean finished;

	/**
	 * True if started
	 */
	protected boolean started;

	/**
	 * The vlcj media player factory
	 */
	private static MediaPlayerFactory mediaPlayerFactory;

	/**
	 * Used to configure vlc when necessary
	 */
	private static String vlcOptions = "";

	/**
	 * The path of the video file
	 */
	private String path;

	/**
	 * The eAd asset handler
	 */
	private GdxDesktopAssetHandler assetHandler;

	/**
	 * Sets if VLC has been successfully loaded
	 */
	private static boolean vlcLoaded;

	private static VLCMediaPlayerEventListener vlcListener;

	private static Canvas canvas;

	/**
	 * Sound manager, to control videos volume
	 */
	private SoundManager soundManager;

	private boolean wasSilence;

	static {
		logger.debug("Loading VLC...");
		initializeVariables();
		initComponent();
		logger.debug("VLC loaded: {}", mediaPlayer != null);
	}

	@Inject
	public VLCVideoRenderer(GdxDesktopAssetHandler assetHandler,
			SoundManager soundManager) {
		this.assetHandler = assetHandler;
		this.soundManager = soundManager;
		vlcListener.setRenderer(this);
	}

	@Override
	public Component getComponent(EAdVideo asset) {
		if (vlcLoaded) {
			try {
				return getVLCComponent(asset);
			} catch (Exception e) {
				logger.warn("VLC not supported in this OS. Videos won't load");
				this.setFinished(true);
				return null;
			}
		} else {
			logger.warn("VLC not supported in this OS. Videos won't load");
			this.setFinished(true);
			return null;
		}
	}

	private static void initComponent() {
		String[] options = { vlcOptions };
		mediaPlayerFactory = new MediaPlayerFactory(options);
		canvas = new Canvas();
		canvas.setBackground(Color.black);
		mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
		mediaPlayer.setAdjustVideo(false);
		mediaPlayer.setCropGeometry("4:3");
		videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
		mediaPlayer.setVideoSurface(videoSurface);

		// canvas.addMouseListener(new MouseAdapter() {
		//
		// @Override
		// public void mousePressed(MouseEvent e) {
		// finished = true;
		// }
		//
		// });

		vlcListener = new VLCMediaPlayerEventListener();
		mediaPlayer.addMediaPlayerEventListener(vlcListener);

	}

	/**
	 * Initialize system and system dependent variables for vlcj.
	 */
	private static void initializeVariables() {
		if (System.getProperty("jna.nosys") == null) {
			System.setProperty("jna.nosys", "true");
		}
		vlcLoaded = false;
		String pathLibvlc = null;
		String pathPlugins = null;
		vlcOptions = "--no-video-title-show";
		String os = System.getProperty("os.name").toLowerCase();
		// Looking for in embedded installation
		if (os.contains("win")) {
			pathLibvlc = "vlc/vlc-windows";
			pathPlugins = "vlc/vlc-windows/plugins";
		} else if (os.contains("mac")) {
			pathLibvlc = "vlc/vlc-mac/Contents/MacOS/lib/";
			pathPlugins = "vlc/vlc-mac/Contents/MacOS/plugins/";
			vlcOptions += " --vout=macosx";
		} else if (os.contains("linux")) {
			pathLibvlc = "vlc/vlc-linux/";
			pathPlugins = "vlc/vlc-linux/plugins";
		}

		if (pathLibvlc == null) {
			logger.warn("VLC not supported by this OS. Videos won't load.");
		} else {
			NativeLibrary.addSearchPath("vlc", pathLibvlc);
			System.setProperty("jna.library.path", pathLibvlc);
			System.setProperty("VLC_PLUGIN_PATH", pathPlugins);
		}
		// Looking for in the system
		if (os.contains("win")) {
			String temp = null;
			try {
				temp = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE,
						"Software\\VideoLAN\\VLC", "InstallDir");
				if (temp == null) {
					temp = WinRegistry.readString(
							WinRegistry.HKEY_LOCAL_MACHINE,
							"Software\\Wow6432Node\\VideoLAN\\VLC",
							"InstallDir");
				}
				logger.info("VLC folder: '{}'", temp);
			} catch (Exception e) {
				logger.debug("VLC folder not found in Windows Registry");
			}

			if (temp == null) {
				logger.warn("VLC not installed");
				// not exists, extract
				return;

			}
			pathLibvlc = temp;
			pathPlugins = temp + "\\plugins";
		} else if (os.contains("mac")) {
			String temp = "/Applications/VLC.app";
			if (!new File("/Applications/VLC.app/").exists()) {
				logger.warn("VLC not installed");
				// not exists, extract
				// temp = ....;
			} else {
				logger.info("VLC installed");
			}
			pathLibvlc = temp + "/Contents/MacOS/lib/";
			pathPlugins = temp + "/Contents/MacOS/plugins/";
		} else if (os.contains("linux")) {
			File[] libDirs = new File[] { new File("/usr/lib/vlc"),
					new File("/usr/local/lib/vlc") };
			File libDir = null;
			for (File d : libDirs) {
				if (d.exists()) {
					libDir = d;
					break;
				}
			}
			if (libDir != null) {
				logger.info("VLC installation at {}", libDir);
				pathPlugins = new File(libDir, "plugins").getAbsolutePath();
				pathLibvlc = libDir.getAbsolutePath();
				NativeLibrary.addSearchPath("vlc", pathLibvlc);
				System.setProperty("jna.library.path", pathLibvlc);
				System.setProperty("VLC_PLUGIN_PATH", pathPlugins);
				vlcLoaded = true;
			}
		}
		NativeLibrary.addSearchPath("vlc", pathLibvlc);
		System.setProperty("jna.library.path", pathLibvlc);
		System.setProperty("VLC_PLUGIN_PATH", pathPlugins);
		vlcLoaded = true;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	protected Component getVLCComponent(EAdVideo asset) {
		vlcListener.setCount(asset.isStream() ? -1 : 1);
		started = false;
		path = asset.getUri();
		if (assetHandler != null && !asset.isStream()) {
			path = assetHandler.getTempFilePath(asset.getUri());
		}
		if (asset.isStream()) {
			mediaPlayer.setPlaySubItems(true);
		}
		finished = false;
		return canvas;
	}

	@Override
	public boolean start() {
		if (!started && mediaPlayer != null) {
			String[] mediaOptions = {};
			wasSilence = soundManager.isSilence();
			soundManager.setSilence(true);
			mediaPlayer.prepareMedia(path, mediaOptions);
			mediaPlayer.play();
			started = true;
			return true;
		}
		return false;
	}

	/**
	 * Set the finished flag
	 *
	 * @param b
	 *            The new value for finished
	 */
	public void setFinished(boolean b) {
		if (b)
			soundManager.setSilence(wasSilence);
		this.finished = b;
	}

	public void stop() {
		mediaPlayer.setPlaySubItems(false);
	}

	/**
	 * Set the started flag
	 *
	 * @param b
	 *            The new value for started
	 */
	public void setStarted(boolean b) {
		this.started = b;
	}

	@Override
	public void reset() {
		finished = false;
	}

}
