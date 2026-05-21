package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.Scene;
import org.junit.Before;
import org.junit.Test;
import org.lgna.story.Color;
import org.lgna.story.SScene;
import org.lgna.story.implementation.eventhandling.EventManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Behavior tests for SceneImp — construction, properties (atmosphere, fog, lighting),
 * event manager initialization, and camera registry queries.
 *
 * <p>Uses a {@code TestScene} subclass of the abstract {@link SScene} with a no-op
 * {@code handleActiveChanged}. All operations are headless-safe.
 */
public class SceneImpTest {

  private TestScene scene;
  private SceneImp sceneImp;

  @Before
  public void setUp() {
    scene = new TestScene();
    sceneImp = scene.getImplementation();
  }

  // --- Construction ---

  @Test
  public void constructorCreatesNonNullSceneImp() {
    assertNotNull("SScene should create a SceneImp internally", sceneImp);
  }

  @Test
  public void sgCompositeIsScenegraphScene() {
    assertTrue("getSgComposite() should return a scenegraph Scene",
        sceneImp.getSgComposite() instanceof Scene);
  }

  @Test
  public void sgCompositeIsNotNull() {
    assertNotNull("getSgComposite() should never be null", sceneImp.getSgComposite());
  }

  @Test
  public void abstractionReturnsSScene() {
    assertSame("getAbstraction() should return the owning SScene", scene, sceneImp.getAbstraction());
  }

  @Test
  public void getSceneReturnsSelf() {
    assertSame("SceneImp.getScene() should return itself", sceneImp, sceneImp.getScene());
  }

  @Test
  public void getProgramReturnsNullWhenNotActivated() {
    assertNull("getProgram() should return null before activation", sceneImp.getProgram());
  }

  // --- Atmosphere color property ---

  @Test
  public void atmosphereColorDefaultIsNotNull() {
    Color color = sceneImp.atmosphereColor.getValue();
    assertNotNull("Default atmosphere color should not be null", color);
  }

  @Test
  public void atmosphereColorSetValueRoundTrips() {
    sceneImp.atmosphereColor.setValue(Color.RED);
    Color result = sceneImp.atmosphereColor.getValue();
    assertNotNull(result);
    assertEquals("Atmosphere color should round-trip RED", Color.RED, result);
  }

  @Test
  public void atmosphereColorSetToWhiteRoundTrips() {
    sceneImp.atmosphereColor.setValue(Color.WHITE);
    assertEquals(Color.WHITE, sceneImp.atmosphereColor.getValue());
  }

  @Test
  public void atmosphereColorSetToBlackRoundTrips() {
    sceneImp.atmosphereColor.setValue(Color.BLACK);
    assertEquals(Color.BLACK, sceneImp.atmosphereColor.getValue());
  }

  // --- From-above light color property ---

  @Test
  public void fromAboveLightColorDefaultIsNotNull() {
    Color color = sceneImp.fromAboveLightColor.getValue();
    assertNotNull("Default from-above light color should not be null", color);
  }

  @Test
  public void fromAboveLightColorSetValueRoundTrips() {
    sceneImp.fromAboveLightColor.setValue(Color.YELLOW);
    assertEquals(Color.YELLOW, sceneImp.fromAboveLightColor.getValue());
  }

  // --- From-below light color property ---

  @Test
  public void fromBelowLightColorDefaultIsBlack() {
    Color color = sceneImp.fromBelowLightColor.getValue();
    assertNotNull("Default from-below light color should not be null", color);
    assertEquals("Default from-below light color should be BLACK", Color.BLACK, color);
  }

  @Test
  public void fromBelowLightColorSetValueRoundTrips() {
    sceneImp.fromBelowLightColor.setValue(Color.GREEN);
    assertEquals(Color.GREEN, sceneImp.fromBelowLightColor.getValue());
  }

  // --- Fog density property ---

  @Test
  public void fogDensityDefaultIsZero() {
    float density = sceneImp.fogDensity.getValue();
    assertEquals("Default fog density should be 0.0", 0.0f, density, 1e-6f);
  }

  @Test
  public void fogDensitySetToOneRoundTrips() {
    sceneImp.fogDensity.setValue(1.0f);
    float result = sceneImp.fogDensity.getValue();
    assertEquals("Fog density should round-trip 1.0", 1.0f, result, 1e-6f);
  }

  @Test
  public void fogDensitySetToHalfRoundTrips() {
    sceneImp.fogDensity.setValue(0.5f);
    float result = sceneImp.fogDensity.getValue();
    assertEquals("Fog density should round-trip 0.5", 0.5f, result, 1e-3f);
  }

  @Test
  public void fogDensitySetToZeroResetsToZero() {
    sceneImp.fogDensity.setValue(0.8f);
    sceneImp.fogDensity.setValue(0.0f);
    assertEquals("Fog density should return to 0.0", 0.0f, sceneImp.fogDensity.getValue(), 1e-6f);
  }

