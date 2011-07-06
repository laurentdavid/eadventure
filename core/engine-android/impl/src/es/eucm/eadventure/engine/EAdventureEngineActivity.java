package es.eucm.eadventure.engine;

import com.google.inject.Injector;

import es.eucm.eadventure.engine.core.GameController;
import es.eucm.eadventure.engine.core.MouseState;
import es.eucm.eadventure.engine.core.impl.modules.BasicGameModule;
import es.eucm.eadventure.engine.core.platform.AssetHandler;
import es.eucm.eadventure.engine.core.platform.GUI;
import es.eucm.eadventure.engine.core.platform.PlatformConfiguration;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class EAdventureEngineActivity extends Activity {

	private GameController gameController;

	private DisplayMetrics dm;

	private Injector injector;

	private EAdventureSurfaceView surfaceView;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        
        surfaceView = new EAdventureSurfaceView(this);
        setContentView(surfaceView);

        injector = Guice.createInjector(new AndroidModule(), new BasicGameModule());

        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        AndroidPlatformConfiguration config = (AndroidPlatformConfiguration) injector.getInstance(PlatformConfiguration.class);
        config.setWidth(dm.widthPixels);
        config.setHeight(dm.heightPixels);
        config.setFullscreen(true);

        //TODO fix this
        AndroidAssetHandler aah = (AndroidAssetHandler) injector.getInstance(AssetHandler.class);
        aah.setResources(getResources());

        surfaceView.start(injector.getInstance(GUI.class),
        		config,
        		injector.getInstance(MouseState.class));
        
        gameController = injector.getInstance(GameController.class);
        gameController.start();        
    }

	@Override
    protected void onPause() {
		super.onPause();
		gameController.pause();
    }
    
	@Override
    protected void onResume() {
    	super.onResume();
    	gameController.resume();
    }
    
	@Override
	protected void onDestroy() {
        super.onDestroy();
        gameController.stop();

        Runtime r = Runtime.getRuntime();
        r.gc();
    }
	
}