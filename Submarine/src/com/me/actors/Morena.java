package com.me.actors;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gushikustudios.rube.RubeScene;
import com.me.submarine.BodyAccessor;

public class Morena {
	
	Sprite MorenaS;
	Body MorenaB;
	Vector2 MorenaO;
	Vector2 MorenaD;
	String MorenaN;	
	Timeline MorenaTl;
	TweenCallback MorenaCb;
	TweenCallback Step1Cb;
	Boolean TlFinish = true;
	
	public Morena(Body smorena,RubeScene scene){
		MorenaN = "morena"+scene.getCustom(smorena, "val2", (String)null);
    	MorenaB = scene.getNamed(Body.class, MorenaN).get(0);
    	smorena.setUserData(this);
    	MorenaD = scene.getCustom(MorenaB, "val1",(Vector2)null);
    	MorenaO = new Vector2(MorenaB.getPosition());
    	MorenaS = new Sprite(new Texture(Gdx.files.internal("data/morena.png")));
    	MorenaS.setSize(3, 3*(MorenaS.getHeight()/MorenaS.getWidth()));
    	
    	MorenaCb = new TweenCallback() {
    		@Override public void onEvent(int type, BaseTween source) {
    			switch (type) {
    				case START: 
    						TlFinish = false;
    						System.out.println("StartCallBack");break;
    				case COMPLETE:
    						TlFinish = true;
    						System.out.println("FinishCallBack"); break;
    			}
    		}
    	};
    	
    	Step1Cb = new TweenCallback() {
    		@Override public void onEvent(int type, BaseTween source) {
    			switch (type) {
    				case START: System.out.println("StartMorena Step");break;
    				case COMPLETE:System.out.println("Finish Step"); break;
    			}
    		}
    	};
	}

	public void StartAnimation(TweenManager tweenManager){
		if(TlFinish){
			MorenaTl = Timeline.createSequence()  
		    			.push(Tween.to(MorenaB,BodyAccessor.POS_XY, 1f)
		    					.target(MorenaD.x,MorenaD.y)
		    					.ease(Quad.INOUT))
		    					.setCallback(Step1Cb)
		    					.setCallbackTriggers(TweenCallback.START | TweenCallback.COMPLETE)
						.push(Tween.to(MorenaB, BodyAccessor.POS_XY, 1f)
								.target(MorenaO.x,MorenaO.y)
								.ease(Quad.INOUT))
						.push(Tween.to(MorenaB, BodyAccessor.POS_XY, 3f)
								.target(MorenaO.x,MorenaO.y)
								.ease(Quad.INOUT))
				.setCallback(MorenaCb)
		    	.setCallbackTriggers(TweenCallback.START | TweenCallback.COMPLETE)
				.start(tweenManager);
		}
	}
	
	public void draw(SpriteBatch batch){
		MorenaS.setPosition(MorenaB.getPosition().x-1, MorenaB.getPosition().y);
		MorenaS.draw(batch);
	}
}
