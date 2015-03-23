package com.me.Spatial;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class NamedSprite{
	private String name;
	private Sprite sprite;
	
	public NamedSprite(String setname, Sprite setsprite){
		sprite = setsprite;
		name = setname;
	}
	
	public void setSprite(Sprite setsprite){
		sprite = setsprite;
	}
	
	public Sprite getSprite(){
		return sprite;
	}
	
	public void setName(String setName){
		name = setName;
	}
	
	public String getName(){
		return name;
	}
}
