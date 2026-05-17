package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.scenegraph.event.BoundEvent;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.EPSILON;
import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DiscAndCapsuleTest {

  // ── Disc ──────────────────────────────────────────

  @Test
  public void discDefaultBoundsYAxis() {
    Disc disc = new Disc();
    AxisAlignedBox bbox = disc.getAxisAlignedMinimumBoundingBox();
    assertNotNull(bbox);
    // Y axis: flat disc in XZ plane
    assertPointEquals(new Point3(-1, 0, -1), bbox.minimum());
    assertPointEquals(new Point3(1, 0, 1), bbox.maximum());
  }

  @Test
  public void discXAxisBounds() {
    Disc disc = new Disc();
    disc.axis.setValue(Disc.Axis.X);
    AxisAlignedBox bbox = disc.getAxisAlignedMinimumBoundingBox();
    assertPointEquals(new Point3(0, -1, -1), bbox.minimum());
    assertPointEquals(new Point3(0, 1, 1), bbox.maximum());
  }

  @Test
  public void discZAxisBounds() {
    Disc disc = new Disc();
    disc.axis.setValue(Disc.Axis.Z);
    AxisAlignedBox bbox = disc.getAxisAlignedMinimumBoundingBox();
    assertPointEquals(new Point3(-1, -1, 0), bbox.minimum());
    assertPointEquals(new Point3(1, 1, 0), bbox.maximum());
  }

  @Test
  public void discChangingOuterRadiusUpdatesBounds() {
    Disc disc = new Disc();
    disc.outerRadius.setValue(3.0);
    AxisAlignedBox bbox = disc.getAxisAlignedMinimumBoundingBox();
    assertPointEquals(new Point3(-3, 0, -3), bbox.minimum());
    assertPointEquals(new Point3(3, 0, 3), bbox.maximum());
  }

  @Test
  public void discChangingAxisDirtiesBounds() {
    Disc disc = new Disc();
    disc.getAxisAlignedMinimumBoundingBox(); // prime cache

    AtomicInteger events = new AtomicInteger();
    disc.addBoundListener((BoundEvent e) -> events.incrementAndGet());

    disc.axis.setValue(Disc.Axis.X);
    assertEquals(1, events.get());
  }

  @Test
  public void discDefaultProperties() {
    Disc disc = new Disc();
    assertEquals(Disc.Axis.Y, disc.axis.getValue());
    assertEquals(0.0, disc.innerRadius.getValue(), EPSILON);
    assertEquals(1.0, disc.outerRadius.getValue(), EPSILON);
    assertTrue(disc.isFrontFaceVisible.getValue());
    assertTrue(disc.isBackFaceVisible.getValue());
  }

  @Test
  public void discSetSameAxisNoOp() {
    Disc disc = new Disc();
    disc.getAxisAlignedMinimumBoundingBox();
    AtomicInteger events = new AtomicInteger();
    disc.addBoundListener((BoundEvent e) -> events.incrementAndGet());

    disc.axis.setValue(Disc.Axis.Y); // same as default
    assertEquals(0, events.get());
  }

  // ── Capsule ──────────────────────────────────────────

  @Test
  public void capsuleDefaultBoundsYAxis() {
    Capsule capsule = new Capsule();
    AxisAlignedBox bbox = capsule.getAxisAlignedMinimumBoundingBox();
    assertNotNull(bbox);
    // Y axis: major = 0.5 + 1.0 = 1.5, minor = 1.0
    assertPointEquals(new Point3(-1, -1.5, -1), bbox.minimum());
    assertPointEquals(new Point3(1, 1.5, 1), bbox.maximum());
  }

  @Test
  public void capsuleXAxisBounds() {
    Capsule capsule = new Capsule();
    capsule.axis.setValue(Capsule.Axis.X);
    AxisAlignedBox bbox = capsule.getAxisAlignedMinimumBoundingBox();
    assertPointEquals(new Point3(-1.5, -1, -1), bbox.minimum());
    assertPointEquals(new Point3(1.5, 1, 1), bbox.maximum());
  }

  @Test
  public void capsuleZAxisBounds() {
    Capsule capsule = new Capsule();
    capsule.axis.setValue(Capsule.Axis.Z);
    AxisAlignedBox bbox = capsule.getAxisAlignedMinimumBoundingBox();
    assertPointEquals(new Point3(-1, -1, -1.5), bbox.minimum());
    assertPointEquals(new Point3(1, 1, 1.5), bbox.maximum());
  }

  @Test
  public void capsuleChangingRadiusUpdatesBounds() {
    Capsule capsule = new Capsule();
    capsule.radius.setValue(2.0);
    AxisAlignedBox bbox = capsule.getAxisAlignedMinimumBoundingBox();
    // major = 0.5 + 2.0 = 2.5, minor = 2.0
    assertPointEquals(new Point3(-2, -2.5, -2), bbox.minimum());
    assertPointEquals(new Point3(2, 2.5, 2), bbox.maximum());
  }

  @Test
  public void capsuleChangingDistanceUpdatesBounds() {
    Capsule capsule = new Capsule();
    capsule.distanceBetweenSphereCenters.setValue(4.0);
    AxisAlignedBox bbox = capsule.getAxisAlignedMinimumBoundingBox();
    // major = 2.0 + 1.0 = 3.0, minor = 1.0
    assertPointEquals(new Point3(-1, -3, -1), bbox.minimum());
    assertPointEquals(new Point3(1, 3, 1), bbox.maximum());
  }

  @Test
  public void capsuleChangingAxisDirtiesBounds() {
    Capsule capsule = new Capsule();
    capsule.getAxisAlignedMinimumBoundingBox();

    AtomicInteger events = new AtomicInteger();
    capsule.addBoundListener((BoundEvent e) -> events.incrementAndGet());

    capsule.axis.setValue(Capsule.Axis.X);
    assertEquals(1, events.get());
  }

  @Test
  public void capsuleDefaultProperties() {
    Capsule capsule = new Capsule();
    assertEquals(Capsule.Axis.Y, capsule.axis.getValue());
    assertEquals(1.0, capsule.distanceBetweenSphereCenters.getValue(), EPSILON);
    assertEquals(1.0, capsule.radius.getValue(), EPSILON);
  }

  @Test
  public void capsuleSetSameAxisNoOp() {
    Capsule capsule = new Capsule();
    capsule.getAxisAlignedMinimumBoundingBox();
    AtomicInteger events = new AtomicInteger();
    capsule.addBoundListener((BoundEvent e) -> events.incrementAndGet());

    capsule.axis.setValue(Capsule.Axis.Y); // same as default
    assertEquals(0, events.get());
  }

}
