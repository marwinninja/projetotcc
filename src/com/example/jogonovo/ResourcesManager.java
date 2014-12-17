package com.example.jogonovo;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;


public class ResourcesManager 
{
	//VARIAVEIS
	//CLASSE SINGLETON QUE GUARDA OS RECURSOS DO JOGO
	
	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	public Engine engine;
	public MainActivity activity;
	public Camera camera;
	public VertexBufferObjectManager vbom;
	
	//TEXTURAS
	public ITextureRegion splash_region;
	private BitmapTextureAtlas splashTextureAtlas;
	
	public ITextureRegion menu_background_region;
	public ITextureRegion play_region;
	public ITextureRegion options_region;
	public ITiledTextureRegion sound_region;
	
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	
	public Font font;
	
	//TEXTURAS DO JOGO
	public BuildableBitmapTextureAtlas gameTextureAtlas;
	public BitmapTextureAtlas gameBackgroundTextureAtlas;
	//Regiões da textura do jogo
	public ITiledTextureRegion player_region;
	
	public ITiledTextureRegion tile1_region;
	public ITiledTextureRegion tile2_region;
	public ITiledTextureRegion tile3_region;
	public ITiledTextureRegion tile4_region;
	public ITextureRegion gamebg_region;
	public ITiledTextureRegion gameobjective_region;
	
	//
	
	//AUDIO DO JOGO
	public Sound buttonClick;
	public Sound shutDown;
	public Sound torneiraOff;
	public Music gameMusic;
	public Music pingoTorneira;
	
	
	//
	
	//MENU DE COMANDOS
	public ITextureRegion moveup_region;
	public ITextureRegion movedown_region;
	public ITextureRegion moveleft_region;
	public ITextureRegion moveright_region;
	public ITextureRegion execute_region;
	public ITextureRegion clear_region;
	//
	public ITextureRegion arrowup_region;
	public ITextureRegion arrowdown_region;
	public ITextureRegion arrowleft_region;
	public ITextureRegion arrowright_region;
	
	public ITextureRegion list_region;
	
	////TEXTURAS DAS INSTRUÇÕES
	public BuildableBitmapTextureAtlas instructionTextureAtlas;
	public ITextureRegion instructionlabel_region;
	public ITextureRegion instructiontext_region;
	
	///TEXTURAS DO FIM DE JOGO
	public BuildableBitmapTextureAtlas gameOverTextureAtlas;
	public ITextureRegion winlabel_region;
	public ITextureRegion gameover1_region;
	public ITextureRegion gameover2_region;
	
	
	public void loadMenuResources()
	{
		loadMenuGraphics();
		loadMenuAudio();
		loadMenuFonts();
	}
	
	public void loadGamesResources()
	{
		loadGameGraphics();
		loadGameAudio();
		loadWinLoseTextures();
		//loadGameFonts();
		loadInstructionTextures();
		
	}
	
	
	
	public void loadInstructionTextures()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		instructionTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		instructionlabel_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(instructionTextureAtlas, activity, "instruction_label.png");
		instructiontext_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(instructionTextureAtlas, activity, "instructiontext.png");
		try
		{
			this.instructionTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.instructionTextureAtlas.load();
		}
		catch(final TextureAtlasBuilderException e)
		{
			Debug.e(e);			
		}
		
	}
	
	public void loadWinLoseTextures()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		gameOverTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		winlabel_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameOverTextureAtlas, activity, "wingame.png");
		gameover1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameOverTextureAtlas, activity, "gameover1.png");
		gameover2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameOverTextureAtlas, activity, "gameover2.png");
		
		try
		{
			gameOverTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			gameOverTextureAtlas.load();
		}
		catch(final TextureAtlasBuilderException e)
		{
			Debug.e(e);			
		}
		
	}
	
	public void unloadMenuTextures()
	{
		menuTextureAtlas.unload();
	}
	
	public void loadMenuTextures()
	{
		menuTextureAtlas.load();
	}
	
	private void loadMenuGraphics()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_background.png");
		play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "jogar.png");
		options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "options.png");
		sound_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(menuTextureAtlas, activity, "soundbutton.png", 2, 1);
		try
		{
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		}
		catch(final TextureAtlasBuilderException e)
		{
			Debug.e(e);			
		}
	}
	
	private void loadMenuAudio()
	{
		MusicFactory.setAssetBasePath("sfx/");
		try
		{
			gameMusic = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "maintrack.wav");
			gameMusic.setLooping(true);
			gameMusic.setVolume(0.4f);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void loadGameGraphics()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		gameBackgroundTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		tile1_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "tile1.png", 1, 1);
		tile2_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "tile2.png", 1, 1);
		tile3_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "tile3.png", 1, 1);
		tile4_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "tile4.png", 3, 1);
		player_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "player.png", 5, 1);
		gameobjective_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "torneira.png", 5, 1);
		gamebg_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameBackgroundTextureAtlas, activity, "gamebg.png", 0, 0);
		moveup_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "moveup.png");
		moveleft_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "moveleft.png");
		moveright_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "moveright.png");
		movedown_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "movedown.png");
		execute_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "execute.png");
		arrowup_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "arrowup.png");
		arrowdown_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "arrowdown.png");
		arrowleft_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "arrowleft.png");
		arrowright_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "arrowright.png");
		list_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "list.png");
		clear_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "clear.png");
		try
		{
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameTextureAtlas.load();
			this.gameBackgroundTextureAtlas.load();
		}
		catch (final TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		
	}
	
	private void loadGameFonts()
	{
		
	}
	
	private void loadMenuFonts()
	{
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256 , 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		font = FontFactory.createFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "loadingfont.ttf", 20, true, Color.BLACK);
		font.load();
	}
	
	private void loadGameAudio()
	{
		SoundFactory.setAssetBasePath("sfx/");
		MusicFactory.setAssetBasePath("sfx/");
		try
		{
			buttonClick = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "click.wav");
			shutDown = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "shutdown.wav");
			shutDown.setVolume(0.5f);
			torneiraOff = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "torneira.wav");
			torneiraOff.setVolume(0.5f);
			pingoTorneira = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity, "dripping.wav");
			pingoTorneira.setVolume(0.3f);
			pingoTorneira.setLooping(true);
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadSplashScreen()
	{
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		splashTextureAtlas.load();
		
	}
	public void unloadSplashScreen()
	{
		splashTextureAtlas.unload();
		splash_region = null;
	}
	
	public void unloadGameTextures()
	{
		gameTextureAtlas.unload();
		gameBackgroundTextureAtlas.unload();
		instructionTextureAtlas.unload();
		
		player_region = null;
		
		tile1_region = null;
		tile2_region = null;
		tile3_region = null;
		tile4_region = null;
		gamebg_region = null;
		gameobjective_region = null;
		//
		//MENU DE COMANDOS
		moveup_region = null;
		movedown_region = null;
		moveleft_region = null;
		moveright_region = null;
		execute_region = null;
		
	}
	public static void prepareManager(Engine engine, MainActivity activity, Camera camera, VertexBufferObjectManager vbom)
	{
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
	}
	
	//GETTER AND SETTER
	public static ResourcesManager getInstance()
	{
		return INSTANCE;
	}

}
