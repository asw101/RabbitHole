package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertSame;

public class StandInTransformationBehaviorTest {
  @Test
  public void standInWithoutVehicleUsesItsLocalTransformationAsAbsoluteTransform() {
    StandIn standIn = new StandIn();
    standIn.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 2, 3));

    assertPointEquals(new Point3(1, 2, 3), standIn.getLocalTransformation().translation());
    assertPointEquals(new Point3(1, 2, 3), standIn.getAbsoluteTransformation().translation());
  }

  @Test
  public void standInComposesVehicleAndLocalTransformations() {
    Transformable vehicle = new Transformable();
    vehicle.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));

    StandIn standIn = new StandIn();
    standIn.setVehicle(vehicle);
    standIn.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 2, 3));

    assertSame(vehicle, standIn.getVehicle());
    assertPointEquals(new Point3(11, 2, 3), standIn.getAbsoluteTransformation().translation());
    assertPointEquals(new Point3(-11, -2, -3), standIn.getInverseAbsoluteTransformation().translation());
  }
}
