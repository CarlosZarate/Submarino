package com.me.submarine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.brashmonkey.spriter.Spriter;
import com.brashmonkey.spriter.player.SpriterPlayer;
import com.gushikustudios.rube.RubeScene;
import com.gushikustudios.rube.loader.RubeSceneLoader;
import com.gushikustudios.rube.loader.serializers.utils.RubeImage;
import com.me.Spatial.MapSpatial;
import com.me.Spatial.NamedSprite;
import com.me.Spatial.PolySpatial;
import com.me.Spatial.SimpleSpatial;
import com.me.actors.Anguila;
import com.me.actors.BasePez;
import com.me.actors.Morena;
import com.me.actors.Pez;
import com.me.gdxspriter.SpriterDrawer;
import com.me.gdxspriter.SpriterLoader;

/**
 * Use the left-click to pan. Scroll-wheel zooms.
 * 
 * @author cvayer, tescott
 * 
 */
public class Submarine implements ApplicationListener, ContactListener
{
   private OrthographicCamera camera;
   private RubeSceneLoader loader;
   private RubeScene scene;
   private Box2DDebugRenderer debugRender;
   private int NumMonedas = 0;
   private BitmapFont font;

   private Array<SimpleSpatial> spatials; // used for rendering rube images
   private Array<PolySpatial> polySpatials;
   private Map<String, Texture> textureMap;
   private Map<Texture, TextureRegion> textureRegionMap;
   private Array<NamedSprite> ListImgMap = new Array<NamedSprite>();
   private Array<MapSpatial> BuildMap = new Array<MapSpatial>();

   private static final Vector2 mTmp = new Vector2(); // shared by all objects
   private static final Vector2 mTmp3 = new Vector2(); // shared during polygon creation
   private SpriteBatch batch;
   private PolygonSpriteBatch polygonBatch;

   private World mWorld;

   private float mAccumulator; // time accumulator to fix the physics step.

   private int mVelocityIter = 8;
   private int mPositionIter = 3;
   private float mSecondsPerStep = 1 / 60f;
   final static float MAX_VELOCITY = 5f;
   private static final float MAX_DELTA_TIME = 0.25f;
   int mPositionIterations = 3;
   
   /*>><<<<><>><<*/
   ArrayList <Body> Minas = new ArrayList<Body>();
   Array <Body> MinasUp = new Array<Body>();
   Array <Body> Rocas = new Array<Body>();
   Array <Body> RemoveElem = new Array<Body>();
   Array <Rope> Chains = new Array<Rope>();
   ArrayList <Body> Monedas = new ArrayList<Body>();
   /*>>>>>>>>>>>>>>>>*/
   List <Morena> MorenaList = new LinkedList<Morena>();
   List <Anguila> AnguilaList = new LinkedList<Anguila>();
   List <Pez> PezList = new LinkedList<Pez>();
   List <BasePez> BasePezList = new LinkedList<BasePez>();
   /*<<<<<<<<<<<<<<*/
   boolean dirchange = false;
   Body player;
   float offseteffect = 0.19f;
   Fixture playerSensorFixture;
   Vector2[] verticesflip = new Vector2[8];
   Vector2[] vertices = new Vector2[8];
   
   Sprite minesprite;
   Sprite monedasprite;
   Texture linktxture;
   Texture minetexture;
   Image alertbg;
   boolean isalert = true;
   boolean ismorenaatack = true;
   
   private ParticleEffect effect;
   private ParticleEmitter emitter_1;
   private ParticleEmitter emitter_2;
   private ParticleEmitter emitter_3;
   private ParticleEmitter emiterx;
	
   private static final int N = 1;
   private static final int NE = 2;
   private static final int NW = 3;
   private static final int E = 4;
   private static final int W = 5;
   private static final int SE = 6;
   private static final int SW = 7;
   private static final int S = 8;
   
   private SpriterLoader loaderSpriter;
   private SpriterDrawer drawerSpriter;
   
   private Spriter anim;
   
   private Stage stage;
   private Stage GuiAlert; 
   public TextureAtlas joypadtextures;
   public TextureAtlas ImagesMap;
   private TextureRegion contorno;
   private TextureRegion buttonTexture;
   private Touchpad joystick2;
	
   private int AnguloEfect = 0;
   private float porcentx;
   private float porcenty;

   public final TweenManager tweenManager = new TweenManager();

