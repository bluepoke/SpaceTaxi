package de.peterkossek.spacetaxi;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;

/**
 * This is the SpaceTaxiGame Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class SpaceTaxiGame extends SimpleApplication implements AnalogListener {

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
    }
    private Node spaceCraftNode = new Node("SpaceCraftNode");
    

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (name.equals("Thrust_up")) {
            spaceCraftNode.getControl(RigidBodyControl.class).applyForce(Vector3f.UNIT_Y.mult(10), Vector3f.ZERO);
        }
    }
}
