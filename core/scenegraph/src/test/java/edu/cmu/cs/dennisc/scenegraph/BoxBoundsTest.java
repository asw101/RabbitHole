package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.scenegraph.event.BoundEvent;
import edu.cmu.cs.dennisc.scenegraph.event.BoundListener;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.EPSILON;
import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class BoxBoundsTest {

  @Test
  public void defaultBoxBoundsAreHalfUnit() {
    Box box = new Box();
    AxisAlignedBox bbox = box.getAxisAlignedMinimumBoundingBox();
    assertNotNull(bbox);
    assertPointEquals(new Point3(-0.5, -0.5, -0.5), bbox.minimum());
    assertPointEquals(new Point3(0.5, 0.5, 0.5), bbox.maximum());
  }

  @Test
  public void getMinimumAndMaximumReflectDefaults() {
    Box box = new Box();
    assertPointEquals(new Point3(-0.5, -0.5, -0.5), box.getMinimum());
    assertPointEquals(new Point3(0.5, 0.5, 0.5), box.getMaximum());
  }

  @Test
  public void setMinimumWithDoublesUpdatesBounds() {
    Box box = new Box();
    box.setMinimum(-2, -3, -4);
    assertPointEquals(new Point3(-2, -3, -4), box.getMinimum());

    AxisAlignedBox bbox = box.getAxisAlignedMinimumBoundingBox();
    assertNotNull(bbox);
    assertPointEquals(new Point3(-2, -3, -4), bbox.minimum());
    assertPointEquals(new Point3(0.5, 0.5, 0.5), bbox.maximum());
  }

  @Test
  public void setMaximumWithDoublesUpdatesBounds() {
    Box box = new Box();
    box.setMaximum(5, 6, 7);
    assertPointEquals(new Point3(5, 6, 7), box.getMaximum());

    AxisAlignedBox bbox = box.getAxisAlignedMinimumBoundingBox();
    assertNotNull(bbox);
    assertPointEquals(new Point3(-0.5, -0.5, -0.5), bbox.minimum());
    assertPointEquals(new Point3(5, 6, 7), bbox.maximum());
  }

  @Test
  public void setMinimumWithPoint3UpdatesBounds() {
    Box box = new Box();
    box.setMinimum(new Point3(-1, -2, -3));
    assertPointEquals(new Point3(-1, -2, -3), box.getMinimum());
  }

  @Test
  public void setMaximumWithPoint3UpdatesBounds() {
    Box box = new Box();
    box.setMaximum(new Point3(10, 20, 30));
    assertPointEquals(new Point3(10, 20, 30), box.getMaximum());
  }

  @Test
  public void setWithTwoPoint3sUpdatesMinAndMax() {
    Box box = new Box();
    box.set(new Point3(-10, -20, -30), new Point3(10, 20, 30));
    assertPointEquals(new Point3(-10, -20, -30), box.getMinimum());
    assertPointEquals(new Point3(10, 20, 30), box.getMaximum());

    AxisAlignedBox bbox = box.getAxisAlignedMinimumBoundingBox();
    assertPointEquals(new Point3(-10, -20, -30), bbox.minimum());
    assertPointEquals(new Point3(10, 20, 30), bbox.maximum());
  }

  @Test
  public void setWithAxisAlignedBoxUpdatesMinAndMax() {
    Box box = new Box();
    AxisAlignedBox aab = new AxisAlignedBox(new Point3(-5, -6, -7), new Point3(5, 6, 7));
    box.set(aab);
    assertPointEquals(new Point3(-5, -6, -7), box.getMinimum());
    assertPointEquals(new Point3(5, 6, 7), box.getMaximum());
  }

  @Test
  public void changingPropertyDirtiesBoundsAndFiresEvent() {
    Box box = new Box();
    box.getAxisAlignedMinimumBoundingBox(); // prime cache

    AtomicInteger events = new AtomicInteger();
    box.addBoundListener((BoundEvent e) -> events.incrementAndGet());

    box.xMinimum.setValue(-3.0);
    assertEquals(1, events.get());

    AxisAlignedBox bbox = box.getAxisAlignedMinimumBoundingBox();
    assertEquals(-3.0, bbox.minimum().x(), EPSILON);
  }

  @Test
  public void boundingBoxIsCachedUntilDirtied() {
    Box box = new Box();
    AxisAlignedBox first = box.getAxisAlignedMinimumBoundingBox();
    AxisAlignedBox second = box.getAxisAlignedMinimumBoundingBox();
    assertSame("bbox should be cached", first, second);

    box.xMaximum.setValue(5.0);
    AxisAlignedBox third = box.getAxisAlignedMinimumBoundingBox();
    assertNotSame("bbox should be recomputed after change", first, third);
  }

  @Test
  public void allSixPropertiesAreIndependent() {
    Box box = new Box();
    box.xMinimum.setValue(-1.0);
    box.xMaximum.setValue(1.0);
    box.yMinimum.setValue(-2.0);
    box.yMaximum.setValue(2.0);
    box.zMinimum.setValue(-3.0);
    box.zMaximum.setValue(3.0);

    AxisAlignedBox bbox = box.getAxisAlignedMinimumBoundingBox();
    assertPointEquals(new Point3(-1, -2, -3), bbox.minimum());
    assertPointEquals(new Point3(1, 2, 3), bbox.maximum());
  }

}