   @Override
   public void create()
   {
      float w = Gdx.graphics.getWidth();
      float h = Gdx.graphics.getHeight();

      camera = new OrthographicCamera(10, 10 * h / w);
      camera.zoom +=1;
      camera.update();
      
      batch = new SpriteBatch();
      polygonBatch = new PolygonSpriteBatch();

      loader = new RubeSceneLoader();

      scene = loader.loadScene(Gdx.files.internal("data/nivel1.json"));
      
      loaderSpriter = new SpriterLoader(true);
      drawerSpriter = new SpriterDrawer(loaderSpriter,batch);     
      anim = Spriter.getSpriter("data/anim/anim.scml", loaderSpriter);

      debugRender = new Box2DDebugRenderer();      

      joypadtextures = new TextureAtlas(Gdx.files.internal("data/joypad.pack"));
      ImagesMap = new TextureAtlas(Gdx.files.internal("data/images/elements.pack"));
      
      font = new BitmapFont(Gdx.files.internal("data/arial-15.fnt"), false);

      textureMap = new HashMap<String, Texture>();
      textureRegionMap = new HashMap<Texture, TextureRegion>();

      createSpatialsFromRubeImages(scene);
      createPolySpatialsFromRubeFixtures(scene);

      mWorld = scene.getWorld();
      // configure simulation settings
      mVelocityIter = scene.velocityIterations;
      mPositionIter = scene.positionIterations;
      if (scene.stepsPerSecond != 0)
      {
         mSecondsPerStep = 0.016f;//scene.stepsPerSecond;
      }
      
      Tween.setWaypointsLimit(10);
      Tween.registerAccessor(Body.class, new BodyAccessor());
      Tween.registerAccessor(Sprite.class, new SpriteAccessor());
      Tween.registerAccessor(Image.class, new ImageAccessor());
      
      createPlayer();
      
      Texture capstexture = new Texture(Gdx.files.internal("data/submarion3.png"));
      capstexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
      
      linktxture = new Texture(Gdx.files.internal("data/cadena.png"));
      linktxture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
      
      minetexture = new Texture(Gdx.files.internal("data/mine.png"));
      minetexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
      
      Texture monedatexture = new Texture(Gdx.files.internal("data/moneda.png"));
      monedatexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
      monedasprite = new Sprite(monedatexture);
      monedasprite.setSize(0.6f, monedasprite.getHeight()/monedasprite.getWidth()*0.6f);
      
      minesprite = new Sprite(new TextureRegion(capstexture,0,0,512,487));
      minesprite.setOrigin(64, 64);
      minesprite.setSize(2.2f, 487/512f*2.2f);
      
      alertbg = new Image(new Texture(Gdx.files.internal("data/alert.png")));
      alertbg.setOrigin(640, 400);
      alertbg.setSize(w,h);
      alertbg.setColor(1, 0, 0, 0);
      
		
      contorno = joypadtextures.findRegion("contorno1280");
      buttonTexture = joypadtextures.findRegion("boton1280");
		
      TouchpadStyle style = new TouchpadStyle(new TextureRegionDrawable(contorno),new TextureRegionDrawable(buttonTexture));
      joystick2 = new Touchpad(1,style );
      joystick2.setPosition(100, 150);
		
      stage = new Stage();
      GuiAlert = new Stage();
      
      GuiAlert.addActor(alertbg);
      stage.addActor(joystick2);
		
      InputMultiplexer multiplexer = new InputMultiplexer();		
      multiplexer.addProcessor(stage);
      Gdx.input.setInputProcessor(multiplexer);	
		
      AgregarListener();
      createTurboEfect();
      createElements();
      
      initRocas();
   }

   @Override
   public void dispose()
   {
   }
   
