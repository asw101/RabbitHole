package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.scenegraph.event.BoundEvent;
import edu.cmu.cs.dennisc.scenegraph.event.BoundListener;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SphereBoundsTest {

  @Test
  public void defaultSphereBoundsAreHalfUnit() {
    Sphere sphere = new Sphere();
    AxisAlignedBox bbox = sphere.getAxisAlignedMinimumBoundingBox();
    assertNotNull(bbox);
    assertPointEquals(new Point3(-0.5, -0.5, -0.5), bbox.minimum());
    assertPointEquals(new Point3(0.5, 0.5, 0.5), bbox.maximum());
  }

  @Test
  public void changingRadiusUpdatesBoundsAndNotifiesListeners() {
    Sphere sphere = new Sphere();
    AxisAlignedBox initialBounds = sphere.getAxisAlignedMinimumBoundingBox();

    AtomicInteger events = new AtomicInteger();
    sphere.addBoundListener((BoundEvent e) -> events.incrementAndGet());

    sphere.radius.setValue(2.0);

    assertEquals(1, events.get());
    AxisAlignedBox newBounds = sphere.getAxisAlignedMinimumBoundingBox();
    assertNotNull(newBounds);
    assertPointEquals(new Point3(-2, -2, -2), newBounds.minimum());
    assertPointEquals(new Point3(2, 2, 2), newBounds.maximum());
  }

  @Test
  public void boundingBoxIsCachedUntilDirtied() {
    Sphere sphere = new Sphere();
    AxisAlignedBox first = sphere.getAxisAlignedMinimumBoundingBox();
    AxisAlignedBox second = sphere.getAxisAlignedMinimumBoundingBox();

    assertSame("bbox should be cached (same object)", first, second);

    sphere.radius.setValue(3.0);
    AxisAlignedBox third = sphere.getAxisAlignedMinimumBoundingBox();
    assertNotSame("bbox should be recomputed after change", first, third);
  }

  @Test
  public void boundListenerCanBeRemovedAndStopsReceivingEvents() {
    Sphere sphere = new Sphere();
    AtomicInteger events = new AtomicInteger();
    BoundListener listener = (BoundEvent e) -> events.incrementAndGet();

    sphere.addBoundListener(listener);
    sphere.radius.setValue(2.0);
    assertEquals(1, events.get());

    sphere.removeBoundListener(listener);
    sphere.radius.setValue(5.0);
    assertEquals(1, events.get());
  }

  @Test
  public void isChangedTracksDirtyFlag() {
    Sphere sphere = new Sphere();
    assertFalse(sphere.isChanged());

    sphere.markAsChanged();
    assertTrue(sphere.isChanged());

    sphere.markAsUnchanged();
    assertFalse(sphere.isChanged());
  }

}
