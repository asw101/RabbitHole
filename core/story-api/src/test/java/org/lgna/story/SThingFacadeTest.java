package org.lgna.story;

import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class SThingFacadeTest {

  @Test
  public void sThingExposesCoreFacadeMethods() {
    assertHasMethod(SThing.class, "getImplementation", 0);
    assertHasMethod(SThing.class, "getName", 0);
    assertHasMethod(SThing.class, "setName", 1);
    assertHasMethod(SThing.class, "getVehicle", 0);
    assertHasMethod(SThing.class, "getVantagePoint", 1);
    assertHasMethod(SThing.class, "delay", 1);
    assertHasMethod(SThing.class, "playAudio", 1);
    assertHasMethod(SThing.class, "isCollidingWith", 1);
    assertHasMethod(SThing.class, "getBooleanFromUser", 1);
    assertHasMethod(SThing.class, "getStringFromUser", 1);
    assertHasMethod(SThing.class, "getDoubleFromUser", 1);
    assertHasMethod(SThing.class, "getIntegerFromUser", 1);
    assertHasMethod(SThing.class, "getCollisionHull", 0);
  }

  @Test
  public void sSceneExposesFacadeMethods() {
    assertHasMethod(SScene.class, "getImplementation", 0);
    assertHasMethod(SScene.class, "getAtmosphereColor", 0);
    assertHasMethod(SScene.class, "setAtmosphereColor", 2);
    assertHasMethod(SScene.class, "addMouseClickOnScreenListener", 2);
    assertHasMethod(SScene.class, "addTimeListener", 3);
    assertHasMethod(SScene.class, "addSceneActivationListener", 1);
    assertHasMethod(SScene.class, "removeSceneActivationListener", 1);
    assertHasMethod(SScene.class, "addCollisionStartListener", 4);
    assertHasMethod(SScene.class, "addProximityEnterListener", 5);
    assertHasMethod(SScene.class, "addViewEnterListener", 3);
  }

  @Test
  public void sCameraExposesFacadeMethods() {
    assertHasMethod(SCamera.class, "getLeftHand", 0);
    assertHasMethod(SCamera.class, "getRightHand", 0);
    assertHasMethod(SCamera.class, "moveAndOrientToAGoodVantagePointOf", 2);
    assertHasMethod(SCamera.class, "setFarClippingPlaneDistance", 1);
    assertHasMethod(SCamera.class, "getFarClippingPlaneDistance", 0);
    assertHasMethod(SCamera.class, "setNearClippingPlaneDistance", 1);
    assertHasMethod(SCamera.class, "getNearClippingPlaneDistance", 0);
    assertHasMethod(SCamera.class, "setHorizontalViewingAngle", 1);
    assertHasMethod(SCamera.class, "getHorizontalViewingAngle", 0);
    assertHasMethod(SCamera.class, "setVerticalViewingAngle", 1);
    assertHasMethod(SCamera.class, "getVerticalViewingAngle", 0);
  }

  @Test
  public void sModelExposesFacadeMethods() {
    assertHasMethod(SModel.class, "getImplementation", 0);
    assertHasMethod(SModel.class, "getPaint", 0);
    assertHasMethod(SModel.class, "setPaint", 2);
    assertHasMethod(SModel.class, "getOpacity", 0);
    assertHasMethod(SModel.class, "setOpacity", 2);
    assertHasMethod(SModel.class, "getScale", 0);
    assertHasMethod(SModel.class, "setScale", 2);
    assertHasMethod(SModel.class, "getSize", 0);
    assertHasMethod(SModel.class, "setSize", 2);
    assertHasMethod(SModel.class, "resize", 2);
    assertHasMethod(SModel.class, "resizeWidth", 2);
    assertHasMethod(SModel.class, "resizeHeight", 2);
    assertHasMethod(SModel.class, "resizeDepth", 2);
    assertHasMethod(SModel.class, "say", 2);
    assertHasMethod(SModel.class, "think", 2);
  }

  @Test
  public void sThingRuntimeNameAndToStringUseAssignedName() {
    SBox box = new SBox();
    box.setName("crate");
    assertEquals("crate", box.getName());
    assertEquals("crate", box.toString());
  }

  @Test
  public void unnamedThingToStringFallsBackToClassName() {
    SBox box = new SBox();
    assertTrue(box.toString().contains("SBox"));
  }

  @Test
  public void sThingVehicleRoundTrips() {
    SBox carrier = new SBox();
    SBox passenger = new SBox();
    passenger.setVehicle(carrier);
    assertSame(carrier, passenger.getVehicle());
  }

  @Test
  public void sThingGetVantagePointReturnsNonNull() {
    SBox box = new SBox();
    SBox reference = new SBox();
    assertNotNull(box.getVantagePoint(reference));
  }

  @Test
  public void sThingCollisionHullReturnsNonNull() {
    SBox box = new SBox();
    assertNotNull(box.getCollisionHull());
  }

  @Test
  public void sSceneImplementationIsNonNull() {
    TestScene scene = new TestScene();
    assertNotNull(scene.getImplementation());
  }

  @Test
  public void sSceneSceneActivationMethodExistsOnConcreteScene() throws Exception {
    Method method = TestScene.class.getMethod("handleActiveChanged", Boolean.class, Integer.class);
    assertNotNull(method);
  }

  @Test
  public void sCameraDefaultsAreAvailable() {
    assertNotNull(SCamera.DEFAULT_ORIENTATION);
    assertNotNull(SCamera.DEFAULT_POSITION);
  }

  @Test
  public void sCameraHandsAreNonNull() {
    SCamera camera = new SCamera();
    assertNotNull(camera.getLeftHand());
    assertNotNull(camera.getRightHand());
  }

  @Test
  public void sCameraClippingPlaneSettersRoundTrip() {
    SCamera camera = new SCamera();
    camera.setFarClippingPlaneDistance(321.0);
    camera.setNearClippingPlaneDistance(0.25);
    assertEquals(321.0, camera.getFarClippingPlaneDistance(), 1e-6);
    assertEquals(0.25, camera.getNearClippingPlaneDistance(), 1e-6);
  }

  @Test
  public void sCameraViewingAngleSettersUpdateUnderlyingCameraProperties() {
    SCamera camera = new SCamera();
    camera.setHorizontalViewingAngle(0.3);
    assertEquals(0.3,
        camera.getImplementation().getSgCamera().horizontalViewingAngle.getValue().getAsRevolutions(),
        1e-6);

    camera.setVerticalViewingAngle(0.2);
    assertEquals(0.2,
        camera.getImplementation().getSgCamera().verticalViewingAngle.getValue().getAsRevolutions(),
        1e-6);
  }

  @Test
  public void moveDirectionValuesMatchExpectedSet() {
    Set<MoveDirection> expected = new HashSet<>(Arrays.asList(
        MoveDirection.LEFT,
        MoveDirection.RIGHT,
        MoveDirection.UP,
        MoveDirection.DOWN,
        MoveDirection.FORWARD,
        MoveDirection.BACKWARD));
    assertEquals(expected, new HashSet<>(Arrays.asList(MoveDirection.values())));
  }

  @Test
  public void moveDirectionAxesMatchExpectedOrientation() {
    assertEquals(Vector3.NEGATIVE_X_AXIS, MoveDirection.LEFT.getAxis());
    assertEquals(Vector3.POSITIVE_X_AXIS, MoveDirection.RIGHT.getAxis());
    assertEquals(Vector3.POSITIVE_Y_AXIS, MoveDirection.UP.getAxis());
    assertEquals(Vector3.NEGATIVE_Y_AXIS, MoveDirection.DOWN.getAxis());
    assertEquals(Vector3.NEGATIVE_Z_AXIS, MoveDirection.FORWARD.getAxis());
    assertEquals(Vector3.POSITIVE_Z_AXIS, MoveDirection.BACKWARD.getAxis());
  }

  @Test
  public void moveDirectionCreateTranslationUsesAxis() {
    assertEquals(new Point3(-2.0, 0.0, 0.0), MoveDirection.LEFT.createTranslation(2.0));
    assertEquals(new Point3(0.0, 3.0, 0.0), MoveDirection.UP.createTranslation(3.0));
    assertEquals(new Point3(0.0, 0.0, 4.0), MoveDirection.BACKWARD.createTranslation(4.0));
  }

  @Test
  public void turnDirectionValuesMatchExpectedSet() {
    Set<TurnDirection> expected = new HashSet<>(Arrays.asList(
        TurnDirection.LEFT,
        TurnDirection.RIGHT,
        TurnDirection.FORWARD,
        TurnDirection.BACKWARD));
    assertEquals(expected, new HashSet<>(Arrays.asList(TurnDirection.values())));
  }

  @Test
  public void turnDirectionAxesMatchExpectedOrientation() {
    assertEquals(Vector3.POSITIVE_Y_AXIS, TurnDirection.LEFT.getAxis());
    assertEquals(Vector3.NEGATIVE_Y_AXIS, TurnDirection.RIGHT.getAxis());
    assertEquals(Vector3.NEGATIVE_X_AXIS, TurnDirection.FORWARD.getAxis());
    assertEquals(Vector3.POSITIVE_X_AXIS, TurnDirection.BACKWARD.getAxis());
  }

  @Test
  public void rollDirectionValuesMatchExpectedSet() {
    Set<RollDirection> expected = new HashSet<>(Arrays.asList(RollDirection.LEFT, RollDirection.RIGHT));
    assertEquals(expected, new HashSet<>(Arrays.asList(RollDirection.values())));
  }

  @Test
  public void rollDirectionAxesMatchExpectedOrientation() {
    assertEquals(Vector3.POSITIVE_Z_AXIS, RollDirection.LEFT.getAxis());
    assertEquals(Vector3.NEGATIVE_Z_AXIS, RollDirection.RIGHT.getAxis());
  }

  private static void assertHasMethod(Class<?> type, String name, int parameterCount) {
    for (Method method : type.getMethods()) {
      if (method.getName().equals(name) && method.getParameterTypes().length == parameterCount) {
        return;
      }
    }
    fail("Expected method not found: " + type.getName() + "." + name + "(" + parameterCount + " args)");
  }

  private static class TestScene extends SScene {
    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
    }
  }
}