   @Override
   public void render()
   {
	   Gdx.gl.glClearColor(5f/255, 48f/255, 67f/255, 1);
	   Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	   
	   Vector2 pos = player.getPosition();
	   float lerp = 0.1f;
	   Vector3 position = camera.position;
	   position.x += (pos.x - position.x) * lerp;
	   position.y += (pos.y - position.y) * lerp;
	   camera.update();

	   minesprite.setPosition(pos.x -1.1f,pos.y-0.98f);
	   
	   float delta = Gdx.graphics.getDeltaTime();
	   
	   tweenManager.update(delta); 
	   

	   if (delta > MAX_DELTA_TIME)
	   {
		   delta = MAX_DELTA_TIME;
	   }
	   mAccumulator += 0.016;//delta;
	
	   while (mAccumulator >= mSecondsPerStep)
	   {
		   mWorld.step(mSecondsPerStep, mVelocityIter, mPositionIter);
		   mAccumulator -= mSecondsPerStep;
	   }

	   if ((polySpatials != null) && (polySpatials.size > 0))
	   {
		   polygonBatch.setProjectionMatrix(camera.combined);
		   polygonBatch.begin();
		   for (int i = 0; i < polySpatials.size; i++)
		   {
			   polySpatials.get(i).render(polygonBatch, 0);
		   }
		   polygonBatch.end();
	   }
	   mWorld.setContactListener(this);
	   
	   Vector2 vel = player.getLinearVelocity();
	   
	   if(porcentx>0 && vel.x< MAX_VELOCITY) {
		   player.applyLinearImpulse(porcentx, 0, pos.x, pos.y,true);
	   }
		
	   if(vel.x > -MAX_VELOCITY && porcentx<0) {
		   player.applyLinearImpulse(porcentx, 0, pos.x, pos.y,true);
	   }
		
	   if(porcenty>0 && vel.y < MAX_VELOCITY) {
		   player.applyLinearImpulse(0,porcenty, pos.x, pos.y,true);
	   }
		
	   if(vel.y > -MAX_VELOCITY && porcenty<0) {
		   player.applyLinearImpulse(0,porcenty, pos.x, pos.y,true);
	   }
	   
	   if(porcentx > 0)
		   if(!dirchange){
			   flipPlayerX(true);
			   dirchange = true;
		   }
	   if(porcentx < 0)
		   if(dirchange){
			   flipPlayerX(false);
			   dirchange = false;
		   }
		
	   if(porcentx == 0)
		   player.setLinearVelocity(vel.x * 0.995f, vel.y);
	   
	   batch.setProjectionMatrix(camera.projection);
	   batch.setTransformMatrix(camera.view);
	   

	   //debugRender.render(mWorld, camera.combined);
	   
	   batch.begin();	   

	   for(Pez pez : PezList)
	   {
		   pez.draw(batch, drawerSpriter);
	   }
	   
	   for(int i = 0 ; i < BuildMap.size ; i++){
		   Vector2 size = BuildMap.get(i).getZise();
		   Vector2 imgpos = BuildMap.get(i).getPosition();
		   int index = BuildMap.get(i).getIndex();
		   Sprite sprite = ListImgMap.get(index).getSprite();
		   sprite.setPosition(imgpos.x - size.x/2, imgpos.y - size.y/2);
		   sprite.draw(batch);
	   }
	   effect.setPosition(pos.x+offseteffect,pos.y);
	   effect.draw(batch, delta);
	   
	   emiterx.draw(batch, delta);
	   
	   minesprite.draw(batch);
	   
	   for(Morena morena : MorenaList){
		   morena.draw(batch);
	   }
	   for(Anguila anguila : AnguilaList){
		   anguila.draw(batch, drawerSpriter);
	   }
	   
	   if ((spatials != null) && (spatials.size > 0))
	   {
		   for (int i = 0; i < spatials.size; i++)
	       {
	          spatials.get(i).render(batch, 0);
	       }
	    }
	   
	   for(int i = 0 ; i < Monedas.size() ; i++){
	    		Body moneda = Monedas.get(i);
	    		monedasprite.setPosition(moneda.getPosition().x-0.3f, moneda.getPosition().y-0.3f);
	    		monedasprite.draw(batch);
	    }	
	   
	   for(int i = 0 ; i < Chains.size ; i++){
	    	for(int j = 0 ; j < Chains.get(i).bodies.size(); j++){
	    		Body Blink = Chains.get(i).bodies.get(j);
	    		Sprite Slink = (Sprite)Blink.getUserData();
	    		Slink.setPosition(Blink.getPosition().x-0.18f, Blink.getPosition().y);
	    		Slink.draw(batch);
	    	}
	    }	   

	    for(int i = 0 ; i < Minas.size() ; i++){
	    	Body mina = Minas.get(i);
	    	if(mina.getLinearVelocity().y < 0.25f)
			mina.applyLinearImpulse(0f, 0.05f, mina.getPosition().x, mina.getPosition().y,true);
	    	Sprite Smine = (Sprite)mina.getUserData();
	    	Smine.setPosition(mina.getPosition().x-0.8f, mina.getPosition().y-0.8f);
	    	Smine.draw(batch);
	    }
	    batch.end();	    
	    GuiAlert.act(delta);
	    GuiAlert.draw();
	    stage.act(delta);
	    stage.draw();
	    
	    removeElement();
   }
   
   private void createPlayer() {
		BodyDef playerBodyDef = new BodyDef();
		playerBodyDef.type = BodyType.DynamicBody;
		
		vertices = new Vector2[8];

	    vertices[0] = new Vector2(-1.02f, 0f);
	    vertices[1] = new Vector2(-0.21f, 0.85f);
	    vertices[2] = new Vector2(0.47f, 0.9f);
	    vertices[3] = new Vector2(1.02f, 0.46f);
	    vertices[4] = new Vector2(1.02f,-0.1f);
	    vertices[5] = new Vector2(0.57f,-0.67f);
	    vertices[6] = new Vector2(-0.64f,-0.67f);
	    vertices[7] = new Vector2(-1f,-0.35f);
	    
	    verticesflip = new Vector2[8];
	    verticesflip[0] = new Vector2(1.02f, 0f);
	    verticesflip[1] = new Vector2(0.21f, 0.85f);
	    verticesflip[2] = new Vector2(-0.47f, 0.9f);
	    verticesflip[3] = new Vector2(-1.02f, 0.46f);
	    verticesflip[4] = new Vector2(-1.02f,-0.1f);
	    verticesflip[5] = new Vector2(-0.57f,-0.67f);
	    verticesflip[6] = new Vector2(0.64f,-0.67f);
	    verticesflip[7] = new Vector2(1f,-0.35f);
	    
	    PolygonShape shape = new PolygonShape();
	    shape.set(vertices);
		
		FixtureDef fdmov = new FixtureDef();
		fdmov.density = 1f;
		fdmov.friction = 1f;
		fdmov.restitution = 0.3f;
		fdmov.shape = shape;
		fdmov.filter.categoryBits = 3;
		fdmov.filter.maskBits = (short) 65535;
		
		player = mWorld.createBody(playerBodyDef);
		playerSensorFixture = player.createFixture(fdmov);
		scene.setCustom(player, "tipo", "player");
		player.setBullet(true);
		player.setActive(true);
		player.setFixedRotation(true);
		//player.setTransform(47,-20,0);
	}
   

