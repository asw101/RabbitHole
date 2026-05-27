package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.scenegraph.event.BoundEvent;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertEquals;

public class CapsuleTargetTest {
  @Test
  public void boundingBoxReflectsAxisRadiusAndDistance() {
    Capsule capsule = new Capsule();
    capsule.radius.setValue(2.0);
    capsule.distanceBetweenSphereCenters.setValue(4.0);
    capsule.axis.setValue(Capsule.Axis.X);

    AxisAlignedBox box = capsule.getAxisAlignedMinimumBoundingBox();

    assertPointEquals(new Point3(-4.0, -2.0, -2.0), box.minimum());
    assertPointEquals(new Point3(4.0, 2.0, 2.0), box.maximum());
  }

  @Test
  public void axisChangeFiresOnceAndSameAxisIsNoOp() {
    Capsule capsule = new Capsule();
    capsule.getAxisAlignedMinimumBoundingBox();
    AtomicInteger events = new AtomicInteger();
    capsule.addBoundListener((BoundEvent event) -> events.incrementAndGet());

    capsule.axis.setValue(Capsule.Axis.Y);
    capsule.axis.setValue(Capsule.Axis.Z);

    assertEquals(1, events.get());
  }
}
