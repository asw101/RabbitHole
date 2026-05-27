package org.lgna.story;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class VrImplementationBehaviorTest {
  @Test
  public void vrUserScalePropertyRescalesAttachedDeviceTranslations() {
    SVRUser user = new SVRUser();
    user.getHeadset().setPositionRelativeToVehicle(new Position(1, 2, 3), new Duration(0.0));
    user.getLeftHand().setPositionRelativeToVehicle(new Position(-2, 1, 4), new Duration(0.0));
    user.getRightHand().setPositionRelativeToVehicle(new Position(3, 5, -1), new Duration(0.0));

    user.getImplementation().scale.setValue(2.0);

    assertSame(user, user.getImplementation().getAbstraction());
    assertEquals(2.0, user.getImplementation().scale.getValue(), 1e-9);
    assertEquals(2.0, user.getHeadset().getPositionRelativeToVehicle().getRight(), 1e-9);
    assertEquals(4.0, user.getHeadset().getPositionRelativeToVehicle().getUp(), 1e-9);
    assertEquals(-4.0, user.getLeftHand().getPositionRelativeToVehicle().getRight(), 1e-9);
    assertEquals(10.0, user.getRightHand().getPositionRelativeToVehicle().getUp(), 1e-9);
  }

  @Test
  public void vrHandImplementationScalesItsLocalTranslationRelativeToParent() {
    SVRUser user = new SVRUser();
    user.getLeftHand().setPositionRelativeToVehicle(new Position(1, 2, 3), new Duration(0.0));

    user.getLeftHand().getImplementation().scaleBy(0.5);

    assertSame(user.getImplementation(), user.getLeftHand().getImplementation().getParent());
    assertEquals(0.5, user.getLeftHand().getPositionRelativeToVehicle().getRight(), 1e-9);
    assertEquals(1.0, user.getLeftHand().getPositionRelativeToVehicle().getUp(), 1e-9);
    assertEquals(1.5, user.getLeftHand().getPositionRelativeToVehicle().getBackward(), 1e-9);
  }

  @Test
  public void vrHeadsetImplementationScalesItsLocalTranslationRelativeToParent() {
    SVRUser user = new SVRUser();
    user.getHeadset().setPositionRelativeToVehicle(new Position(2, 3, 4), new Duration(0.0));

    user.getHeadset().getImplementation().scaleBy(3.0);

    assertSame(user.getImplementation(), user.getHeadset().getImplementation().getParent());
    assertSame(user.getHeadset(), user.getHeadset().getImplementation().getAbstraction());
    assertEquals(6.0, user.getHeadset().getPositionRelativeToVehicle().getRight(), 1e-9);
    assertEquals(9.0, user.getHeadset().getPositionRelativeToVehicle().getUp(), 1e-9);
    assertEquals(12.0, user.getHeadset().getPositionRelativeToVehicle().getBackward(), 1e-9);
  }
}