   	private void flipPlayerX(boolean isflip){
   		//Vector2 pos = player.getPosition();
   		PolygonShape shape = new PolygonShape();;
   		if(isflip){
   			minesprite.flip(true, false);
   			player.destroyFixture(playerSensorFixture);
   			shape.set(verticesflip);
   			offseteffect = -0.19f;
   		}
   		else{
   			minesprite.flip(true, false);
   			player.destroyFixture(playerSensorFixture);
   			shape.set(vertices);
   			offseteffect = 0.19f;
	   }
   		
   		FixtureDef fdmov = new FixtureDef();
		fdmov.density = 0.1f;
		fdmov.friction = 1f;
		fdmov.restitution = 0.3f;
		fdmov.shape = shape;
		fdmov.filter.categoryBits = 3;
		fdmov.filter.maskBits = (short) 65535;
		playerSensorFixture = player.createFixture(fdmov);
   		shape.dispose();
   }

   
   public void createElements(){
	   Array<Body> Bodies = scene.getBodies();
	   for (int i = 0; i < Bodies.size; i++){
		   Body body = Bodies.get(i);
			String tipo = scene.getCustom(body, "tipo", (String) null);
			if(tipo.equals("mine")){
				createMine(body,4);
			}
			if(tipo.equals("anguila")){
				AnguilaList.add(new Anguila(body, scene, tweenManager, anim, loaderSpriter));
			}
			if(tipo.equals("roca")){
				body.setActive(false);
				Rocas.add(body);
			}
			if(tipo.equals("smorena")){
				MorenaList.add(new Morena(body,scene));
			}
			if(tipo.equals("card")){
				BasePezList.add(new BasePez(body, tweenManager, scene, PezList, anim, loaderSpriter));
			}
			if(tipo.equals("coin")){
				Monedas.add(body);
			}
		}
	}
   private void initRocas(){
		  Tween.call(new TweenCallback() {
			  private int idx = 0;
				@Override public void onEvent(int type, BaseTween<?> source) {
					if(Rocas.size>idx) {
						Rocas.get(idx).setAwake(true);
						Rocas.get(idx).setActive(true);
						idx++;
					}
				}
			}).repeat(-1, 1f).start(tweenManager);
	  }
   
   public void createMine(Body ancla, float distancia ){
	   Vector2 pos = null;
	   String tipomina = scene.getCustom(ancla, "val2", (String)null);
	   if(tipomina.equals("up"))
		   pos = new Vector2(ancla.getPosition().x,ancla.getPosition().y+distancia);
	   else
		   pos = new Vector2(ancla.getPosition().x,ancla.getPosition().y-distancia);
	   
	   Body mine = crearEsfera(pos,BodyType.DynamicBody);
	   mine.applyLinearImpulse(0.5f, 0f, mine.getPosition().x, mine.getPosition().y,true);
	   
	   if(tipomina.equals("up"))
		   Minas.add(mine);
	   Rope rope =  new Rope(ancla,mine,scene,linktxture);
	   Chains.add(rope);
	}

	public Body crearEsfera(Vector2 v, BodyType bt){
		Sprite Smine = new Sprite(minetexture);
		Smine.setSize(2f, 2f);
		
		Body b;
		BodyDef ballBodyDef = new BodyDef();
		ballBodyDef.type = bt;
		
		CircleShape shapemov = new CircleShape();
		shapemov.setRadius(0.7f);
		
		FixtureDef fdmov = new FixtureDef();
		fdmov.density = 0.5f;
		fdmov.friction = 0.5f;
		fdmov.restitution = 0f;
		fdmov.shape = shapemov;
		
		b =mWorld.createBody(ballBodyDef);
		b.createFixture(fdmov);
		b.setUserData(Smine);
		
		scene.setCustom(b, "tipo", "bola");
		b.setTransform(v, 0);
		b.setFixedRotation(true);
		return b;
	}

   
   	private void createTurboEfect(){
		effect = new ParticleEffect();
		effect.load(Gdx.files.internal("data/bubbles.p"), Gdx.files.internal("data"));
		
		Array<ParticleEmitter> emitters = new Array<ParticleEmitter>(effect.getEmitters());
		emiterx = emitters.get(0);
		emitter_1 = emitters.get(1);
		emitter_1.getXOffsetValue().setActive(true);
		emitter_1.getYOffsetValue().setActive(true);
		emitter_2 = emitters.get(2);
		emitter_2.getXOffsetValue().setActive(true);
		emitter_2.getYOffsetValue().setActive(true);
		emitter_3 = emitters.get(3);
		emitter_3.getXOffsetValue().setActive(true);
		emitter_3.getYOffsetValue().setActive(true);
		effect.getEmitters().clear();
		effect.getEmitters().add(emitter_1);
		effect.getEmitters().add(emitter_2);
		effect.getEmitters().add(emitter_3);
		
		emiterx.setPosition(0, 0);
		
		effect.setPosition(0, 0);
	}
   	
