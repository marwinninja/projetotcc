package com.example.jogonovo;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.adt.color.Color;

import com.example.jogonovo.SceneManager.SceneType;

public class LoadingScene extends BaseScene {

	@Override
	public void createScene() {
		setBackground(new Background(Color.GREEN));
		attachChild(new Text(camera.getWidth()/2, camera.getHeight()/2, resourcesManager.font, "Loading...", vbom));
				
	}

	@Override
	public void onBackKeyPressed() {
		return;
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOADING;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}

}
