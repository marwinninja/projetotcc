package com.example.jogonovo;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.andengine.audio.music.Music;
import org.andengine.audio.sound.Sound;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.util.SAXUtils;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
import org.andengine.util.level.simple.SimpleLevelLoader;
import org.xml.sax.Attributes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.example.jogonovo.Player.MoveType;
import com.example.jogonovo.SceneManager.SceneType;
import com.example.jogonovo.Tile.TileType;

public class GameScene extends BaseScene implements IOnSceneTouchListener
{
	private HUD gameHUD;
	private Text movesText;
	private int moves = 0;
	private int objectives = 0;
	private Text objectivesText;
	private PhysicsWorld physicsWorld;
	private Player player;
	private int minObjectives = 0;
	private int timeStamp = 0;
	private Text timeStampText;
	
	private  Sound click;
	
	
	private List<IEntity> myEntitiesList;
	private List<Sprite> arrowList;
	
	//PARAMETROS PARA O LEVEL LOADER
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_TILE1 = "tile1";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_TILE2 = "tile2";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_TILE3 = "tile3";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_TILE4 = "tile4";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_TORNEIRA = "torneira";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER = "player";
	
	private Sprite[][] tileMap;
	
	//COMANDOS DO JOGADOR
	private final int UP = 0;
	private final int DOWN = 1;
	private final int LEFT = 2;
	private final int RIGHT = 3;
	private final int EXECUTE = 4;
	private final int CLEAR = 5;
	
	private final TimerHandler mTime = new TimerHandler(1f, true, new ITimerCallback()
	{
		@Override
		public void onTimePassed(TimerHandler pTimerHandler)
		{
			addTime();
			pTimerHandler.reset();
			
		}
	});
	
	private Sprite list;
	
	/////CENA DE INSTRUÇÕES
	private CameraScene mInstructionsScene;
	
	////CENA DE FIM DE JOGO
	private CameraScene mGameOverScene;
		
	@Override
	public void createScene() {
		tileMap = new Sprite[7][5];
		myEntitiesList = new ArrayList<IEntity>();
		arrowList = new ArrayList<Sprite>();
		createBackground();
		createPhysics();
		createAudio();
		loadLevel(SceneManager.getInstance().getCurrentLevel());//loadLevel(3);
		loadPlayerTileMap();
		System.out.println("Objetivos atuais: "+  minObjectives);
		player.setObjective(minObjectives);
		createInstructionScene();
		
	}

	@Override
	public void onBackKeyPressed() {
		if(resourcesManager.pingoTorneira.isPlaying())
		{
			resourcesManager.pingoTorneira.stop();
		}
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		//camera.setCenter(camera.getWidth()/2, camera.getHeight()/2);	
		//TODO Code responsible for disposing scene
		//remove all game scene objects
	}
	
	private void createBackground()
	{
		Sprite backgroundSprite = new Sprite(camera.getWidth()/2, camera.getHeight()/2, resourcesManager.gamebg_region, vbom);
		backgroundSprite.setZIndex(-1);
		attachChild(backgroundSprite);
	}
	
	private void createAudio()
	{
		click = resourcesManager.buttonClick;
	}
	
