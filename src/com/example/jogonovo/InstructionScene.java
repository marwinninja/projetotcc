package com.example.jogonovo;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.color.Color;

import com.example.jogonovo.SceneManager.SceneType;

public class InstructionScene extends BaseScene implements IOnSceneTouchListener
{
	private Text instructionText;
	private final String instruction = "Ajude a Aluna a economizar agua e energia eletrica usando logica computacional! \nSelecione a sequencia de comandos a ser seguida, e toque em Executar Comandos\n E necessario desligar todos os aparelhos eletronicos antes de fechar a torneira!";
	
	@Override
	public void createScene() {
		this.loadBackground();
		this.showInstructions();
		this.setOnSceneTouchListener(this);
		
	}

	@Override
	public void onBackKeyPressed() {
		this.disposeScene();
		SceneManager.getInstance().loadMenuScene(engine);
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		this.detachSelf();
		this.disposeScene();
		
	}
	
	private void loadBackground()
	{
		this.setBackground(new Background(Color.WHITE));
	}
	
	private void showInstructions()
	{
		instructionText = new Text(0, 0, resourcesManager.font, instruction, vbom);
		instructionText.setPosition(camera.getWidth()/2, (camera.getHeight()/2) + 120);
		instructionText.setText(instruction);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(pSceneTouchEvent.isActionUp())
		{
			SceneManager.getInstance().loadGameScene(engine);
		}
		return true;
	}

}
