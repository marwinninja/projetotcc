package com.example.jogonovo;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.engine.camera.Camera;

import com.example.jogonovo.SceneManager.SceneType;

public class SplashScene extends BaseScene 
{
	private Sprite splash;

	@Override
	public void createScene() {
		splash = new Sprite(camera.getWidth()/2f, camera.getHeight()/2f, resourcesManager.splash_region, vbom)
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		
		splash.setScale(1.5f);
		splash.setPosition(400, 240 );
		attachChild(splash);
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SPLASH;
	}

	@Override
	public void disposeScene() {
		splash.detachSelf();
		splash.dispose();
		this.detachSelf();
		this.dispose();
	}
	

}