	private void createHUD()
	{
		gameHUD = new HUD();	
		playDripping();
		
		//CRIA TEXTO PARA EXIBIR QUANTIDADE DE MOVIMENTOS
		movesText = new Text(camera.getWidth(), camera.getHeight(), resourcesManager.font, "Movimentos: 0123456789", new TextOptions(HorizontalAlign.RIGHT), vbom);
		movesText.setAnchorCenter(1f, 1f);
		movesText.setPosition(camera.getWidth() - 15, camera.getHeight()-15);
		movesText.setText("Movimentos: 0");
		gameHUD.attachChild(movesText);
		
		
		//CRIA TEXTO PARA NUMERO DE OBJETIVOS
		objectivesText = new Text(camera.getWidth(), camera.getHeight(), resourcesManager.font, "Objetivos: 0123456789", new TextOptions(HorizontalAlign.RIGHT), vbom);
		objectivesText.setAnchorCenter(1f, 1f);
		objectivesText.setPosition(camera.getWidth() - 15, movesText.getY() - movesText.getHeight());
		objectivesText.setText("Objetivos: 0");
		gameHUD.attachChild(objectivesText);
		
		timeStampText = new Text(camera.getWidth(), camera.getHeight(), resourcesManager.font, "Tempo: 0123456789", new TextOptions(HorizontalAlign.RIGHT), vbom);
		timeStampText.setAnchorCenter(1f,  1f);
		timeStampText.setPosition(camera.getWidth() - 15,  objectivesText.getY() - objectivesText.getHeight());
		timeStampText.setText("Tempo: 00:00");
		gameHUD.attachChild(timeStampText);
		
		
		
		final IMenuItem moveUpItem = new ScaleMenuItemDecorator(new SpriteMenuItem(UP, resourcesManager.moveup_region, vbom), 1.1f, 1)
		{
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
			{
				if(touchEvent.isActionDown())
				{
					onMenuItemSelected(this);
					playButtonSound();
				}
				if(touchEvent.isActionUp())
				{
					if(!player.isAnimationRunning())
					{
						player.addCommand(MoveType.MOVE_UP);
						drawCommandList();
						//commandList.add(MoveType.MOVE_UP);
						onMenuItemUnselected(this);
					}
				}
				
				return true;
			}
		};
		final IMenuItem moveDownItem = new ScaleMenuItemDecorator(new SpriteMenuItem(DOWN, resourcesManager.movedown_region, vbom),  1.1f, 1)
		{
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
			{
				
				if(touchEvent.isActionDown())
				{
					onMenuItemSelected(this);
					playButtonSound();
				}
				if(touchEvent.isActionUp())
				{
					if(!player.isAnimationRunning())
					{
						player.addCommand(MoveType.MOVE_DOWN);
						drawCommandList();
						//commandList.add(MoveType.MOVE_DOWN);
						onMenuItemUnselected(this);
					}
				}
				
				return true;
			}
		};
		final IMenuItem moveLeftItem = new ScaleMenuItemDecorator(new SpriteMenuItem(LEFT, resourcesManager.moveleft_region, vbom), 1.1f, 1)
		{
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
			{
				if(touchEvent.isActionDown())
				{
					onMenuItemSelected(this);
					playButtonSound();
				}
				if(touchEvent.isActionUp())
				{
					if(!player.isAnimationRunning())
					{
						player.addCommand(MoveType.MOVE_LEFT);
						drawCommandList();
						//commandList.add(MoveType.MOVE_LEFT);
						onMenuItemUnselected(this);
					}
				}
				
				return true;
			}
		};
		final IMenuItem moveRightItem = new ScaleMenuItemDecorator(new SpriteMenuItem(RIGHT, resourcesManager.moveright_region, vbom),  1.1f, 1)
		{
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
			{
				if(touchEvent.isActionDown())
				{
					onMenuItemSelected(this);
					playButtonSound();
				}
				if(touchEvent.isActionUp())
				{
					if(!player.isAnimationRunning())
					{
						player.addCommand(MoveType.MOVE_RIGHT);
						drawCommandList();
						//commandList.add(MoveType.MOVE_RIGHT);
						onMenuItemUnselected(this);
					}
				}
				
				return true;
			}
		};
		final IMenuItem executeItem = new ScaleMenuItemDecorator(new SpriteMenuItem(EXECUTE, resourcesManager.execute_region, vbom), 1.1f, 1)
		{
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
			{
				if(touchEvent.isActionDown())
				{
					onMenuItemSelected(this);
					playButtonSound();
				}
				if(touchEvent.isActionUp())
				{
					if(!player.isAnimationRunning())
					{
						player.constructPath();
						onMenuItemUnselected(this);
					}
				}
				
				return true;
			}
		};
		
		final IMenuItem clearItem = new ScaleMenuItemDecorator(new SpriteMenuItem(CLEAR, resourcesManager.clear_region, vbom), 1.1f, 1)
		{
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y)
			{
				if(touchEvent.isActionDown())
				{
					onMenuItemSelected(this);
					playButtonSound();
				}
				if(touchEvent.isActionUp())
				{
					if(!player.isAnimationRunning())
					{
						player.clearCommandList();
						drawCommandList();
						onMenuItemUnselected(this);
					}
				}
				
				return true;
			}
		};
		
		moveUpItem.setAnchorCenter(1f, 1f);
		moveDownItem.setAnchorCenter(1f, 1f);
		moveLeftItem.setAnchorCenter(1f, 1f);
		moveRightItem.setAnchorCenter(1f, 1f);
		executeItem.setAnchorCenter(1f, 1f);
		clearItem.setAnchorCenter(1f,  1f);
		
