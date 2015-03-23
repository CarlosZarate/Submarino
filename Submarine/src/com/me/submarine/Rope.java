package com.me.submarine;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.gushikustudios.rube.RubeScene;

public class Rope {
	
	public Body attachBodyA;
	public Body attachBodyB;
	public ArrayList<Body> bodies = new ArrayList<Body>();
	public boolean isTramp;
	RubeScene scene;
	World world;

	public Rope(Body attachBodyA, Body attachBodyB, RubeScene scene, Texture link) {
		this(attachBodyA, attachBodyB, scene, 0, link);
	}
		
	@SuppressWarnings("deprecation")
	public Rope(Body attachBodyA, Body attachBodyB, RubeScene scene, int extraLength, Texture link) {
		this.attachBodyA = attachBodyA;
		this.attachBodyB = attachBodyB;
		this.world = scene.getWorld();
		this.isTramp = false;
		float generationStep = 0.6f;
		
		RopeJointDef rj = new RopeJointDef();
		rj.localAnchorA.x = -0.05f;
		rj.localAnchorB.x = 0.05f;
		rj.maxLength = 0.14f;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.06f, 0.05f);
		
		CircleShape shape2 = new CircleShape();
		shape2.setRadius(0.2f);
		
		FixtureDef fd = new FixtureDef();
		fd.isSensor = true;
		fd.filter.categoryBits = 3;
		fd.filter.maskBits = (short) 65532;
		fd.shape = shape;
		attachBodyA.setAngularDamping(1.0f);
		attachBodyB.setAngularDamping(1.0f);
		
		Vector2 distanceVect = new Vector2(attachBodyB.getPosition());
		distanceVect.sub(attachBodyA.getPosition());
		distanceVect.nor();
		distanceVect.mul(0.05f);
		
		Body prevBody = attachBodyA;
		Body tempBody;
		BodyDef bd = new BodyDef();
		
		bd.type = BodyType.DynamicBody;
		bd.angularDamping = 50.0f;
		
		Vector2 nextBodyPosition = new Vector2(attachBodyA.getPosition());
		int dist = (int)(attachBodyA.getPosition().dst(attachBodyB.getPosition()));
		if (extraLength > 100)
			dist = extraLength - 100;
		if (extraLength > 0 && extraLength < 100)
			fd.density = 30 /  ((((float)dist)/1.5f) + (100-extraLength));
		fd.density = 30 /  (((float)dist)/1.5f);
		if (fd.density < 5.0f)
			fd.density = 5.0f;
		fd.density = 8;
		
		for (float i = 0; i < dist; i+=generationStep/2) {
			nextBodyPosition.add(distanceVect.x, distanceVect.y);
			bd.angle = (float)(Math.toRadians(distanceVect.angle()));
			bd.position.set(nextBodyPosition);
			tempBody = world.createBody(bd);
			tempBody.createFixture(fd);
			Sprite templink = new Sprite(link);
			templink.setSize(0.35f, 0.35f);
			//templink.setOrigin(0.7f, 0.7f);
			rj.bodyA = prevBody;
			rj.bodyB = tempBody;
			world.createJoint(rj);
			tempBody.setUserData(templink);
			bodies.add(tempBody);
			prevBody = tempBody;
		}
		
		if (extraLength > 0 && extraLength < 100) {
			for (int i=0; i<extraLength; i++) {
				nextBodyPosition.add(distanceVect.x*2, distanceVect.y*2);
				bd.angle = (float)(Math.toRadians(distanceVect.angle()));
				bd.position.set(nextBodyPosition);
				tempBody = world.createBody(bd);
				tempBody.createFixture(fd);				
				Sprite templink = new Sprite(link);
				templink.setSize(0.35f, 0.35f);
				//templink.setOrigin(0.7f, 0.7f);
				tempBody.setUserData(templink);
				
				rj.bodyA = prevBody;
				rj.bodyB = tempBody;
				world.createJoint(rj);
				bodies.add(tempBody);
				prevBody = tempBody;
			}
		}
		
		rj.bodyA = prevBody;
		rj.bodyB = attachBodyB;
		world.createJoint(rj);
		
		System.out.println();
		shape.dispose();
	}
}
