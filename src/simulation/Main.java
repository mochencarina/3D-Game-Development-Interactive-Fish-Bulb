package simulation;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.bullet.util.CollisionShapeFactory;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

import com.jme3.material.Material;
import com.jme3.material.RenderState;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.PssmShadowFilter;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer.CompareMode;
import com.jme3.shadow.PssmShadowRenderer.FilterMode;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;


public class Main extends SimpleApplication {

    private AnimChannel channel;
    private AnimControl control;
    Node pivot;
    public static final int SHADOWMAP_SIZE = 1024;
    private PssmShadowRenderer pssmRenderer;

    public static void main(String args[]) {
        Main app = new Main();
        app.start();
    }

    
  /** Prepare the Physics Application State (jBullet) */
    private BulletAppState bulletAppState;
    private RigidBodyControl box1bottom_phy;
    private RigidBodyControl box1bottomup_phy;
    private RigidBodyControl box1side1_phy;
    private RigidBodyControl box1side2_phy;
    private RigidBodyControl box1side3_phy;
    private RigidBodyControl box1side4_phy;
    private RigidBodyControl box2bottom_phy;
    private RigidBodyControl box2side1_phy;
    private RigidBodyControl box2side2_phy;
    private RigidBodyControl box2side3_phy;
    private RigidBodyControl box2side4_phy;
    private RigidBodyControl box3bottom_phy;
    private RigidBodyControl box3side1_phy;
    private RigidBodyControl box3side2_phy;
    private RigidBodyControl box3side3_phy;
    private RigidBodyControl box3side4_phy;
    private RigidBodyControl ball_phy;
    private RigidBodyControl floor_phy;
    private RigidBodyControl fish_phy;
    private Node spatial;
    private HingeJoint joint1;
    private HingeJoint joint2;
    private HingeJoint joint3;
    private boolean left = false, right = false, up = false, down = false, forword = false, backword = false;

    @Override
    public void simpleInitApp() {
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);        
        initFloor();
        initBox();
        initFish();
        initKey();
        
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        SSAOFilter ssaoFilter = new SSAOFilter(12.94f, 43.92f, 0.33f, 0.61f);
        fpp.addFilter(ssaoFilter);
        viewPort.addProcessor(fpp);
        
