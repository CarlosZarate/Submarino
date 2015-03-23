package com.me.actors;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.brashmonkey.spriter.Spriter;
import com.brashmonkey.spriter.player.SpriterPlayer;
import com.gushikustudios.rube.RubeScene;
import com.me.gdxspriter.SpriterLoader;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

public class BasePez {
	private boolean isWork = true;
	private Tween tween;
	private Body BaseB;
	private int count;
	private List <Pez> ListPez;
	private Vector2 BaseO;
	private Vector2 BaseM;
	private Vector2 BaseD;
	private TweenManager tweenManager;
	private SpriterPlayer PezS;
	private World world;
	private RubeScene scene;
	
	public BasePez(Body body, TweenManager tweenmanager, RubeScene setscene,List <Pez> listapez, Spriter pezspriter, SpriterLoader pezLoaderS){
		PezS = new SpriterPlayer(pezspriter, 0, pezLoaderS);
		PezS.setAnimationIndex(1);
		PezS.setScale(0.015f);
		BaseB = body;
		scene = setscene;
		ListPez = listapez;
		
		BaseD = scene.getCustom(body, "val1", (Vector2)null);
		BaseO = new Vector2(body.getPosition());
		BaseM = new Vector2((BaseO.x+BaseD.x)/2f,(BaseO.y+BaseD.y)/2f);
		

		if(BaseD.x > BaseO.x)
			PezS.flipX();
		
		tweenManager = tweenmanager;
		tween = Tween.call(new TweenCallback() {
			@Override public void onEvent(int type, BaseTween<?> source) {
				if(isWork) {
					ListPez.add(new Pez(BaseO, BaseD, BaseM, PezS, scene, ListPez, tweenManager));
				}
			}
		}).repeat(-1,  0.6f).start(tweenManager);
	}
	
	public void Destroy(){
		count--;
		if(count == 0){
			tween.kill();
			isWork = false;
		}
	}	
}
