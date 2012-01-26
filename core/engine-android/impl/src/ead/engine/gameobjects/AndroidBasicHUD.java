package ead.engine.gameobjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Vibrator;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ead.common.model.EAdElement;
import ead.common.model.elements.scenes.SceneElementImpl;
import ead.common.params.text.EAdString;
import ead.common.resources.StringHandler;
import ead.engine.AndroidAssetHandler;
import ead.engine.core.game.GameState;
import ead.engine.core.gameobjects.GameObjectManager;
import ead.engine.core.gameobjects.factories.SceneElementGOFactory;
import ead.engine.core.gameobjects.go.GameObject;
import ead.engine.core.gameobjects.huds.TopBasicHUDImpl;
import ead.engine.core.gameobjects.huds.MenuHUD;
import ead.engine.core.input.InputHandler;
import ead.engine.core.input.states.MouseState;
import ead.engine.core.platform.AssetHandler;
import ead.engine.core.platform.EngineConfiguration;
import ead.engine.core.platform.GUI;
import ead.engine.core.platform.rendering.GenericCanvas;
import ead.engine.extra.BitmapCanvas;

@Singleton
public class AndroidBasicHUD extends TopBasicHUDImpl {

	private Paint borderPaint;
	private Path clip;
	private Rect rect;
	private Paint textPaint;
	private Bitmap magGlass;
	private EngineConfiguration platformConfiguration;
	private Vibrator vibrator;
	private boolean vibrate;

	@Inject
	public AndroidBasicHUD(GUI gui, MenuHUD menuHUD,
			SceneElementGOFactory gameObjectFactory, GameState gameState,
			GameObjectManager gameObjectManager, InputHandler inputHandler,
			StringHandler stringHandler,
			EngineConfiguration platformConfiguration,
			AssetHandler assetHandler) {
		super(menuHUD, gameObjectFactory, gameState, inputHandler, stringHandler,
				gui, assetHandler);
		this.platformConfiguration = platformConfiguration;
		this.vibrator = (Vibrator) ((AndroidAssetHandler) assetHandler).getContext().getSystemService(Context.VIBRATOR_SERVICE);
		
		borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		borderPaint.setColor(Color.BLACK);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(8);

		clip = new Path();
		magGlass = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_4444);

		clip.addCircle(100, 100, 100, Path.Direction.CCW);

		rect = new Rect(0, 0, 200, 200);

		textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		textPaint.setColor(0xFFFFFFFF);
		textPaint.setShadowLayer(4f, 0, 0, Color.BLACK);
		textPaint.setTextSize(25);
		textPaint.setTypeface(Typeface.SANS_SERIF);
		textPaint.setTextAlign(Align.CENTER);
		vibrate = true;

		logger.info("New instance");
	}

	@SuppressWarnings("unchecked")
	public void render(GenericCanvas<?> canvas) {
		
		if (inputHandler.checkState(MouseState.LEFT_BUTTON_PRESSED)) {
			BitmapCanvas graphicContext = (BitmapCanvas) canvas
					.getNativeGraphicContext();
			if (inputHandler.getPointerX() != -1 && inputHandler.getPointerX() != -1) {

				int notScaledX, notScaledY; 
				notScaledX = inputHandler.getRawPointerX();
				notScaledY = inputHandler.getRawPointerY();
				
				Canvas c = new Canvas(magGlass);
				c.clipPath(clip);

				c.drawBitmap(
						((BitmapCanvas) graphicContext).getBitmap(),
						new Rect(notScaledX - 50, notScaledY - 50,
								notScaledX + 50, notScaledY + 50), rect, null);

				c.drawPath(clip, borderPaint);
				c.drawCircle(100, 100, 3, borderPaint);

				int magX, magY, textX, textY;
				magX = notScaledX - 100;
				magY = notScaledY - 100;

				if (magX + 100 >= this.platformConfiguration.getWidth()) {
					magX = this.platformConfiguration.getWidth() - 100;
				} else if (magX <= -100) {
					magX = -100;
				}
				if (magY + 100 >= this.platformConfiguration.getHeight()) {
					magY = this.platformConfiguration.getHeight() - 100;
				} else if (magY <= -100) {
					magY = -100;
				}

				textX = magX;
				textY = magY - 10;

				graphicContext.save();
				graphicContext.setMatrix(null);
				graphicContext.drawBitmap(magGlass, magX, magY, null);

				GameObject<? extends EAdElement> go = (GameObject<? extends EAdElement>) inputHandler
						.getGameObjectUnderPointer();

				if (go != null && go.getElement() instanceof EAdElement) {
					EAdString name = gameState.getValueMap().getValue((EAdElement) go.getElement(),
							SceneElementImpl.VAR_NAME);
					if (name != null) {
						graphicContext.drawText(name.toString(), textX, textY, textPaint);
						if (vibrate) activateVibrationFor(50);
					}
					else vibrate = true;
				}

				graphicContext.restore();
			}
		}
	}
	
	private void activateVibrationFor(int milis){

		vibrator.vibrate(milis);
		vibrate = false;		
	}

}