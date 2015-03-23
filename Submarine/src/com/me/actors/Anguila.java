package com.me.actors;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.brashmonkey.spriter.Spriter;
import com.brashmonkey.spriter.player.SpriterPlayer;
import com.gushikustudios.rube.RubeScene;
import com.me.gdxspriter.SpriterDrawer;
import com.me.gdxspriter.SpriterLoader;
import com.me.submarine.BodyAccessor;

public class Anguila {
	Body AnguilaB;
	SpriterPlayer AnguilaS;
	Tween tween;
	Vector2 AnguilaO;
	Vector2 AnguilaD;
	
	TweenCallback flip = new TweenCallback() {
		@Override public void onEvent(int type, BaseTween source) {
			switch (type) {
				case START:
					if(isRight()){
						if(AnguilaS.getFlipX()==1)
							AnguilaS.flipX();
					}else{
						if(AnguilaS.getFlipX()==-1)
							AnguilaS.flipX();
					}
					;break;
				case COMPLETE:System.out.println("flip"); break;
			}
		}
	};
	
	public Anguila(Body body, RubeScene scene, TweenManager tweenManager, Spriter spriter, SpriterLoader loader) {
		AnguilaB = body;
		AnguilaO = new Vector2(body.getPosition());
		AnguilaD = new Vector2(scene.getCustom(AnguilaB, "val1", (Vector2)null));
		AnguilaS = new SpriterPlayer(spriter, 0, loader);
		AnguilaS.setScale(0.015f);
		
		tween = Tween.to(AnguilaB, BodyAccessor.POS_XY, 2f)
		.ease(Linear.INOUT)
		.target(AnguilaD.x, AnguilaD.y)
		.repeatYoyo(-1, 0.0001f)
		.setCallback(flip)
		.setCallbackTriggers(TweenCallback.START | TweenCallback.COMPLETE)
		.start(tweenManager);
	}
	
	public void draw(SpriteBatch batch,SpriterDrawer drawer){
		AnguilaS.update(AnguilaB.getPosition().x,AnguilaB.getPosition().y);
		drawer.draw(AnguilaS);
	}
	
	private boolean isRight(){
		Float disto = AnguilaO.x - AnguilaB.getPosition().x;
		Float distd = AnguilaD.x - AnguilaB.getPosition().x;
		Float dist = 0f;
		if(disto > distd)
			dist = disto;
		else
			dist = distd;
		if(dist>0){
			return true;
		}else{
			return false;
		}
	}
}
