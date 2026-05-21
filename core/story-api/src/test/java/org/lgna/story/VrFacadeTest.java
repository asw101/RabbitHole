package org.lgna.story;

import org.junit.Before;
import org.lgna.project.ast.JavaType;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class VrFacadeTest {
  private SVRUser user;
  private SVRHeadset headset;
  private SVRHand leftHand;
  private SVRHand rightHand;

  @Before
  public void setUp() {
    user = new SVRUser();
    headset = user.getHeadset();
    leftHand = user.getLeftHand();
    rightHand = user.getRightHand();
  }

  @Test
  public void vrUserDefaultsExposeDevicesAndConstants() {
    assertNotNull(SVRUser.DEFAULT_ORIENTATION);
    assertNotNull(SVRUser.DEFAULT_POSITION);
    assertNotNull(SVRUser.HEADSET_POSITION);
    assertNotNull(SVRUser.HEADSET_ORIENTATION);
    assertNotNull(SVRUser.LEFT_HAND_POSITION);
    assertNotNull(SVRUser.RIGHT_HAND_POSITION);
    assertSame(headset, user.getHeadset());
    assertSame(leftHand, user.getLeftHand());
    assertSame(rightHand, user.getRightHand());
  }

  @Test
  public void vrUserScaleAndVehicleRoundTripWithoutAnimation() {
    SBox vehicle = new SBox();
    user.setVehicle(vehicle);
    user.setScale(2.0, new Duration(0.0));

    assertEquals(2.0, user.getScale(), 1e-9);
    assertSame(vehicle.getImplementation(), user.getImplementation().getVehicle());
    assertNotNull(user.getImplementation().getAbstraction());
  }

  @Test
  public void vrHeadsetCameraPropertiesAndTransformsRoundTrip() {
    headset.setFarClippingPlaneDistance(500.0);
    headset.setNearClippingPlaneDistance(0.5);
    headset.setHorizontalViewingAngle(0.25);
    headset.setVerticalViewingAngle(0.125);
    headset.setOrientationRelativeToVehicle(new Orientation(0, 0, 0, 1), new Duration(0.0));
    headset.setPositionRelativeToVehicle(new Position(1, 2, 3), new Duration(0.0));

    assertEquals(500.0, headset.getFarClippingPlaneDistance(), 1e-9);
    assertEquals(0.5, headset.getNearClippingPlaneDistance(), 1e-9);
    assertEquals(0.25, headset.getHorizontalViewingAngle(), 1e-9);
    assertEquals(0.125, headset.getVerticalViewingAngle(), 1e-9);
    assertEquals(1.0, headset.getPositionRelativeToVehicle().getRight(), 1e-9);
    assertNotNull(headset.getOrientationRelativeToVehicle());
    assertSame(user.getImplementation(), headset.getImplementation().getParent());
  }

  @Test
  public void vrHandsTrackRelativePositionAndParent() {
    leftHand.setPositionRelativeToVehicle(new Position(-1, 2, 3), new Duration(0.0));
    rightHand.setPositionRelativeToVehicle(new Position(1, 2, 3), new Duration(0.0));

    assertEquals(-1.0, leftHand.getPositionRelativeToVehicle().getRight(), 1e-9);
    assertEquals(1.0, rightHand.getPositionRelativeToVehicle().getRight(), 1e-9);
    assertSame(user.getImplementation(), leftHand.getImplementation().getParent());
    assertSame(user.getImplementation(), rightHand.getImplementation().getParent());
  }

  @Test
  public void getDeviceMethodsFindsHandAndHeadsetAccessors() {
    List<?> methods = SVRUser.getDeviceMethods(JavaType.getInstance(SVRUser.class));
    assertEquals(3, methods.size());
    assertTrue(methods.stream().allMatch(m -> m.toString().contains("Hand") || m.toString().contains("Headset")));
  }
}
