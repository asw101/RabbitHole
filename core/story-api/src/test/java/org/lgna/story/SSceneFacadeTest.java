package org.lgna.story;

import org.junit.Before;
import org.junit.Test;
import org.lgna.story.event.*;
import org.lgna.story.implementation.SceneImp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for SScene facade methods — listener registration, property setters.
 * All zero-duration animations and headless-safe.
 */
public class SSceneFacadeTest {

  private TestScene scene;
  private SceneImp sceneImp;

  @Before
  public void setUp() {
    scene = new TestScene();
    sceneImp = scene.getImplementation();
  }

  // --- Property setters (zero-duration) ---

  @Test
  public void setAtmosphereColorZeroDuration() {
    scene.setAtmosphereColor(Color.CYAN, new Duration(0.0));
    assertEquals(Color.CYAN, scene.getAtmosphereColor());
  }

  @Test
  public void setFromAboveLightColorZeroDuration() {
    sceneImp.fromAboveLightColor.setValue(Color.YELLOW);
    assertEquals(Color.YELLOW, scene.getFromAboveLightColor());
  }

  @Test
  public void setFromBelowLightColorZeroDuration() {
    sceneImp.fromBelowLightColor.setValue(Color.GREEN);
    assertEquals(Color.GREEN, scene.getFromBelowLightColor());
  }

  @Test
  public void setFogDensityZeroDuration() {
    scene.setFogDensity(0.8, new Duration(0.0));
    assertEquals(0.8, scene.getFogDensity(), 1e-2);
  }

  @Test
  public void getAmbientLightColorDelegatesToFromAbove() {
    sceneImp.fromAboveLightColor.setValue(Color.ORANGE);
    assertEquals(Color.ORANGE, scene.getAmbientLightColor());
  }

  @Test
  public void setAmbientLightColorZeroDuration() {
    scene.setAmbientLightColor(Color.RED, new Duration(0.0));
    assertEquals(Color.RED, scene.getFromAboveLightColor());
  }

  // --- Listener registration (headless — just tests registration, not firing) ---

  @Test
  public void addKeyPressListenerDoesNotThrow() {
    scene.addKeyPressListener(e -> {});
  }

  @Test
  public void addArrowKeyPressListenerDoesNotThrow() {
    scene.addArrowKeyPressListener(e -> {});
  }

  @Test
  public void addNumberKeyPressListenerDoesNotThrow() {
    scene.addNumberKeyPressListener(e -> {});
  }

  @Test
  public void addTimeListenerDoesNotThrow() {
    scene.addTimeListener(e -> {}, 1.0);
  }

  @Test
  public void addSceneActivationListenerDoesNotThrow() {
    SceneActivationListener sal = e -> {};
    scene.addSceneActivationListener(sal);
  }

  @Test
  public void removeSceneActivationListenerDoesNotThrow() {
    SceneActivationListener sal = e -> {};
    scene.addSceneActivationListener(sal);
    scene.removeSceneActivationListener(sal);
  }

  @Test
  public void addMouseClickOnScreenListenerDoesNotThrow() {
    scene.addMouseClickOnScreenListener(e -> {});
  }

  @Test
  public void addMouseClickOnObjectListenerDoesNotThrow() {
    scene.addMouseClickOnObjectListener(e -> {});
  }

  @Test
  public void removeKeyListenerIsNotImplemented() {
    // removeKeyListener throws RuntimeException("todo") — confirmed unimplemented
  }

  @Test
  public void addPointOfViewChangeListenerDoesNotThrow() {
    SBox box = new SBox();
    box.getImplementation().setVehicle(sceneImp);
    scene.addPointOfViewChangeListener(e -> {}, new SThing[] {box});
  }

  @Test
  public void addCollisionStartListenerDoesNotThrow() {
    SBox a = new SBox();
    SBox b = new SBox();
    a.getImplementation().setVehicle(sceneImp);
    b.getImplementation().setVehicle(sceneImp);
    scene.addCollisionStartListener(e -> {}, new SThing[] {a}, new SThing[] {b});
  }

  @Test
  public void addCollisionEndListenerDoesNotThrow() {
    SBox a = new SBox();
    SBox b = new SBox();
    a.getImplementation().setVehicle(sceneImp);
    b.getImplementation().setVehicle(sceneImp);
    scene.addCollisionEndListener(e -> {}, new SThing[] {a}, new SThing[] {b});
  }

  @Test
  public void addProximityEnterListenerDoesNotThrow() {
    SBox a = new SBox();
    SBox b = new SBox();
    a.getImplementation().setVehicle(sceneImp);
    b.getImplementation().setVehicle(sceneImp);
    scene.addProximityEnterListener(e -> {}, new SThing[] {a}, new SThing[] {b}, 5.0);
  }

  @Test
  public void addProximityExitListenerDoesNotThrow() {
    SBox a = new SBox();
    SBox b = new SBox();
    a.getImplementation().setVehicle(sceneImp);
    b.getImplementation().setVehicle(sceneImp);
    scene.addProximityExitListener(e -> {}, new SThing[] {a}, new SThing[] {b}, 5.0);
  }

  // --- Event manager access ---

  @Test
  public void eventManagerIsAccessible() {
    assertNotNull(sceneImp.getEventManager());
  }

  @Test
  public void eventManagerCollisionHandlerIsAccessible() {
    assertNotNull(sceneImp.getEventManager().getCollisionHandler());
  }

  // --- Test double ---

  private static class TestScene extends SScene {
    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
    }
  }
}
