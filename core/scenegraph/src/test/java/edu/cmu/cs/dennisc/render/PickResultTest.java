package edu.cmu.cs.dennisc.render;

import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.Sphere;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PickResultTest {
  private static final double EPSILON = 0.000001;

  @Test
  public void defaultConstructorSetsNaN() {
    PickResult pr = new PickResult();
    assertNull(pr.getSource());
    assertNull(pr.getVisual());
    assertNull(pr.getGeometry());
    assertFalse(pr.isFrontFacing());
    assertEquals(-1, pr.getSubElement());
    assertTrue(pr.getPositionInSource().isNaN());
  }

  @Test
  public void constructorWithSourceSetsSource() {
    Transformable t = new Transformable();
    PickResult pr = new PickResult(t);
    assertSame(t, pr.getSource());
    assertNull(pr.getVisual());
    assertNull(pr.getGeometry());
  }

  @Test
  public void constructorWithAllFieldsSetsAll() {
    Scene scene = new Scene();
    Transformable source = new Transformable();
    Visual visual = new Visual();
    Sphere geometry = new Sphere();
    Point3 pos = new Point3(1, 2, 3);

    source.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    scene.addComponent(source);
    source.addComponent(visual);

    PickResult pr = new PickResult(source, visual, true, geometry, 42, pos);
    assertSame(source, pr.getSource());
    assertSame(visual, pr.getVisual());
    assertTrue(pr.isFrontFacing());
    assertSame(geometry, pr.getGeometry());
    assertEquals(42, pr.getSubElement());
    assertEquals(1.0, pr.getPositionInSource().x(), EPSILON);
    assertEquals(2.0, pr.getPositionInSource().y(), EPSILON);
    assertEquals(3.0, pr.getPositionInSource().z(), EPSILON);
  }

  @Test
  public void setWithSourceOnlyResetsOtherFields() {
    Transformable t1 = new Transformable();
    Transformable t2 = new Transformable();
    Visual visual = new Visual();

    PickResult pr = new PickResult(t1, visual, true, null, 5, new Point3(1, 2, 3));
    pr.set(t2);

    assertSame(t2, pr.getSource());
    assertNull(pr.getVisual());
    assertEquals(-1, pr.getSubElement());
  }

  @Test
  public void setNaNResetsAll() {
    Transformable source = new Transformable();
    PickResult pr = new PickResult(source);
    pr.setNaN();

    assertNull(pr.getSource());
    assertNull(pr.getVisual());
    assertNull(pr.getGeometry());
    assertFalse(pr.isFrontFacing());
    assertEquals(-1, pr.getSubElement());
  }

  @Test
  public void setWithNullPositionBecomesNaN() {
    Transformable source = new Transformable();
    PickResult pr = new PickResult();
    pr.set(source, null, false, null, 0, null);

    assertTrue("Position should be NaN when set with null", pr.getPositionInSource().isNaN());
  }

  @Test
  public void toStringContainsClassName() {
    PickResult pr = new PickResult();
    String s = pr.toString();
    assertNotNull(s);
    assertTrue(s.contains("PickResult"));
  }

  @Test
  public void toStringContainsVisualInfo() {
    PickResult pr = new PickResult();
    String s = pr.toString();
    assertTrue(s.contains("visual="));
  }

  @Test
  public void getPositionInVisualWithNaNSourceReturnsNaN() {
    PickResult pr = new PickResult();
    Point3 posInVisual = pr.getPositionInVisual();
    assertTrue("Position in visual should be NaN for default", posInVisual.isNaN());
  }
}