  // --- Event manager ---

  @Test
  public void eventManagerIsNotNullAfterConstruction() {
    EventManager em = sceneImp.getEventManager();
    assertNotNull("EventManager should be created in SceneImp constructor", em);
  }

  // --- Camera registry ---

  @Test
  public void findFirstCameraReturnsNullWhenNoCameraAdded() {
    CameraImp<?> camera = sceneImp.findFirstCamera();
    assertNull("findFirstCamera() should return null when no camera is attached", camera);
  }

  // --- Instance registry ---

  @Test
  public void instanceRegistryReturnsSelfForSgScene() {
    EntityImp imp = EntityImp.getInstance(sceneImp.getSgComposite());
    assertSame("Instance registry should map sgScene back to SceneImp", sceneImp, imp);
  }

  // --- Scene activation helpers (non-rendering paths) ---

  @Test
  public void pushPopMinimalInitializationDoesNotThrow() {
    sceneImp.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_pushPerformMinimalInitialization();
    sceneImp.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_popPerformMinimalInitialization();
  }

  @Test
  public void preserveAndRestoreStateDoesNotThrow() {
    sceneImp.preserveStateAndEventListeners();
    sceneImp.restoreStateAndEventListeners();
  }

  // --- SScene facade pass-through ---

  @Test
  public void sSceneGetAtmosphereColorDelegatesToImpl() {
    sceneImp.atmosphereColor.setValue(Color.CYAN);
    Color result = scene.getAtmosphereColor();
    assertEquals("SScene.getAtmosphereColor should delegate to SceneImp", Color.CYAN, result);
  }

  @Test
  public void sSceneGetFogDensityDelegatesToImpl() {
    sceneImp.fogDensity.setValue(0.7f);
    double result = scene.getFogDensity();
    assertEquals(0.7, result, 1e-2);
  }

  @Test
  public void sSceneGetFromAboveLightColorDelegatesToImpl() {
    sceneImp.fromAboveLightColor.setValue(Color.ORANGE);
    Color result = scene.getFromAboveLightColor();
    assertEquals(Color.ORANGE, result);
  }

  @Test
  public void sSceneGetFromBelowLightColorDelegatesToImpl() {
    sceneImp.fromBelowLightColor.setValue(Color.MAGENTA);
    Color result = scene.getFromBelowLightColor();
    assertEquals(Color.MAGENTA, result);
  }

  // --- Test double ---

  private static class TestScene extends SScene {
    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
      // no-op for test
    }
  }

  // --- Extension: SceneActivationListener add/remove and camera methods ---

  @Test
  public void addSceneActivationListenerThenRemoveIsSafe() {
    org.lgna.story.event.SceneActivationListener listener = e -> { };
    sceneImp.addSceneActivationListener(listener);
    sceneImp.removeSceneActivationListener(listener);
  }

  @Test
  public void minimalInitializationCanBeNested() {
    sceneImp.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_pushPerformMinimalInitialization();
    sceneImp.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_pushPerformMinimalInitialization();
    sceneImp.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_popPerformMinimalInitialization();
    sceneImp.ACCEPTABLE_HACK_FOR_SCENE_EDITOR_popPerformMinimalInitialization();
  }

  @Test
  public void sgCompositeReferenceIsStable() {
    Scene first = sceneImp.getSgComposite();
    Scene second = sceneImp.getSgComposite();
    assertSame(first, second);
  }

  @Test
  public void atmosphereColorSettingDoesNotAffectFromAboveLightColor() {
    Color before = sceneImp.fromAboveLightColor.getValue();
    sceneImp.atmosphereColor.setValue(Color.RED);
    assertEquals(before, sceneImp.fromAboveLightColor.getValue());
  }

  @Test
  public void fogDensityRoundtripsZeroAndOneSeveralTimes() {
    for (int i = 0; i < 3; i++) {
      sceneImp.fogDensity.setValue(0.0f);
      assertEquals(0.0f, sceneImp.fogDensity.getValue(), 1e-6f);
      sceneImp.fogDensity.setValue(1.0f);
      assertEquals(1.0f, sceneImp.fogDensity.getValue(), 1e-6f);
    }
  }

  @Test
  public void preserveAndRestoreStateAndEventListenersIsSafe() {
    sceneImp.preserveStateAndEventListeners();
    sceneImp.restoreStateAndEventListeners();
  }

  @Test
  public void preserveAndRestoreCanBeNestedSafely() {
    sceneImp.preserveStateAndEventListeners();
    sceneImp.preserveStateAndEventListeners();
    sceneImp.restoreStateAndEventListeners();
    sceneImp.restoreStateAndEventListeners();
  }
}
