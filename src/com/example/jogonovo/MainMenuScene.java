package com.example.jogonovo;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.AnimatedSpriteMenuItem;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;

import com.example.jogonovo.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener{
	private MenuScene menuChildScene;
	private final int MENU_PLAY = 0;
	private final int MENU_SOUND = 2;
	private boolean soundOn;
	
	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
		soundOn = true;
		playBGM();
		
	}

	@Override
	public void onBackKeyPressed() {
		this.disposeScene();
		System.exit(0);
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		if(resourcesManager.gameMusic.isPlaying())
		{
			resourcesManager.gameMusic.stop();
			soundOn = false;
		}
	}
	
	private void createBackground()
	{
		attachChild(new Sprite(camera.getWidth() / 2f, camera.getHeight()/2f, resourcesManager.menu_background_region, vbom)
		{
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera)
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		});
	}
	
	private void createMenuChildScene()
	{
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);
		
		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.play_region, vbom), 1.2f, 1);
		//final IMenuItem optionsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_OPTIONS, resourcesManager.options_region, vbom),  1.2f, 1);
		final IMenuItem soundMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_SOUND, resourcesManager.sound_region, vbom),  1.2f, 1);
		
		menuChildScene.addMenuItem(playMenuItem);
		//menuChildScene.addMenuItem(optionsMenuItem);
		menuChildScene.addMenuItem(soundMenuItem);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		playMenuItem.setPosition(playMenuItem.getX(), playMenuItem.getY() - 100);
		//optionsMenuItem.setPosition(optionsMenuItem.getX(), optionsMenuItem.getY() - 110);
		soundMenuItem.setPosition(playMenuItem.getX() - 300, playMenuItem.getY() - 100);

		
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,	float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID())
		{
			case MENU_PLAY:
				if(resourcesManager.gameMusic.isPlaying())
				{
					resourcesManager.gameMusic.stop();
					soundOn = false;
				}
				SceneManager.getInstance().loadGameScene(engine);
				return true;
			case MENU_SOUND:
				if(soundOn)
				{
					soundOn = false;
					resourcesManager.gameMusic.pause();
				}
				else
				{
					soundOn = true;
					resourcesManager.gameMusic.resume();
				}
				return true;
			default:
				return false;
		
		}
	}
	
	private void playBGM()
	{
		if(!resourcesManager.gameMusic.isPlaying())
		{
			resourcesManager.gameMusic.play();
			soundOn = true;
		}
	}

}
