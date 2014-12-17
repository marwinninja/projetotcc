package com.example.jogonovo;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Tile extends AnimatedSprite
{
	private int column;
	private int row;
	private TileType tileType;
	
	public enum TileType
	{
		TILE_NORMAL,
		TILE_BLOCK,
		TILE_BONUS,	
		TILE_FINAL,
	}
	
	
	public Tile(int pColumn, int pRow, float pX, float pY, int pTileType, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY, 80, 80, setTileTexture(pTileType), pVertexBufferObjectManager);
		this.column = pColumn;
		this.row = pRow;
		setTileType(pTileType);
		this.setZIndex(0);
		if(tileType == TileType.TILE_BONUS)
		{
			animate(new long[]{100, 100}, 1, 2, true);
		}
		
	}
	
	public int getColumn()
	{
		return this.column;
	}
	
	public int getRow()
	{
		return this.row;
	}
	
	public static ITiledTextureRegion setTileTexture(int pTileType)
	{
		ITiledTextureRegion pTile;
		switch(pTileType)
		{
			case 1:
				pTile = ResourcesManager.getInstance().tile1_region;
				break;
			case 2:
				pTile = ResourcesManager.getInstance().tile2_region;
				break;
			case 3:
				pTile = ResourcesManager.getInstance().tile3_region;
				break;
			case 4:
				pTile = ResourcesManager.getInstance().tile4_region;
				break;
			case 5:
				pTile = ResourcesManager.getInstance().gameobjective_region;
				break;
			default:
				pTile = ResourcesManager.getInstance().tile1_region;
				break;
		}
		return pTile;
	}
	
	public void setTileType(int pTileType)
	{
		switch(pTileType)
		{
			case 1:
				this.tileType = TileType.TILE_NORMAL;
				break;
			case 2:
				this.tileType = TileType.TILE_BLOCK;
				break;
			case 3:
				this.tileType = TileType.TILE_BLOCK;
				break;
			case 4:
				this.tileType = TileType.TILE_BONUS;
				break;
			case 5:
				this.tileType = TileType.TILE_FINAL;
				break;
			default:
				this.tileType = TileType.TILE_NORMAL;
				break;
		}
		
	}
	public TileType getTileType()
	{
		return this.tileType;
	}
	
	public void turnOff()
	{
		if(tileType == TileType.TILE_BONUS)
		{
			if(this.isAnimationRunning())
			{
				stopAnimation(0);
			}
		}
		
		if(tileType == TileType.TILE_FINAL)
		{
			if(this.isAnimationRunning())
			{
				stopAnimation(0);
			}
		}
	}
	
	public boolean isTurnedOff()
	{
		if(this.isAnimationRunning())
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	

}
