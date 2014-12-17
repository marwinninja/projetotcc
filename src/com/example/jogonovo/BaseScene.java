package com.example.jogonovo;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.example.jogonovo.SceneManager.SceneType;

import android.app.Activity;

public abstract class BaseScene extends Scene 
{
	//VARIAVEIS
	protected Engine engine;
	protected Activity activity;
	protected ResourcesManager resourcesManager;
	protected VertexBufferObjectManager vbom;
	protected Camera camera;
	
	//CONSTRUTOR
	public BaseScene()
	{
		this.resourcesManager = ResourcesManager.getInstance();
		this.engine = resourcesManager.engine;
		this.activity = resourcesManager.activity;
		this.vbom = resourcesManager.vbom;
		this.camera = resourcesManager.camera;
		createScene();
	}
	
	//ABSTRAÇAO - IMPLEMENTA NAS CLASSES FILHAS
	public abstract void createScene();
	
	public abstract void onBackKeyPressed();
	
	public abstract SceneType getSceneType();
	
	public abstract void disposeScene();

}
