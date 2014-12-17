package com.example.jogonovo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.IPathModifierListener;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.EaseSineInOut;

import com.example.jogonovo.Tile.TileType;

public abstract class Player extends AnimatedSprite
{
	private int column;
	private int row;
	final long[] PLAYER_ANIMATE = new long[] { 150, 150, 150, 150 };
	private Sprite[][] tileMap;
	private Sprite[] tileSequence;
	private int objective = 0;
	private Path tilePath;
	private Queue<MoveType> commandList = new LinkedList<MoveType>();
	private final IPathModifierListener playerMoveListener = new IPathModifierListener()
	{

		@Override
		public void onPathStarted(PathModifier pPathModifier, IEntity pEntity) {
			//ALGO
			
		}

		@Override
		public void onPathWaypointStarted(PathModifier pPathModifier,
				IEntity pEntity, int pWaypointIndex) {
			animate(PLAYER_ANIMATE);
			
		}

		@Override
		public void onPathWaypointFinished(PathModifier pPathModifier,
				IEntity pEntity, int pWaypointIndex) {
			
			if(((Tile) tileSequence[pWaypointIndex+1]).getTileType() == TileType.TILE_BONUS)
			{
				if(!((Tile) tileSequence[pWaypointIndex+1]).isTurnedOff())
				{
					//objective--;
					((GameScene)SceneManager.getInstance().getCurrentScene()).addToObjectives();
					((Tile) tileSequence[pWaypointIndex+1]).turnOff();
					ResourcesManager.getInstance().shutDown.play();
				}
			}
			setTilePosition(((Tile) tileSequence[pWaypointIndex+1]).getColumn(), ((Tile) tileSequence[pWaypointIndex+1]).getRow());
			((GameScene) SceneManager.getInstance().getCurrentScene()).addToMoves();
			stopAnimation(0);
			commandList.remove();
			((GameScene)SceneManager.getInstance().getCurrentScene()).drawCommandList();
		}

		@Override
		public void onPathFinished(PathModifier pPathModifier, IEntity pEntity) {
			//ACABOU CAMINHO
			//VERIFICAR SE HÁ FIM DE JOGO
			if(((Tile) tileSequence[tileSequence.length - 1]).getTileType() == TileType.TILE_FINAL)
			{
				ResourcesManager.getInstance().torneiraOff.play();
				((Tile) tileSequence[tileSequence.length - 1]).turnOff();
				((GameScene) SceneManager.getInstance().getCurrentScene()).levelCompleted();
			}
			else
			{
				((GameScene) SceneManager.getInstance().getCurrentScene()).levelLose(0);
			}
			
		}
	};
	
	public enum MoveType
	{
		MOVE_RIGHT,
		MOVE_DOWN,
		MOVE_UP,
		MOVE_LEFT
	}
	
	//CONSTRUCTOR
	public Player(int pColumn, int pRow, float pX, float pY, VertexBufferObjectManager vbo, Camera camera)
	{
		super(pX, pY, ResourcesManager.getInstance().player_region, vbo);
		this.setZIndex(2);
		this.setTilePosition(pColumn, pRow);
	}	
	
	public void walkAlongPath(Path pPath)
	{
		
		final PathModifier movimento = new PathModifier(0.8f * tileSequence.length, pPath, playerMoveListener, EaseSineInOut.getInstance());
		this.clearEntityModifiers();
		this.registerEntityModifier(movimento);
	}
	
	
	public float convertColumnToX(int pColumn)
	{
		return 80*pColumn - 40;
	}
	
	public float convertRowToY(int pRow)
	{
		return 32 + 80*pRow - 40;
	}
	
	public int getColumn()
	{
		return this.column;
	}
	
	public int getRow()
	{
		return this.row;
	}
	
	public void setTilePosition(int pColumn, int pRow)
	{
		this.column = pColumn;
		this.row = pRow;
	}
	
	public void setTileMap(Sprite[][] pTileMap)
	{
		this.tileMap = pTileMap.clone();
	}
	
	public void addCommand(MoveType pMoveType)
	{
		if(commandList.size() <= 16)
		{
			commandList.add(pMoveType);
		}
	}
	
	public void constructPath()
	{
		tilePath = new Path(commandList.size()+1);
		tileSequence = new Sprite[commandList.size()+1];
		int pIndex = 1;
		tileSequence[0] = tileMap[this.column - 1][this.row - 1];
		tilePath.to(this.getX(), this.getY());
		Iterator<MoveType> iterator = commandList.iterator();
		while(iterator.hasNext())
		{
			checkNextTile(pIndex, iterator.next());
			pIndex++;
		}
		walkAlongPath(tilePath);
		//commandList.clear();
		
	}
	
	public void checkNextTile(int pIndex, MoveType pMoveType)
	{
		int previousTileColumn = ((Tile)tileSequence[pIndex - 1]).getColumn();
		int previousTileRow = ((Tile)tileSequence[pIndex - 1]).getRow();
		switch(pMoveType)
		{
		case MOVE_UP:
			if(previousTileRow < 5)
			{
				tileSequence[pIndex] = tileMap[previousTileColumn - 1][previousTileRow - 1 +1];
			}
			else
			{
				tileSequence[pIndex] = tileSequence[pIndex - 1];
			}
			break;
		case MOVE_DOWN:
			if(previousTileRow > 1)
			{
				tileSequence[pIndex] = tileMap[previousTileColumn - 1][previousTileRow - 1 -1];
			}
			else
			{
				tileSequence[pIndex] = tileSequence[pIndex - 1];
			}
			break;
		case MOVE_LEFT:
			if(previousTileColumn > 1)
			{
				tileSequence[pIndex] = tileMap[previousTileColumn - 1 - 1][previousTileRow - 1];
			}
			else
			{
				tileSequence[pIndex] = tileSequence[pIndex - 1];
			}
			break;
		case MOVE_RIGHT:
			if(previousTileColumn < 7)
			{
				tileSequence[pIndex] = tileMap[previousTileColumn - 1 + 1][previousTileRow - 1];
			}
			else
			{
				tileSequence[pIndex] = tileSequence[pIndex - 1];
			}
			break;
		}
		if(tileSequence[pIndex] != null && ((Tile) tileSequence[pIndex]).getTileType() != TileType.TILE_BLOCK)
		{
			tilePath.to(tileSequence[pIndex].getX(), tileSequence[pIndex].getY());
		}
		else
		{
			tilePath.to(pIndex-1);
			tileSequence[pIndex] = tileSequence[pIndex - 1];
		}
	}
	
	public int getObjective()
	{
		return this.objective;
	}
	
	public void setObjective(int pObjective)
	{
		this.objective = pObjective;
		System.out.println("Objetivos do jogador: " + objective);
	}
	
	public void clearCommandList()
	{
		this.commandList.clear();
	}

	public Iterator<MoveType> commandListIterator()
	{
		return commandList.iterator();
	}
	
}
