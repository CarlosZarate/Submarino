package com.me.actors;

import java.util.List;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.TweenPaths;
import aurelienribon.tweenengine.equations.Linear;
import aurelienribon.tweenengine.equations.Quad;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.brashmonkey.spriter.player.SpriterPlayer;
import com.gushikustudios.rube.RubeScene;
import com.me.gdxspriter.SpriterDrawer;
import com.me.submarine.BodyAccessor;

public class Pez {
	Body PezB;
	SpriterPlayer PezS;
	private World world;
	private RubeScene scene;
	private Vector2 PezO;
	private Vector2 PezD;
	private Vector2 PezM;
	private Timeline PezTL;
	private TweenCallback PezTCB;
	private List <Pez> ListPez;
	private TweenManager tweenManager;
	
	public Pez(Vector2 setorigin, Vector2 setdestino, Vector2 setmedio, SpriterPlayer player, RubeScene setscene, List <Pez> listapez, TweenManager settweenmanager){
		PezS = player;
		world = setscene.getWorld();
		scene = setscene;
		PezO = new Vector2(setorigin);
		PezD = new Vector2(setdestino);
		PezM = new Vector2(setmedio);
		ListPez = listapez;
		tweenManager = settweenmanager;
		createPez();	
		
		PezTCB = new TweenCallback() {
    		@Override public void onEvent(int type, BaseTween source) {
    			switch (type) {
    				case START:
    						break;
    				case COMPLETE:
    						Destroy();
    						break;
    			}
    		}
    	};

		CreateTimeLine();	
	}
	
	public void Destroy(){
		PezTL.kill();
		world.destroyBody(PezB);
		ListPez.remove(this);
	}
	
	private void createPez(){
		BodyDef ballBodyDef = new BodyDef();
		ballBodyDef.type = BodyType.StaticBody;
		
		CircleShape shapemov = new CircleShape();
		shapemov.setRadius(0.3f);
		
		FixtureDef fdmov = new FixtureDef();
		fdmov.shape = shapemov;
		//fdmov.isSensor = true;
		fdmov.filter.categoryBits = 3;
		fdmov.filter.maskBits = (short) 65534;
		
		PezB =world.createBody(ballBodyDef);
		PezB.createFixture(fdmov);
		PezB.setUserData(this);
		scene.setCustom(PezB, "tipo", "pez");
		if(PezD.x > PezD.x)
			PezB.setTransform(PezO.x,PezO.y, -80);
		else
			PezB.setTransform(PezO.x,PezO.y, 80);
		PezB.setFixedRotation(true);
		
		
	}
	
	private void CreateTimeLine(){
		PezTL = Timeline.createSequence()
				.beginParallel()
	    			.push(Tween.to(PezB, BodyAccessor.POS_XY, 3.0f)
	    					.waypoint(32, -13)
	    					.target(PezD.x, PezD.y)
	    					.ease(Linear.INOUT)
	    					.path(TweenPaths.catmullRom))
					.push(Tween
							.to(PezB, BodyAccessor.ROTATE, 3.0f)
							.target(-90)
							.ease(Linear.INOUT))
				.end()
		.setCallback(PezTCB)
    	.setCallbackTriggers(TweenCallback.START | TweenCallback.COMPLETE)
		.start(tweenManager);
	}
	
	public void draw(SpriteBatch batch,SpriterDrawer drawer){
		PezS.setAngle(PezB.getAngle());
		PezS.update(PezB.getPosition().x,PezB.getPosition().y);
		drawer.draw(PezS);
	}
}
