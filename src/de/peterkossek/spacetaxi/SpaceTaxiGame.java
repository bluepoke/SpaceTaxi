package de.peterkossek.spacetaxi;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;

/**
 * This is the SpaceTaxiGame Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class SpaceTaxiGame extends SimpleApplication implements AnalogListener, ActionListener {

    public static void main(String[] args) {
        SpaceTaxiGame app = new SpaceTaxiGame();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        spaceCraft();
        
    }


    private Geometry createFloor(BulletAppState bullet) {
        Box floorBox = new Box(10, 0.1f, 10);
        Material floorMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floorMat.setColor("Color", ColorRGBA.Gray);
        Geometry floor = new Geometry("floor", floorBox);
        floor.setMaterial(floorMat);
        floor.setLocalTranslation(0, -5f, 0);
        CollisionShape floorCollShape = CollisionShapeFactory.createBoxShape(floor);
        final RigidBodyControl floorRigidBody = new RigidBodyControl(floorCollShape);
        floor.addControl(floorRigidBody);
        floorRigidBody.setApplyPhysicsLocal(true);
        bullet.getPhysicsSpace().add(floor);
        //floorRigidBody.setGravity(Vector3f.ZERO);
        floorRigidBody.setMass(0f);
        floor.setShadowMode(RenderQueue.ShadowMode.Receive);
        rootNode.attachChild(floor);
        return floor;
    }

    private DirectionalLight createSun() {
        DirectionalLight sun = new DirectionalLight(new Vector3f(-1f, -1f, -1f));
        sun.setColor(ColorRGBA.White);

        rootNode.addLight(sun);

        DirectionalLightShadowRenderer lightRend = new DirectionalLightShadowRenderer(assetManager, 4096, 4);
        lightRend.setLight(sun);
        lightRend.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        viewPort.addProcessor(lightRend);
        return sun;
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void spaceCraft() {
        
        
        BulletAppState bullet = new BulletAppState();

        stateManager.attach(bullet);

        bullet.getPhysicsSpace().setGravity(new Vector3f(0, -4f, 0));
        
        DirectionalLight sun = createSun();
        
        Geometry floor = createFloor(bullet);
        
        
        Box bodyBox = new Box(2, 0.5f, 2);
        Geometry spaceCraftBody = new Geometry("SpacecraftBody", bodyBox);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        
        spaceCraftNode.attachChild(spaceCraftBody);
        Box legBox = new Box(0.1f, 1f, 0.1f);
        
        Geometry leg1 = new Geometry("Leg1", legBox);
        leg1.setLocalTranslation(-2, -1, -2);
        spaceCraftNode.attachChild(leg1);
        
        Geometry leg2 = new Geometry("Leg2", legBox);
        leg2.setLocalTranslation(-2, -1, 2);
        spaceCraftNode.attachChild(leg2);
        
        Geometry leg3 = new Geometry("Leg2", legBox);
        leg3.setLocalTranslation(2, -1, 2);
        spaceCraftNode.attachChild(leg3);
        
        Geometry leg4 = new Geometry("Leg2", legBox);
        leg4.setLocalTranslation(2, -1, -2);
        spaceCraftNode.attachChild(leg4);
        
        CollisionShape collShape = CollisionShapeFactory.createMeshShape(spaceCraftNode);
        RigidBodyControl rigidBodyControl = new RigidBodyControl(collShape);
        spaceCraftNode.addControl(rigidBodyControl);
        bullet.getPhysicsSpace().addAll(spaceCraftNode);
        
        spaceCraftNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        spaceCraftNode.setMaterial(material);
        rootNode.attachChild(spaceCraftNode);
        
        inputManager.addMapping("Thrust_up", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addListener(this, "Thrust_up");
        
        ParticleEmitter fire = new ParticleEmitter("Thruster", ParticleMesh.Type.Triangle, 30);
        Material mat_fire = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat_fire.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        fire.setMaterial(mat_fire);
        fire.setImagesX(2);
        fire.setImagesY(2);
        fire.setNumParticles(200);
        fire.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f));
        fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f));
        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, -5, 0));
        fire.setStartSize(0.2f);
        fire.setEndSize(0.01f);
        fire.setGravity(Vector3f.ZERO);
        fire.setLowLife(0.2f);
        fire.setHighLife(1f);
        fire.getParticleInfluencer().setVelocityVariation(0.3f);
        fire.setLocalTranslation(0, -0.55f, 0);
        fire.setParticlesPerSec(0);

        spaceCraftNode.attachChild(fire);
        
        ParticleEmitter smoke = new ParticleEmitter("Smoke", ParticleMesh.Type.Triangle, 30);
        Material mat_smoke = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        mat_smoke.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
        smoke.setMaterial(mat_smoke);
        smoke.setImagesX(2);
        smoke.setImagesY(2);
        smoke.setNumParticles(200);
        smoke.setEndColor(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.4f));
        smoke.setStartColor(new ColorRGBA(0f, 0f, 0f, 0.8f));
        smoke.getParticleInfluencer().setInitialVelocity(new Vector3f(0, -5, 0));
        smoke.setStartSize(0.5f);
        smoke.setEndSize(0.75f);
        smoke.setGravity(Vector3f.ZERO);
        smoke.setLowLife(1f);
        smoke.setHighLife(2f);
        smoke.getParticleInfluencer().setVelocityVariation(0.3f);
        smoke.setLocalTranslation(0, -0.55f, 0);
        smoke.setParticlesPerSec(0);

        spaceCraftNode.attachChild(smoke);
        
        fireLight = new PointLight(new Vector3f(0, -0.55f, 0), ColorRGBA.Yellow, 500);
        fireLight.setEnabled(true);
        spaceCraftNode.addLight(fireLight);
    }

    private final Node spaceCraftNode = new Node("SpaceCraftNode");
    private PointLight fireLight;

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("Thrust_up")) {
            Quaternion rotation = spaceCraftNode.getLocalRotation();
            Vector3f force = Vector3f.UNIT_Y.mult(10);
            force = rotation.mult(force);
            spaceCraftNode.getControl(RigidBodyControl.class).applyForce(force, Vector3f.ZERO);
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("Thrust_up")) {
            ParticleEmitter emitter = (ParticleEmitter)spaceCraftNode.getChild("Thruster");
            emitter.setParticlesPerSec(isPressed?100:0);
            emitter = (ParticleEmitter)spaceCraftNode.getChild("Smoke");
            emitter.setParticlesPerSec(isPressed?100:0);
            
            fireLight.setEnabled(isPressed);
        }
    }
    
}