        cam.setLocation(new Vector3f(0f,-2f,19f));
        flyCam.setDragToRotate(true);
        setDisplayFps(false);
        setDisplayStatView(false);
//        flyCam.setEnabled(true);
//        viewPort.setBackgroundColor(new ColorRGBA(0.5f, 0.8f, 1f, 0f));
  }

  
 private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("shoot") && !keyPressed) {
                initBall();
            }
            if (name.equals("forword")) {
                forword = keyPressed;
            }
            if (name.equals("backword")) {
                backword = keyPressed;
            }
            if (name.equals("up")) {
                up = keyPressed;
            }
            if (name.equals("down")) {
                down = keyPressed;
            }
            if (name.equals("left")) {
                left = keyPressed;
            }
            if (name.equals("right")) {
                right = keyPressed;
            }
            if (name.equals("SwimForward") && !keyPressed) {
                if (!channel.getAnimationName().equals("swimforward")) {
                    channel.setAnim("swimforward", 0.50f);
                    channel.setLoopMode(LoopMode.Loop);
                } else {
                    channel.setAnim("stop", 0.50f);
                    channel.setLoopMode(LoopMode.Loop);
                }
            }
            if (name.equals("Stationary") && !keyPressed) {
                if (!channel.getAnimationName().equals("stationary")) {
                    channel.setAnim("stationary", 0.50f);
                    channel.setLoopMode(LoopMode.Loop);
                } else {
                    channel.setAnim("stop", 0.50f);
                    channel.setLoopMode(LoopMode.Loop);
                }
            }
            if (name.equals("Turn180Degree")) {
                channel.setAnim("turn180degree", 0.50f);
                channel.setLoopMode(LoopMode.DontLoop);
                channel.setLoopMode(LoopMode.DontLoop);
            }
        }
    };

    public void initKey(){
        inputManager.addMapping("shoot",new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("forword",new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("backword",new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addMapping("left",new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("right",new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("up",new KeyTrigger(KeyInput.KEY_PGUP));
        inputManager.addMapping("down",new KeyTrigger(KeyInput.KEY_PGDN));
        inputManager.addMapping("SwimForward", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Stationary", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("Turn180Degree", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(actionListener, "shoot");
        inputManager.addListener(actionListener, "forword");
        inputManager.addListener(actionListener, "backword");
        inputManager.addListener(actionListener, "left");
        inputManager.addListener(actionListener, "right");
        inputManager.addListener(actionListener, "up");
        inputManager.addListener(actionListener, "down");
        inputManager.addListener(actionListener, "SwimForward", "Stationary", "Turn180Degree");
    }
    
    /** Make a solid floor and add it to the scene. */
    public void initFloor(){
        Geometry floor = new Geometry("Floor", new Box(40f, 0.1f, 30f));
        Material matBase = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matBase.setTexture("ColorMap",
        assetManager.loadTexture("Textures/ice.jpg"));
//        matBase.setColor("Color", new ColorRGBA(1f, 0.8f, 0.8f, 0f)); 
        floor.setMaterial(matBase);
        floor.setLocalTranslation(0f, -10f, 0f);
        floor.setShadowMode(ShadowMode.Receive);
        floor_phy = new RigidBodyControl(0.0f);
        floor.addControl(floor_phy);
        bulletAppState.getPhysicsSpace().add(floor_phy);
        floor_phy.setRestitution(0.5f);
        rootNode.attachChild(floor);
        
        Texture west = getAssetManager().loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
        Texture east = getAssetManager().loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
        Texture north = getAssetManager().loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
        Texture south = getAssetManager().loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
        Texture up = getAssetManager().loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
        Texture down = getAssetManager().loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");
        getRootNode().attachChild(SkyFactory.createSky(getAssetManager(), west, east, north, south, up, down));
    }
   
    /** Initialize three boxes used in this scene. */
    public void initBox(){
        initBox1();
        initBox2();
        initBox3();
    }
    
    public void initBox1(){
        Geometry box1btm = new Geometry("Box1Bottom", new Box(1f, 0.1f, 1f));
        Material matBase = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matBase.setColor("Color", new ColorRGBA( 0.7f, 0.7f, 0.7f, 0.5f));
        matBase.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        box1btm.setMaterial(matBase);
        box1btm.setQueueBucket(RenderQueue.Bucket.Transparent);
        box1btm.setLocalTranslation(0f, 0f, 0f);
        box1btm.setShadowMode(ShadowMode.CastAndReceive);
        box1bottom_phy = new RigidBodyControl(0.5f);
        box1btm.addControl(box1bottom_phy);
        bulletAppState.getPhysicsSpace().add(box1bottom_phy);
        box1bottom_phy.setRestitution(0.5f);
        rootNode.attachChild(box1btm);
        
        Geometry box1sd1 = new Geometry("Box1Side1", new Box(0.1f,0.5f,1f));
        box1sd1.setMaterial(matBase);
        box1sd1.setQueueBucket(RenderQueue.Bucket.Transparent);
        box1sd1.setLocalTranslation(-1f, 0.5f, 0f);
        box1side1_phy = new RigidBodyControl(0.0f);
        box1sd1.addControl(box1side1_phy);
        bulletAppState.getPhysicsSpace().add(box1side1_phy);
        box1side1_phy.setRestitution(0.5f);
        rootNode.attachChild(box1sd1);
        
        Geometry box1sd2 = new Geometry("Box1Side2", new Box(0.1f,0.5f,1f));
        box1sd2.setMaterial(matBase);
        box1sd2.setQueueBucket(RenderQueue.Bucket.Transparent);
        box1sd2.setLocalTranslation(1f, 0.5f, 0f);
        box1side2_phy = new RigidBodyControl(0.0f);
        box1sd2.addControl(box1side2_phy);
        bulletAppState.getPhysicsSpace().add(box1side2_phy);
        box1side2_phy.setRestitution(0.5f);
        rootNode.attachChild(box1sd2);
        
        Geometry box1sd3 = new Geometry("Box1Side3", new Box(1f,0.5f,0.1f));
        box1sd3.setMaterial(matBase);
        box1sd3.setQueueBucket(RenderQueue.Bucket.Transparent);
        box1sd3.setLocalTranslation(0f, 0.5f, 1f);
        box1side3_phy = new RigidBodyControl(0.0f);
        box1sd3.addControl(box1side3_phy);
        bulletAppState.getPhysicsSpace().add(box1side3_phy);
        box1side3_phy.setRestitution(0.5f);
        rootNode.attachChild(box1sd3);
        
        Geometry box1sd4 = new Geometry("Box1Side4", new Box(1f,0.5f,0.1f));
        box1sd4.setMaterial(matBase);
        box1sd4.setQueueBucket(RenderQueue.Bucket.Transparent);
        box1sd4.setLocalTranslation(0f, 0.5f, -1f);
        box1side4_phy = new RigidBodyControl(0.0f);
        box1sd4.addControl(box1side4_phy);
        bulletAppState.getPhysicsSpace().add(box1side4_phy);
        box1side4_phy.setRestitution(0.5f);
        rootNode.attachChild(box1sd4);
                
        joint1 =new HingeJoint(box1side1_phy,      
                     box1bottom_phy,               
                     new Vector3f(0.1f, -0.5f, 0f),     
                     new Vector3f(-1f, 0.1f, 0f),  
                     Vector3f.UNIT_Z,              
                     Vector3f.UNIT_Z  );           
        joint1.enableMotor(true, -1f, .1f);
        bulletAppState.getPhysicsSpace().add(joint1);
        
        pssmRenderer = new PssmShadowRenderer(assetManager, 1024, 3);
        pssmRenderer.setDirection(new Vector3f(-0.5f, -0.5f, -0.5f).normalizeLocal());
        pssmRenderer.setLambda(0.55f);
        pssmRenderer.setShadowIntensity(0.6f);
        pssmRenderer.setCompareMode(CompareMode.Software);
        pssmRenderer.setFilterMode(FilterMode.Dither);
                
//        pssmRenderer.displayFrustum();
        viewPort.addProcessor(pssmRenderer);
    }
    
    
    public void initBox2(){
        
        Geometry box2btm = new Geometry("Box2Bottom", new Box(1f, 0.1f, 1f));
        Material matBase = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matBase.setColor("Color", new ColorRGBA( 0.7f, 0.7f, 0.7f, 0.5f));
        matBase.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        box2btm.setMaterial(matBase);
        box2btm.setQueueBucket(RenderQueue.Bucket.Transparent);
        box2btm.setLocalTranslation(-3f, -5f, 0f);
        box2btm.setShadowMode(ShadowMode.CastAndReceive);
        box2bottom_phy = new RigidBodyControl(0.5f);
        box2btm.addControl(box2bottom_phy);
        bulletAppState.getPhysicsSpace().add(box2bottom_phy);
        box2bottom_phy.setRestitution(0.5f);
        rootNode.attachChild(box2btm);
         
        Geometry box2sd1 = new Geometry("Box2Side1", new Box(0.1f,0.5f,1f));
        box2sd1.setMaterial(matBase);
        box2sd1.setQueueBucket(RenderQueue.Bucket.Transparent);
        box2sd1.setLocalTranslation(-4f,-4.5f, 0f);
        box2side1_phy = new RigidBodyControl(0.0f);
        box2sd1.addControl(box2side1_phy);
        bulletAppState.getPhysicsSpace().add(box2side1_phy);
        box2side1_phy.setRestitution(0.5f);
        rootNode.attachChild(box2sd1);
        
        Geometry box2sd2 = new Geometry("Box2Side2", new Box(0.1f,0.5f,1f));
        box2sd2.setMaterial(matBase);
        box2sd2.setQueueBucket(RenderQueue.Bucket.Transparent);
        box2sd2.setLocalTranslation(-2f, -4.5f, 0f);
        box2side2_phy = new RigidBodyControl(0.0f);
        box2sd2.addControl(box2side2_phy);
        bulletAppState.getPhysicsSpace().add(box2side2_phy);
        box2side2_phy.setRestitution(0.5f);
        rootNode.attachChild(box2sd2);
      
        Geometry box2sd3 = new Geometry("Box2Side3", new Box(1f,0.5f,0.1f));
        box2sd3.setMaterial(matBase);
        box2sd3.setQueueBucket(RenderQueue.Bucket.Transparent);
        box2sd3.setLocalTranslation(-3f, -4.5f, 1f);
        box2side3_phy = new RigidBodyControl(0.0f);
        box2sd3.addControl(box2side3_phy);
        bulletAppState.getPhysicsSpace().add(box2side3_phy);
        box2side3_phy.setRestitution(0.5f);
        rootNode.attachChild(box2sd3);
         
        Geometry box2sd4 = new Geometry("Box2Side4", new Box(1f,0.5f,0.1f));
        box2sd4.setMaterial(matBase);
        box2sd4.setQueueBucket(RenderQueue.Bucket.Transparent);
        box2sd4.setLocalTranslation(-3f, -4.5f, -1f);
        box2side4_phy = new RigidBodyControl(0.0f);
        box2sd4.addControl(box2side4_phy);
        bulletAppState.getPhysicsSpace().add(box2side4_phy);
        box1side4_phy.setRestitution(0.5f);
        rootNode.attachChild(box2sd4);
        
        joint2 =new HingeJoint(box2side1_phy,      
                     box2bottom_phy,               
                     new Vector3f(0.1f, -0.5f, 0f),     
                     new Vector3f(-1f, 0.1f, 0f),  
                     Vector3f.UNIT_Z,              
                     Vector3f.UNIT_Z  );           
        joint2.enableMotor(true, -1f, .1f);
        bulletAppState.getPhysicsSpace().add(joint2);
        
    }
     
    
    public void initBox3(){
        Geometry box3btm = new Geometry("Box3Bottom", new Box(1f, 0.1f, 1f));
        Material matBase = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matBase.setColor("Color", new ColorRGBA( 0.7f, 0.7f, 0.7f, 0.5f));
        matBase.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        box3btm.setMaterial(matBase);
        box3btm.setQueueBucket(RenderQueue.Bucket.Transparent);
        box3btm.setLocalTranslation(3f, -4f, 0f);
        box3bottom_phy = new RigidBodyControl(0.5f);
        box3btm.addControl(box3bottom_phy);
        bulletAppState.getPhysicsSpace().add(box3bottom_phy);
        box3bottom_phy.setRestitution(0.5f);
        rootNode.attachChild(box3btm);
        box3btm.setShadowMode(ShadowMode.CastAndReceive);
        Geometry box3sd1 = new Geometry("Box3Side1", new Box(0.1f,0.5f,1f));
        box3sd1.setMaterial(matBase);
        box3sd1.setQueueBucket(RenderQueue.Bucket.Transparent);
        box3sd1.setLocalTranslation(4f,-3.5f, 0f);
        box3side1_phy = new RigidBodyControl(0.0f);
        box3sd1.addControl(box3side1_phy);
        bulletAppState.getPhysicsSpace().add(box3side1_phy);
        box3side1_phy.setRestitution(0.5f);
        rootNode.attachChild(box3sd1);
        
        Geometry box3sd2 = new Geometry("Box3Side2", new Box(0.1f,0.5f,1f));
        box3sd2.setMaterial(matBase);
        box3sd2.setQueueBucket(RenderQueue.Bucket.Transparent);
        box3sd2.setLocalTranslation(2f, -3.5f, 0f);
        box3side2_phy = new RigidBodyControl(0.0f);
        box3sd2.addControl(box3side2_phy);
        bulletAppState.getPhysicsSpace().add(box3side2_phy);
        box3side2_phy.setRestitution(0.5f);
        rootNode.attachChild(box3sd2);
        
        Geometry box3sd3 = new Geometry("Box3Side3", new Box(1f,0.5f,0.1f));
        box3sd3.setMaterial(matBase);
        box3sd3.setQueueBucket(RenderQueue.Bucket.Transparent);
        box3sd3.setLocalTranslation(3f, -3.5f, 1f);
        box3side3_phy = new RigidBodyControl(0.0f);
        box3sd3.addControl(box3side3_phy);
        bulletAppState.getPhysicsSpace().add(box3side3_phy);
        box3side3_phy.setRestitution(0.5f);
        rootNode.attachChild(box3sd3);
        
        Geometry box3sd4 = new Geometry("Box3Side4", new Box(1f,0.5f,0.1f));
        box3sd4.setMaterial(matBase);
        box3sd4.setQueueBucket(RenderQueue.Bucket.Transparent);
        box3sd4.setLocalTranslation(3f, -3.5f, -1f);
        box3side4_phy = new RigidBodyControl(0.0f);
        box3sd4.addControl(box3side4_phy);
        bulletAppState.getPhysicsSpace().add(box3side4_phy);
        box3side4_phy.setRestitution(0.5f);
        rootNode.attachChild(box3sd4);
         
        joint3 =new HingeJoint(box3side2_phy,      
                     box3bottom_phy,               
                     new Vector3f(0.1f, -0.5f, 0f),     
                     new Vector3f(-1f, 0.1f, 0f),  
                     Vector3f.UNIT_Z,               
                     Vector3f.UNIT_Z  );           
        joint3.enableMotor(true, -1f, .1f);
        bulletAppState.getPhysicsSpace().add(joint3);
    }

    public void initBall(){
        Geometry ball_geo = new Geometry("cannon ball", new Sphere(30,30,0.3f));
        Material matBase = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matBase.setColor("Color", ColorRGBA.Red);                   
        ball_geo.setMaterial(matBase);
        ball_geo.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(ball_geo);
        ball_geo.setLocalTranslation(3*initRan(),4.5f,initRan());
        ball_phy = new RigidBodyControl(20f);
        ball_geo.addControl(ball_phy);
        bulletAppState.getPhysicsSpace().add(ball_phy);
        ball_phy.setLinearVelocity(new Vector3f(2*initRan(),2*initRan(),2*initRan()));
        ball_phy.setRestitution(0.5f);
        ball_phy.setMass(0.1f);   
    }
    
    public float initRan(){
        float rd = (float) Math.random();
        double i = Math.random();
        if(i<0.5){
            rd=-rd;
        }
        return rd;
    }
    
    public void initFish(){
        
        spatial = (Node)assetManager.loadModel("Models/Blub/bluefish.j3o");
        rootNode.attachChild(spatial);
        spatial.setShadowMode(ShadowMode.CastAndReceive);
        spatial.setLocalTranslation(0f, -5f, 0f);
        control = spatial.getChild("bluefish").getControl(AnimControl.class);
        System.out.println(control.getAnimationNames());
        channel = control.createChannel();
        channel.setAnim("stop");
        CollisionShape shape = CollisionShapeFactory.createMeshShape(spatial);
        DirectionalLight sunLight = new DirectionalLight();
        sunLight.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
        sunLight.setColor(ColorRGBA.Gray);
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White.mult(0.5f));
        fish_phy = new RigidBodyControl(0.5f);
        spatial.addControl(fish_phy);
        bulletAppState.getPhysicsSpace().add(fish_phy);
        
        rootNode.addLight(sunLight);
        rootNode.addLight(ambientLight);
        
        /* Drop shadows */
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, SHADOWMAP_SIZE, 3);
        dlsr.setLight(sunLight);
        viewPort.addProcessor(dlsr);

        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, SHADOWMAP_SIZE, 3);
        dlsf.setLight(sunLight);
        dlsf.setEnabled(true);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
        viewPort.addProcessor(fpp);
        
        
    }
    
  
@Override
    public void simpleUpdate(float tpf) {
        if (forword) {
            fish_phy.applyCentralForce(new Vector3f(0f,0f,-10f));
        }
        if (backword) {
            fish_phy.applyCentralForce(new Vector3f(0f,0f,10f));
        }
        if (up) {
            fish_phy.applyCentralForce(new Vector3f(0f,10f,0f));
        }
        if (down) {
            fish_phy.applyCentralForce(new Vector3f(0f,-10f,0f));
        }
        if (left) {
            fish_phy.applyCentralForce(new Vector3f(-10f,0f,0f));
        }
        if (right) {
            fish_phy.applyCentralForce(new Vector3f(10f,0f,0f));
        }
    }
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
       
    }
}