   	private void removeElement(){
   		while(RemoveElem.size >0){
   			mWorld.destroyBody(RemoveElem.pop());
   			
   		}
   	}

   /**
    * Creates an array of SimpleSpatial objects from RubeImages.
    * 
    * @param scene2
    */
   	private void createSpatialsFromRubeImages(RubeScene scene)
   	{
   		Array<RubeImage> images = scene.getImages();
   		if ((images != null) && (images.size > 0))
   		{
   			spatials = new Array<SimpleSpatial>();
   			for (int i = 0; i < images.size; i++)
   			{
   				RubeImage image = images.get(i);
   				mTmp.set(image.width, image.height);
   				String tipo = scene.getCustom(image, "tipo", (String)null);
   				String filename = scene.getCustom(image, "img", (String)null);
   				Sprite imgsprt = ImagesMap.createSprite(filename);
   				if(tipo==null){
   					imgsprt.setSize(mTmp.x, mTmp.y);        		 
   					imgsprt.setOrigin(mTmp.x / 2, mTmp.y / 2);
   					int index = findSpite(filename,imgsprt);
   					BuildMap.add(new MapSpatial(image.center, index, mTmp));
   				}
   				else 
   					if(tipo.equals("nors"))
   					{
   						SimpleSpatial spatial = new SimpleSpatial(imgsprt, image.flip, image.body, image.color, mTmp, image.center,
   						image.angleInRads * MathUtils.radiansToDegrees);
   						spatials.add(spatial);
   					}else
   						if(tipo.equals("nord"))
   						{
   							SimpleSpatial spatial = new SimpleSpatial(imgsprt, image.flip, image.body, image.color, mTmp, image.center,
   									image.angleInRads * MathUtils.radiansToDegrees);
   							spatials.add(spatial);
   						}
   			}
   		}
   }

