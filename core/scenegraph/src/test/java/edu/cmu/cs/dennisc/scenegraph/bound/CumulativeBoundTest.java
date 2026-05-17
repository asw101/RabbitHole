package edu.cmu.cs.dennisc.scenegraph.bound;

import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import edu.cmu.cs.dennisc.scenegraph.Geometry;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CumulativeBoundTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void emptyBoundReturnsNaN() {
    CumulativeBound cb = new CumulativeBound();
    AxisAlignedBox box = cb.getBoundingBox();
    assertTrue("Empty cumulative bound should return NaN", box.isNaN());
  }

  @Test
  public void addBoundingBoxAccumulatesPoints() {
    CumulativeBound cb = new CumulativeBound();
    AxisAlignedBox box1 = new AxisAlignedBox(new Point3(0, 0, 0), new Point3(1, 1, 1));
    cb.addBoundingBox(box1, AffineMatrix4x4.IDENTITY);

    AxisAlignedBox result = cb.getBoundingBox();
    assertNotNull(result);
    assertEquals(0.0, result.minimum().x(), EPSILON);
    assertEquals(1.0, result.maximum().x(), EPSILON);
  }

  @Test
  public void addMultipleBoundingBoxesUnions() {
    CumulativeBound cb = new CumulativeBound();
    AxisAlignedBox box1 = new AxisAlignedBox(new Point3(0, 0, 0), new Point3(1, 1, 1));
    AxisAlignedBox box2 = new AxisAlignedBox(new Point3(-5, -5, -5), new Point3(-1, -1, -1));
    cb.addBoundingBox(box1, AffineMatrix4x4.IDENTITY);
    cb.addBoundingBox(box2, AffineMatrix4x4.IDENTITY);

    AxisAlignedBox result = cb.getBoundingBox();
    assertEquals(-5.0, result.minimum().x(), EPSILON);
    assertEquals(1.0, result.maximum().x(), EPSILON);
  }

  @Test
  public void addBoundingBoxWithTransformTranslatesPoints() {
    CumulativeBound cb = new CumulativeBound();
    AxisAlignedBox box = new AxisAlignedBox(new Point3(0, 0, 0), new Point3(1, 1, 1));
    AffineMatrix4x4 translate = AffineMatrix4x4.createTranslation(10, 20, 30);
    cb.addBoundingBox(box, translate);

    AxisAlignedBox result = cb.getBoundingBox();
    assertEquals(10.0, result.minimum().x(), EPSILON);
    assertEquals(11.0, result.maximum().x(), EPSILON);
    assertEquals(20.0, result.minimum().y(), EPSILON);
    assertEquals(21.0, result.maximum().y(), EPSILON);
  }

  @Test
  public void addNaNBoundingBoxIsIgnored() {
    CumulativeBound cb = new CumulativeBound();
    cb.addBoundingBox(AxisAlignedBox.NaN, AffineMatrix4x4.IDENTITY);
    assertTrue("NaN box should be ignored", cb.getBoundingBox().isNaN());
  }

  @Test
  public void addVisualAccumulatesBounds() {
    CumulativeBound cb = new CumulativeBound();
    Visual visual = new Visual();
    Box boxGeom = new Box();
    visual.geometries.setValue(new Geometry[]{boxGeom});

    cb.add(visual, AffineMatrix4x4.IDENTITY);
    AxisAlignedBox result = cb.getBoundingBox();
    assertNotNull(result);
  }
}
