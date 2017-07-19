package de.peterkossek.spacetaxi;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import static com.jme3.bullet.PhysicsSpace.getPhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowRenderer;

/**
 * This is the SpaceTaxiGame Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class SpaceTaxiGame extends SimpleApplication {

    private final BulletAppState bullet = new BulletAppState();
    
    public static void main(String[] args) {
        SpaceTaxiGame app = new SpaceTaxiGame();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        setupPhysics();
        createSun();
        createFloor();
        spaceCraft();
    }

    public BulletAppState getBullet() {
        return bullet;
    }
    
    


    private Geometry createFloor() {
        Box floorBox = new Box(10, 0.1f, 10);
        Material floorMat = assetManager.loadMaterial("Materials/floor.j3m");
        
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
        
        new SpaceShip(this);
        
    }

    private final Node spaceCraftNode = new Node("SpaceCraftNode");
    private PointLight fireLight;
    private PointLightShadowRenderer fireShadow;

    

    private void setupPhysics() {
        getStateManager().attach(bullet);
        getPhysicsSpace().setGravity(new Vector3f(0, -4f, 0));
    }
    
}
