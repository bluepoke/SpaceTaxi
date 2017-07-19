/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.peterkossek.spacetaxi;

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
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.LightNode;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowRenderer;

/**
 *
 * @author Kossek
 */
public class SpaceShip extends Node implements AnalogListener, ActionListener {

    private PointLight fireLight;
    private PointLightShadowRenderer fireShadow;
    private final SpaceTaxiGame game;

    public SpaceShip(SpaceTaxiGame game) {
        super("SpaceShip");
        this.game = game;
       
        Box bodyBox = new Box(2, 0.5f, 2);
        
        Material material = game.getAssetManager().loadMaterial("Materials/ship.j3m");
        addGeom(bodyBox, "SpaceCraftBody", Vector3f.ZERO);
        
        
        Box legBox = new Box(0.1f, 1f, 0.1f);
        
        addGeom(legBox, "Leg1", new Vector3f(-2, -1, -2));
        addGeom(legBox, "Leg2", new Vector3f(+2, -1, -2));
        addGeom(legBox, "Leg3", new Vector3f(+2, -1, +2));
        addGeom(legBox, "Leg4", new Vector3f(-2, -1, +2));
        
        CollisionShape collShape = CollisionShapeFactory.createMeshShape(this);
        RigidBodyControl rigidBodyControl = new RigidBodyControl(collShape);
        addControl(rigidBodyControl);
        game.getBullet().getPhysicsSpace().addAll(this);
        
        setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        setMaterial(material);
        game.getRootNode().attachChild(this);
        
        game.getInputManager().addMapping(THRUST_CENTER, new KeyTrigger(KeyInput.KEY_LSHIFT));
        game.getInputManager().addListener(this, THRUST_CENTER);
        game.getInputManager().addMapping(THRUST_RIGHT, new KeyTrigger(KeyInput.KEY_J));
        game.getInputManager().addListener(this, THRUST_RIGHT);
        game.getInputManager().addMapping(THRUST_LEFT, new KeyTrigger(KeyInput.KEY_L));
        game.getInputManager().addListener(this, THRUST_LEFT);
        game.getInputManager().addMapping(THRUST_FRONT, new KeyTrigger(KeyInput.KEY_I));
        game.getInputManager().addListener(this, THRUST_FRONT);
        game.getInputManager().addMapping(THRUST_BACK, new KeyTrigger(KeyInput.KEY_K));
        game.getInputManager().addListener(this, THRUST_BACK);
        
        final Vector3f steamTranslation = new Vector3f(0, -0.55F, 0);
        final Vector3f steamVelocity = new Vector3f(0, -5, 0);
        
        attachChild(createSteamEmitter(THRUST_CENTER, steamTranslation, steamVelocity));
        attachChild(createSteamEmitter(THRUST_RIGHT, steamTranslation.add(2, 0, 0), steamVelocity));
        attachChild(createSteamEmitter(THRUST_LEFT, steamTranslation.add(-2, 0, 0), steamVelocity));
        attachChild(createSteamEmitter(THRUST_FRONT, steamTranslation.add(0, 0, 2), steamVelocity));
        attachChild(createSteamEmitter(THRUST_BACK, steamTranslation.add(0, 0, -2), steamVelocity));
        
        
        createFireLight();
        
    }
    private static final String THRUST_CENTER = "Thrust_up";
    private static final String THRUST_RIGHT = "Thrust_right";
    private static final String THRUST_LEFT = "Thrust_left";
    private static final String THRUST_FRONT = "Thrust_front";
    private static final String THRUST_BACK = "Thrust_back";

    private ParticleEmitter createSteamEmitter(String name, Vector3f translation, Vector3f velocity) {
        ParticleEmitter smoke = new ParticleEmitter(name, ParticleMesh.Type.Triangle, 30);
        Material mat_smoke = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Particle.j3md");
        mat_smoke.setTexture("Texture", game.getAssetManager().loadTexture("Effects/Explosion/flame.png"));
        smoke.setMaterial(mat_smoke);
        smoke.setImagesX(2);
        smoke.setImagesY(2);
        smoke.setNumParticles(1000);
        smoke.setStartColor(new ColorRGBA(1, 1, 1, 0.8f));
        smoke.setEndColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 0.2f));
        smoke.getParticleInfluencer().setInitialVelocity(velocity);
        smoke.setStartSize(0.1f);
        smoke.setEndSize(0.25f);
        smoke.setGravity(Vector3f.ZERO);
        smoke.setLowLife(1f);
        smoke.setHighLife(2f);
        smoke.getParticleInfluencer().setVelocityVariation(0.1f);
        smoke.setLocalTranslation(translation);
        smoke.setParticlesPerSec(0);
        return smoke;
    }

    private void createFireLight() {
        fireLight = new PointLight();
        fireLight.setColor(ColorRGBA.Yellow);
        fireLight.setRadius(0.1f);
        LightNode ln = new LightNode("fireLight", fireLight);
        ln.setLocalTranslation(0, -0.55f, 0);
        attachChild(ln);
        
        fireShadow = new PointLightShadowRenderer(game.getAssetManager(), 4096);
        fireShadow.setLight(fireLight);
        fireShadow.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        
        
        fireLight.setEnabled(false);
        addLight(fireLight);
    }

    private void addGeom(Mesh mesh, String name, Vector3f v) {
        Geometry geom = new Geometry(name, mesh);
        geom.setLocalTranslation(v);
        attachChild(geom);
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        
        
        switch (name) {
            case THRUST_CENTER:
                thrust(THRUST_FORCE, Vector3f.ZERO);
                break;
            case THRUST_RIGHT:
                thrust(ROTATE_FORCE, new Vector3f(2, 0, 0));
                break;
            case THRUST_LEFT:
                thrust(ROTATE_FORCE, new Vector3f(-2, 0, 0));
                break;
            case THRUST_FRONT:
                thrust(ROTATE_FORCE, new Vector3f(0, 0, 2));
                break;
            case THRUST_BACK:
                thrust(ROTATE_FORCE, new Vector3f(0, 0, -2));
                break;
                
        }
        
    }
    
    private void thrust(int forceMultiplier, Vector3f position) {
        Quaternion rotation = getLocalRotation();
        Vector3f force = Vector3f.UNIT_Y.mult(forceMultiplier);
        force = rotation.mult(force);
        getControl(RigidBodyControl.class).applyForce(force, position);
    }
    private static final int THRUST_FORCE = 10;
    private static final int ROTATE_FORCE = 3;

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        
            ParticleEmitter emitter = (ParticleEmitter)getChild(name);
            emitter.setParticlesPerSec(isPressed?100:0);
            
            fireLight.setEnabled(isPressed);
            if (isPressed) {
                game.getViewPort().addProcessor(fireShadow);
            } else {
                game.getViewPort().removeProcessor(fireShadow);
            }
        
    }

}
