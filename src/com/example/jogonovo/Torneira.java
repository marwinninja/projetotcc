package com.example.jogonovo;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.example.jogonovo.Tile.TileType;

public class Torneira extends Tile
{
	private int column;
	private int row;
	private TileType tileType;
	
	
	public Torneira(int pColumn, int pRow, float pX, float pY, int pTileType, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pColumn, pRow, pX, pY, pTileType, pVertexBufferObjectManager);
		this.animate(new long[] {800, 100, 100, 100, 100}, true);
		this.tileType = TileType.TILE_FINAL;
	}

	
	public int getColumn()
	{
		return this.column;
	}
	
	public int getRow()
	{
		return this.row;
	}
	
	public TileType getTileType()
	{
		return this.tileType;
	}
	
}
