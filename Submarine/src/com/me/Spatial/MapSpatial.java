package com.me.Spatial;

import com.badlogic.gdx.math.Vector2;

public class MapSpatial {
	private int index;
	private Vector2 pos;
	private Vector2 size;
	
	public MapSpatial(Vector2 setpos, int setindex, Vector2 setsize){
		pos = new Vector2(setpos);
		index = setindex;
		size = new Vector2(setsize);
	}
	
	public int getIndex(){
		return index;
	}
	
	public float getX(){
		return pos.x;
	}
	
	public float getY(){
		return pos.y;
	}
	
	public Vector2 getPosition(){
		return pos;
	}
	
	public Vector2 getZise(){
		return size;
	}
	
	public float getWidth(){
		return size.x;
	}
	
	public float getHeight(){
		return size.y;
	}
	
}