		gameHUD.attachChild(moveUpItem);
		gameHUD.attachChild(moveDownItem);
		gameHUD.attachChild(moveLeftItem);
		gameHUD.attachChild(moveRightItem);
		gameHUD.attachChild(executeItem);
		gameHUD.attachChild(clearItem);
		
		moveUpItem.setPosition(movesText.getX() - 15, objectivesText.getY() - 60);
		moveDownItem.setPosition(movesText.getX() - 15, moveUpItem.getY() - moveUpItem.getHeight() - 20);
		moveLeftItem.setPosition(movesText.getX() - 15, moveDownItem.getY() - moveDownItem.getHeight() - 20);
		moveRightItem.setPosition(movesText.getX() - 15, moveLeftItem.getY() - moveLeftItem.getHeight() - 20);
		clearItem.setPosition(movesText.getX() - 15, moveRightItem.getY() - moveRightItem.getHeight() - 50);
		executeItem.setPosition(clearItem.getX() - clearItem.getWidth() - 10, clearItem.getY());
		
		list = new Sprite(moveUpItem.getX() - moveUpItem.getWidth()- 110, (moveUpItem.getY() + moveRightItem.getY())/2 - 60, resourcesManager.list_region, vbom);
		gameHUD.attachChild(list);
		
		gameHUD.registerTouchArea(moveUpItem);
		gameHUD.registerTouchArea(moveDownItem);
		gameHUD.registerTouchArea(moveLeftItem);
		gameHUD.registerTouchArea(moveRightItem);
		gameHUD.registerTouchArea(executeItem);
		gameHUD.registerTouchArea(clearItem);
		
		this.registerUpdateHandler(mTime);
		