   /**
    * Creates an array of PolySpatials based on fixture information from the scene. Note that
    * fixtures create aligned textures.
    * 
    * @param scene
    */
   private void createPolySpatialsFromRubeFixtures(RubeScene scene)
   {
      Array<Body> bodies = scene.getBodies();

      if ((bodies != null) && (bodies.size > 0))
      {
         polySpatials = new Array<PolySpatial>();
         Vector2 bodyPos = new Vector2();
         // for each body in the scene...
         for (int i = 0; i < bodies.size; i++)
         {
            Body body = bodies.get(i);
            bodyPos.set(body.getPosition());

            ArrayList<Fixture> fixtures = body.getFixtureList();

            if ((fixtures != null) && (fixtures.size() > 0))
            {
               // for each fixture on the body...
               for (int j = 0; j < fixtures.size(); j++)
               {
                  Fixture fixture = fixtures.get(j);

                  String textureName = scene.getCustom(fixture, "TextureMask", (String) null);
                  if (textureName != null)
                  {
                     String textureFileName = "data/" + textureName;
                     Texture texture = textureMap.get(textureFileName);
                     TextureRegion textureRegion = null;
                     if (texture == null)
                     {
                        texture = new Texture(textureFileName);
                        texture.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
                        textureMap.put(textureFileName, texture);
                        textureRegion = new TextureRegion(texture);
                        textureRegionMap.put(texture, textureRegion);
                     }
                     else
                     {
                        textureRegion = textureRegionMap.get(texture);
                     }

                     // only handle polygons at this point -- no chain, edge, or circle fixtures.
                     if (fixture.getType() == Shape.Type.Polygon)
                     {
                        PolygonShape shape = (PolygonShape) fixture.getShape();
                        int vertexCount = shape.getVertexCount();
                        float[] vertices = new float[vertexCount * 2];

                        // static bodies are texture aligned and do not get drawn based off of the related body.
                        if (body.getType() == BodyType.StaticBody)
                        {
                           for (int k = 0; k < vertexCount; k++)
                           {

                              shape.getVertex(k, mTmp);
                              mTmp.rotate(body.getAngle() * MathUtils.radiansToDegrees);
                              mTmp.add(bodyPos); // convert local coordinates to world coordinates to that textures are
                                                 // aligned
                              vertices[k * 2] = mTmp.x * PolySpatial.PIXELS_PER_METER;
                              vertices[k * 2 + 1] = mTmp.y * PolySpatial.PIXELS_PER_METER;
                           }
                           PolygonRegion region = new PolygonRegion(textureRegion, vertices);
                           PolySpatial spatial = new PolySpatial(region, Color.WHITE);
                           polySpatials.add(spatial);
                        }
                        else
                        {
                           // all other fixtures are aligned based on their associated body.
                           for (int k = 0; k < vertexCount; k++)
                           {
                              shape.getVertex(k, mTmp);
                              vertices[k * 2] = mTmp.x * PolySpatial.PIXELS_PER_METER;
                              vertices[k * 2 + 1] = mTmp.y * PolySpatial.PIXELS_PER_METER;
                           }
                           PolygonRegion region = new PolygonRegion(textureRegion, vertices);
                           PolySpatial spatial = new PolySpatial(region, body, Color.WHITE);
                           polySpatials.add(spatial);
                        }
                     }
                     else if (fixture.getType() == Shape.Type.Circle)
                     {
                        CircleShape shape = (CircleShape)fixture.getShape();
                        float radius = shape.getRadius();
                        int vertexCount = (int)(12f * radius);
                        float [] vertices = new float[vertexCount*2];
                        //System.out.println("SpatialFactory: radius: " + radius);
                        if (body.getType() == BodyType.StaticBody)
                        {
                           mTmp3.set(shape.getPosition());
                           for (int k = 0; k < vertexCount; k++)
                           {
                              // set the initial position
                              mTmp.set(radius,0);
                              // rotate it by 1/vertexCount * k
                              mTmp.rotate(360f*k/vertexCount);
                              // add it to the position.
                              mTmp.rotate(body.getAngle()*MathUtils.radiansToDegrees);
                              mTmp.add(mTmp3);
                              mTmp.add(bodyPos); // convert local coordinates to world coordinates to that textures are aligned
                              vertices[k*2] = mTmp.x*PolySpatial.PIXELS_PER_METER;
                              vertices[k*2+1] = mTmp.y*PolySpatial.PIXELS_PER_METER;
                           }
                           PolygonRegion region = new PolygonRegion(textureRegion, vertices);
                           PolySpatial spatial = new PolySpatial(region, Color.WHITE);
                           polySpatials.add(spatial);
                        }
                        else
                        {
                           mTmp3.set(shape.getPosition());
                           for (int k = 0; k < vertexCount; k++)
                           {
                              // set the initial position
                              mTmp.set(radius,0);
                              // rotate it by 1/vertexCount * k
                              mTmp.rotate(360f*k/vertexCount);
                              // add it to the position.
                              mTmp.add(mTmp3);
                              vertices[k*2] = mTmp.x*PolySpatial.PIXELS_PER_METER;
                              vertices[k*2+1] = mTmp.y*PolySpatial.PIXELS_PER_METER;
                           }
                           PolygonRegion region = new PolygonRegion(textureRegion, vertices);
                           PolySpatial spatial = new PolySpatial(region, body, Color.WHITE);
                           polySpatials.add(spatial);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void resize(int width, int height)
   {
   }

   @Override
   public void pause()
   {
   }

   @Override
   public void resume()
   {
   }

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
	    Body objcontact1;
	    Body objcontact2;
	    Boolean isPlayer = false;
	    String TipoA = scene.getCustom(contact.getFixtureA().getBody(), "tipo", (String) null);
	    String TipoB = scene.getCustom(contact.getFixtureB().getBody(), "tipo", (String) null);;
	
	    if(TipoA.equals("player")){		
	    	objcontact1=contact.getFixtureB().getBody();
	    	objcontact2 = null;
	    	isPlayer = true;
	    }else if(TipoB.equals("player")){
	    	objcontact1=contact.getFixtureA().getBody();	
	    	objcontact2 = null;
	    	isPlayer = true;
	    }else{
	    	objcontact1=contact.getFixtureA().getBody();
	    	objcontact2=contact.getFixtureB().getBody();		
	    }    	
	    
	    String tipopbj1 = scene.getCustom(objcontact1, "tipo", (String) null);
	    String tipopbj2 = scene.getCustom(objcontact2, "tipo", (String) null);
	    
	    if(isPlayer){
		    if((tipopbj1.equals("anguila") || tipopbj1.equals("bola")||tipopbj1.equals("morena")||tipopbj1.equals("roca"))&& isalert){
		    	Tween.to(alertbg, ImageAccessor.OPACITY, 2f)
				.waypoint(0.6f)
				.waypoint(0)
				.target(0)
				.setCallback(alertCallback)
				.setCallbackTriggers(TweenCallback.START | TweenCallback.COMPLETE)
				.start(tweenManager);
		    }
		    if(tipopbj1.equals("smorena")&& ismorenaatack){
		    	Morena morena = (Morena)objcontact1.getUserData();
		    	morena.StartAnimation(tweenManager);
		    }
		    if(tipopbj1.equals("coin") || tipopbj1.equals("bola")){
		    		RemoveElem.add(objcontact1);
		    		if(tipopbj1.equals("bola"))
		    			Minas.remove(objcontact1);
		    		if(tipopbj1.equals("coin"))
		    			Monedas.remove(objcontact1);
		    }
		    
	    }
	    
	    if(!isPlayer){
		    if((tipopbj1.equals("roca")&&tipopbj2.equals("piso"))||(tipopbj1.equals("piso")&&tipopbj2.equals("roca"))){
		    	Body roca;
		    	if(tipopbj1.equals("roca"))
		    		roca = objcontact1;
		    	else
		    		roca = objcontact2;
		    	Tween.to(roca, BodyAccessor.ROTATE, 1.5f)
		    	.target(0)
		    	.setUserData(roca)
		    	.setCallback(RocaCallback)
				.setCallbackTriggers(TweenCallback.START | TweenCallback.COMPLETE)
				.start(tweenManager);	    		    	
		    }
	    }   
	}
	
	TweenCallback alertCallback = new TweenCallback() {
		@Override public void onEvent(int type, BaseTween source) {
			switch (type) {
				case START: System.out.println("Start Alert");break;
				case COMPLETE:isalert = true; break;
			}
		}
	};
	
	TweenCallback RocaCallback = new TweenCallback() {
		@Override public void onEvent(int type, BaseTween source) {
			Body roca = (Body)source.getUserData();
			Vector2 posroca = new Vector2(scene.getCustom(roca, "val1", (Vector2) null));
			switch (type) {
				case START:
					roca.setLinearVelocity(0, 0);
					roca.setAngularVelocity(0);
					roca.setTransform(posroca.x, posroca.y,0);
					roca.setActive(false);break;
				case COMPLETE: 
					roca.setActive(true);
					break;
			}
		}
	};
	
	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

	private void AgregarListener(){
		joystick2.addListener(new ChangeListener() {
	         @Override
	         public void changed (ChangeEvent event, Actor actor) {
	            Touchpad touchpad = (Touchpad) actor;
	           porcentx = (touchpad.getKnobPercentX()*-0.035f);
	           porcenty = (touchpad.getKnobPercentY()*-0.035f);
	           
	           int TempAnguloEfect = 0;
	           Vector2 Direccion = new Vector2(touchpad.getKnobPercentX(),touchpad.getKnobPercentY());
	           //Mag = Math.sqrt(Math.pow(touchpad.getKnobPercentX(), 2)+Math.pow(touchpad.getKnobPercentY(),2));
	           Float Angulo = Direccion.angle();
	           
	           if(Angulo>=67.5 &&  Angulo < 112.5){
	           	TempAnguloEfect = N;
	           }else if (Angulo>=112.5 &&  Angulo < 157.5){
	           	TempAnguloEfect = NW;
	           }else if (Angulo>=22.5 &&  Angulo < 67.5){
	           	TempAnguloEfect = NE;
	           }else if (Angulo>=157.5 &&  Angulo < 202.5){
	           	TempAnguloEfect = W;
	           }else if (Angulo>=337.5f ||  Angulo < 22.5f){
	           	TempAnguloEfect = E;
	           }else if (Angulo>=202.5 &&  Angulo < 247.5){
	           	TempAnguloEfect = SW;
	           }else if (Angulo>=292.5 &&  Angulo < 337.5){
	           	TempAnguloEfect = SE;
	           }else if (Angulo>=247.5 &&  Angulo < 292.5){
	           	TempAnguloEfect = S;
	           }
	            
	           if(TempAnguloEfect != AnguloEfect){
	           	AnguloEfect = TempAnguloEfect; 
	           	switch (AnguloEfect){
	           		case N: 
	           			emitter_1.getAngle().setHigh(75, 105);
	           			emitter_1.getXOffsetValue().setLow(0f);
	           			emitter_1.getYOffsetValue().setLow(0.9f);
	           			emitter_2.getAngle().setHigh(120, 150);
	           			emitter_2.getXOffsetValue().setLow(-0.75f);
	           			emitter_2.getYOffsetValue().setLow(0.75f);
	           			emitter_3.getAngle().setHigh(30, 60);
	           			emitter_3.getXOffsetValue().setLow(0.75f);
	           			emitter_3.getYOffsetValue().setLow(0.75f);
	           		
	           			break;
	           		case NW:
	           			emitter_1.getAngle().setHigh(120, 150);
	           			emitter_1.getXOffsetValue().setLow(-0.75f);
	           			emitter_1.getYOffsetValue().setLow(0.75f);
	           			emitter_2.getAngle().setHigh(165, 195);
	           			emitter_2.getXOffsetValue().setLow(-0.9f);
	           			emitter_2.getYOffsetValue().setLow(0f);
	           			emitter_3.getAngle().setHigh(75, 105);
	           			emitter_3.getXOffsetValue().setLow(0f);
	           			emitter_3.getYOffsetValue().setLow(0.9f);
	        	   		break;
	            	case NE:
	            		emitter_1.getAngle().setHigh(30, 60);
	            		emitter_1.getXOffsetValue().setLow(0.75f);
	            		emitter_1.getYOffsetValue().setLow(0.75f);
	            		emitter_2.getAngle().setHigh(75, 105);
	            		emitter_2.getXOffsetValue().setLow(0f);
	            		emitter_2.getYOffsetValue().setLow(0.9f);
	            		emitter_3.getAngle().setHigh(-5, 15);
	            		emitter_3.getXOffsetValue().setLow(0.9f);
	            		emitter_3.getYOffsetValue().setLow(0f);
	            		break;
	            	case W:
	            		emitter_1.getAngle().setHigh(165, 195);
	            		emitter_1.getXOffsetValue().setLow(-0.9f);
	            		emitter_1.getYOffsetValue().setLow(0f);
	            		emitter_2.getAngle().setHigh(210, 240);
	            		emitter_2.getXOffsetValue().setLow(-0.75f);
	            		emitter_2.getYOffsetValue().setLow(-0.75f);
	            		emitter_3.getAngle().setHigh(120, 150);
	            		emitter_3.getXOffsetValue().setLow(-0.75f);
	            		emitter_3.getYOffsetValue().setLow(0.75f);
	            		break;
	            	case E:
	            		emitter_1.getAngle().setHigh(-5, 15);
	            		emitter_1.getXOffsetValue().setLow(0.9f);
	            		emitter_1.getYOffsetValue().setLow(0f);
	            		emitter_2.getAngle().setHigh(30, 60);
	            		emitter_2.getXOffsetValue().setLow(0.75f);
	            		emitter_2.getYOffsetValue().setLow(0.75f);
	            		emitter_3.getAngle().setHigh(300, 330);
	            		emitter_3.getXOffsetValue().setLow(0.75f);
	            		emitter_3.getYOffsetValue().setLow(-0.75f);
	            		break;
	            	case SW:
	            		emitter_1.getAngle().setHigh(210, 240);
	            		emitter_1.getXOffsetValue().setLow(-0.75f);
	            		emitter_1.getYOffsetValue().setLow(-0.75f);
	            		emitter_2.getAngle().setHigh(255, 285);
	            		emitter_2.getXOffsetValue().setLow(0f);
	            		emitter_2.getYOffsetValue().setLow(-0.9f);
	            		emitter_3.getAngle().setHigh(175, 195);
	            		emitter_3.getXOffsetValue().setLow(-0.9f);
	            		emitter_3.getYOffsetValue().setLow(0f);
	            		break;
	            	case SE:
	            		emitter_1.getAngle().setHigh(300, 330);
	            		emitter_1.getXOffsetValue().setLow(0.75f);
	            		emitter_1.getYOffsetValue().setLow(-0.75f);
	            		emitter_2.getAngle().setHigh(-5, 15);
	            		emitter_2.getXOffsetValue().setLow(0.9f);
	            		emitter_2.getYOffsetValue().setLow(0f);
	            		emitter_3.getAngle().setHigh(255, 285);
	            		emitter_3.getXOffsetValue().setLow(0f);
	            		emitter_3.getYOffsetValue().setLow(-0.9f);
	            		break;
	            	case  S:
	            		emitter_1.getAngle().setHigh(255, 285);
	            		emitter_1.getXOffsetValue().setLow(0f);
	            		emitter_1.getYOffsetValue().setLow(-0.9f);
	            		emitter_2.getAngle().setHigh(300, 330);
	            		emitter_2.getXOffsetValue().setLow(0.75f);
	            		emitter_2.getYOffsetValue().setLow(-0.75f);
	            		emitter_3.getAngle().setHigh(210, 240);
	            		emitter_3.getXOffsetValue().setLow(-0.75f);
	            		emitter_3.getYOffsetValue().setLow(-0.75f);
	            		break;
	            	}
	            }
	         }
	         
	      });
		joystick2.addListener(new InputListener() {
	    	public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	    		emitter_1.setContinuous(true);
	    		emitter_2.setContinuous(true);
	    		emitter_3.setContinuous(true);
	            return true;
	    }
	    	public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	    		emitter_1.setContinuous(false);
	    		emitter_2.setContinuous(false);
	    		emitter_3.setContinuous(false);
	    	}
	    });
	}
	
	private Integer findSpite(String name, Sprite sprite){
		int size = ListImgMap.size;
		for(int i = 0 ; i < size ; i++){
			if(name.equals(ListImgMap.get(i).getName())){
				return i;
			}
		}
		ListImgMap.add(new NamedSprite(name,sprite));
		return ListImgMap.size-1;
	}

}