		camera.setHUD(gameHUD);
	}
	
	public void addToMoves()
	{
		moves++;
		movesText.setText("Movimentos: " + moves);
	}
	
	private void createPhysics()
	{
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, -17), false);
		registerUpdateHandler(physicsWorld);
	}
	
	private void loadLevel(int levelID)
	{
		final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
		//final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
		
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL) 
				{
					public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException
					{	
						final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
						final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
						//TODO Talvez use
						return GameScene.this;
					}
		});
		
		levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY)
		{
			public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException
			{
				final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
				final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
				final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
				
				final Sprite levelObject;
				
				if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_TILE1))
				{
					levelObject = new Tile(x, y, convertColumnToX(x), convertRowToY(y), 1, vbom);
					addToTileMap(x, y, levelObject);
				}
				
				else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_TILE2))
				{
					levelObject = new Tile(x, y, convertColumnToX(x), convertRowToY(y) , 2, vbom);
					addToTileMap(x, y, levelObject);
				}
				else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_TILE3))
				{
					levelObject = new Tile(x, y, convertColumnToX(x), convertRowToY(y), 3, vbom);
					addToTileMap(x, y, levelObject);
				}
				else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_TILE4))
				{
					levelObject = new Tile(x, y, convertColumnToX(x), convertRowToY(y), 4, vbom);
					addToTileMap(x, y, levelObject);
					minObjectives++;
				}
				else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_PLAYER))
				{
					player = new Player(x, y, convertColumnToX(x), convertRowToY(y), vbom, camera){};
					levelObject = player;
				}
				else if(type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_TORNEIRA))
				{
					levelObject = new Torneira(x, y, convertColumnToX(x), convertRowToY(y), 5, vbom);
					addToTileMap(x, y, levelObject);
				}
				
				else
				{
					throw new IllegalArgumentException();
				}
				
				levelObject.setCullingEnabled(true);
				myEntitiesList.add(levelObject);
				
				return levelObject;
			}
		});
		levelLoader.loadLevelFromAsset(activity.getAssets(), "level/" + levelID + ".lvl");
	}
	
	public float convertColumnToX(int pColumn)
	{
		return 80*pColumn - 40;
	}
	
	public float convertRowToY(int pRow)
	{
		return 32 + 80*pRow - 40;
	}
	
	public void addToTileMap(int pColumn, int pRow, Sprite pSprite)
	{
		tileMap[pColumn - 1][pRow - 1] = pSprite;
	}
	
	public void loadPlayerTileMap()
	{
		player.setTileMap(tileMap);
	}
	
	public void addToObjectives()
	{
		objectives++;
		objectivesText.setText("Objetivos: " + objectives);
	}
	
	public void removeObjective()
	{
		objectives--;
		objectivesText.setText("Objetivos: "+objectives);
	}
	
	public void addTime()
	{
		int minuto, segundo = 0;
		timeStamp++;
		minuto = timeStamp / 60;
		segundo = timeStamp % 60;
		String tempo = String.format("%02d:%02d", minuto, segundo);
		timeStampText.setText("Tempo: " + tempo);
	}
	
	public void levelCompleted()
	{
		this.unregisterUpdateHandler(mTime);
		timeStamp = 0;
		System.out.println("Valor de objective ao fim do jogo: "+ objectives + "\nValor de minObjectives ao fim do jogo: " + minObjectives);
		if(resourcesManager.pingoTorneira.isPlaying())
		{
			resourcesManager.pingoTorneira.stop();
		}
		
		if(objectives == player.getObjective())
		{
			levelWin();
		}
		else
		{
			levelLose(1);
		}
		
	}
	
	public void levelWin()
	{
		camera.setHUD(null);
		mGameOverScene = new CameraScene(camera);
		final Sprite winLabel = new Sprite(camera.getWidth()/2, camera.getHeight()/2, resourcesManager.winlabel_region, vbom);
		winLabel.setZIndex(10);
		mGameOverScene.attachChild(winLabel);
		
		mGameOverScene.setBackgroundEnabled(false);
		mGameOverScene.setOnSceneTouchListener(new IOnSceneTouchListener() {
	         
	        @Override
	        public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
	            if (pSceneTouchEvent.isActionUp()) {
	            	SceneManager.getInstance().advanceLevel();
	            	clearChildScene();
	            	SceneManager.getInstance().restartScene();
	            }
	            return true;
	        }
	    });
		
		setChildScene(mGameOverScene, false, true, true);
		
		/*Text winText = new Text(camera.getWidth() / 2, camera.getHeight()/2, resourcesManager.font, "PARABENS, VOCE VENCEU!", vbom);
		winText.setZIndex(5);
		attachChild(winText);
		setOnSceneTouchListener(this);*/
	}
	
	public void levelLose(int loseType)
	{
		camera.setHUD(null);
		mGameOverScene = new CameraScene(camera);
		if(loseType == 0)
		{
			final Sprite loseLabel = new Sprite(camera.getWidth()/2, camera.getHeight()/2, resourcesManager.gameover1_region, vbom);
			loseLabel.setZIndex(10);
			mGameOverScene.attachChild(loseLabel);
		}
		if(loseType == 1)
		{
			final Sprite loseLabel = new Sprite(camera.getWidth()/2, camera.getHeight()/2, resourcesManager.gameover2_region, vbom);
			loseLabel.setZIndex(10);
			mGameOverScene.attachChild(loseLabel);
		}
		
		mGameOverScene.setBackgroundEnabled(false);
		mGameOverScene.setOnSceneTouchListener(new IOnSceneTouchListener() {
	         
	        @Override
	        public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
	            if (pSceneTouchEvent.isActionUp()) {
	            	clearChildScene();
	            	SceneManager.getInstance().restartScene();
	            }
	            return true;
	        }
	    });
		
		setChildScene(mGameOverScene, false, true, true);
	}
	
	public void cleanEntities()
	{	
		for (IEntity entity: myEntitiesList)
		{
			entity.clearEntityModifiers();
			entity.clearUpdateHandlers();
			entity.detachSelf();
			
			if (!entity.isDisposed())
			{
				entity.dispose();
			}
		}
		
		myEntitiesList.clear();
		myEntitiesList = null;
		tileMap = null;
		//gameHUD.detachChildren();
		//gameHUD = null;
	}
	
	public void clearScene()
	{
		engine.runOnUpdateThread(new Runnable()
		{
			@Override
			public void run()
			{
				disposeScene();
				cleanEntities();
				clearTouchAreas();
				clearUpdateHandlers();
				System.gc();
			}
		});
	}
	
	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if(pSceneTouchEvent.isActionUp())
		{
			SceneManager.getInstance().restartScene();
		}
		return true;
	}
	private void createInstructionScene()
	{
		mInstructionsScene = new CameraScene(camera);
		final Sprite instructionlabel = new Sprite(camera.getWidth()/2, camera.getHeight() - (resourcesManager.instructionlabel_region.getHeight()/2), resourcesManager.instructionlabel_region, vbom);
		instructionlabel.setZIndex(10);
		mInstructionsScene.attachChild(instructionlabel);
		
		final Sprite instructiontext = new Sprite(instructionlabel.getX(), instructionlabel.getY() - (resourcesManager.instructiontext_region.getHeight()/2) - 60, resourcesManager.instructiontext_region, vbom);
		instructiontext.setZIndex(10);
		mInstructionsScene.attachChild(instructiontext);
		mInstructionsScene.setBackgroundEnabled(false);
		mInstructionsScene.setOnSceneTouchListener(new IOnSceneTouchListener() {
	         
	        @Override
	        public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
	            if (pSceneTouchEvent.isActionUp()) {
	                clearChildScene();
	        		createHUD();
	        		//ADICIONA SOM DE FUNDO
	        		//playDripping();
	        		//
	            }
	            return true;
	        }
	    });
		
		setChildScene(mInstructionsScene, false, true, true);
		
	}
	
	public void drawCommandList()
	{
		eraseArrows();
		Iterator<MoveType> iterator = player.commandListIterator();
		Sprite lastSprite = null;
		while(iterator.hasNext())
		{
			switch(iterator.next())
			{
				case MOVE_UP:
					if(lastSprite != null)
					{
						final Sprite arrowup = new Sprite(list.getX(), lastSprite.getY() + (lastSprite.getHeight()/2) + (resourcesManager.arrowup_region.getHeight()/2)+5, resourcesManager.arrowup_region, vbom);
						gameHUD.attachChild(arrowup);
						lastSprite = arrowup;
					}
					else
					{
						final Sprite arrowup = new Sprite(list.getX(), list.getY() - (list.getHeight()/2) + (resourcesManager.arrowup_region.getHeight()/2) + 15, resourcesManager.arrowup_region, vbom);
						gameHUD.attachChild(arrowup);
						lastSprite = arrowup;
					}
					arrowList.add(lastSprite);
					break;
				case MOVE_DOWN:
					if(lastSprite != null)
					{
						final Sprite arrowdown = new Sprite(list.getX(), lastSprite.getY() + (lastSprite.getHeight()/2) + (resourcesManager.arrowdown_region.getHeight()/2)+5, resourcesManager.arrowdown_region, vbom);
						gameHUD.attachChild(arrowdown);
						lastSprite = arrowdown;
					}
					else
					{
						final Sprite arrowdown = new Sprite(list.getX(), list.getY() - (list.getHeight()/2) + (resourcesManager.arrowdown_region.getHeight()/2) + 15, resourcesManager.arrowdown_region, vbom);
						gameHUD.attachChild(arrowdown);
						lastSprite = arrowdown;
					}
					arrowList.add(lastSprite);
					break;
				case MOVE_LEFT:
					if(lastSprite != null)
					{
						final Sprite arrowleft = new Sprite(list.getX(), lastSprite.getY() + (lastSprite.getHeight()/2) + (resourcesManager.arrowleft_region.getHeight()/2)+5, resourcesManager.arrowleft_region, vbom);
						gameHUD.attachChild(arrowleft);
						lastSprite = arrowleft;
					}
					else
					{
						final Sprite arrowleft = new Sprite(list.getX(), list.getY() - (list.getHeight()/2) + (resourcesManager.arrowleft_region.getHeight()/2) + 15, resourcesManager.arrowleft_region, vbom);
						gameHUD.attachChild(arrowleft);
						lastSprite = arrowleft;
					}
					arrowList.add(lastSprite);
					break;
				case MOVE_RIGHT:
					if(lastSprite != null)
					{
						final Sprite arrowright = new Sprite(list.getX(), lastSprite.getY() + (lastSprite.getHeight()/2) + (resourcesManager.arrowright_region.getHeight()/2) +5, resourcesManager.arrowright_region, vbom);
						gameHUD.attachChild(arrowright);
						lastSprite = arrowright;
					}
					else
					{
						final Sprite arrowright = new Sprite(list.getX(), list.getY() - (list.getHeight()/2) + (resourcesManager.arrowright_region.getHeight()/2) + 15, resourcesManager.arrowright_region, vbom);
						gameHUD.attachChild(arrowright);
						lastSprite = arrowright;
					}
					arrowList.add(lastSprite);
					break;
			}
		}
	}
	
	public void eraseArrows()
	{
		if(!arrowList.isEmpty())
		{
			Iterator<Sprite> iterator = arrowList.iterator();
			while(iterator.hasNext())
			{
				iterator.next().detachSelf();
				iterator.remove();
			}
		}
	}

	public void playButtonSound()
	{
		click.setVolume(0.4f);
		click.play();
	}
	
	public void playDripping()
	{
		if(!resourcesManager.pingoTorneira.isPlaying())
		{
			resourcesManager.pingoTorneira.play();
		}
	}

}
