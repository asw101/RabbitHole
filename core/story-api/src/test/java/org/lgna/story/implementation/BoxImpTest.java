package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Dimension3;
import org.junit.Test;
import org.lgna.story.SBox;

import static org.junit.Assert.assertEquals;

public class BoxImpTest {
  private static final double EPSILON = 1.0e-6;

  @Test
  public void defaultConstructorCreatesUnitBoxOnTopOfGroundPlane() {
    BoxImp imp = new SBox().getImplementation();

    assertEquals(0.5, imp.getValueForResizer(Resizer.X_AXIS), EPSILON);
    assertEquals(1.0, imp.getValueForResizer(Resizer.Y_AXIS), EPSILON);
    assertEquals(0.5, imp.getValueForResizer(Resizer.Z_AXIS), EPSILON);

    AxisAlignedBox bbox = imp.getAxisAlignedMinimumBoundingBox();
    assertEquals(-0.5, bbox.minimum().x(), EPSILON);
    assertEquals(0.0, bbox.minimum().y(), EPSILON);
    assertEquals(-0.5, bbox.minimum().z(), EPSILON);
    assertEquals(0.5, bbox.maximum().x(), EPSILON);
    assertEquals(1.0, bbox.maximum().y(), EPSILON);
    assertEquals(0.5, bbox.maximum().z(), EPSILON);
  }

  @Test
  public void uniformResizerScalesWidthHeightAndDepthTogether() {
    BoxImp imp = new SBox().getImplementation();

    imp.setValueForResizer(Resizer.UNIFORM, 2.0);

    Dimension3 size = imp.getSize();
    assertEquals(2.0, size.x(), EPSILON);
    assertEquals(2.0, size.y(), EPSILON);
    assertEquals(2.0, size.z(), EPSILON);
    assertEquals(2.0, imp.getValueForResizer(Resizer.UNIFORM), EPSILON);
  }

  @Test
  public void axisSpecificResizersUpdateSymmetricBounds() {
    BoxImp imp = new SBox().getImplementation();

    imp.setValueForResizer(Resizer.X_AXIS, 3.0);
    imp.setValueForResizer(Resizer.Z_AXIS, 4.0);
    imp.setValueForResizer(Resizer.Y_AXIS, 5.0);

    AxisAlignedBox bbox = imp.getAxisAlignedMinimumBoundingBox();
    assertEquals(-3.0, bbox.minimum().x(), EPSILON);
    assertEquals(3.0, bbox.maximum().x(), EPSILON);
    assertEquals(0.0, bbox.minimum().y(), EPSILON);
    assertEquals(5.0, bbox.maximum().y(), EPSILON);
    assertEquals(-4.0, bbox.minimum().z(), EPSILON);
    assertEquals(4.0, bbox.maximum().z(), EPSILON);
  }

  @Test
  public void setSizeUpdatesBoundingBoxDimensions() {
    BoxImp imp = new SBox().getImplementation();

    imp.setSize(new Dimension3(4.0, 5.0, 6.0));

    Dimension3 size = imp.getSize();
    assertEquals(4.0, size.x(), EPSILON);
    assertEquals(5.0, size.y(), EPSILON);
    assertEquals(6.0, size.z(), EPSILON);

    AxisAlignedBox bbox = imp.getAxisAlignedMinimumBoundingBox();
    assertEquals(-2.0, bbox.minimum().x(), EPSILON);
    assertEquals(2.0, bbox.maximum().x(), EPSILON);
    assertEquals(0.0, bbox.minimum().y(), EPSILON);
    assertEquals(5.0, bbox.maximum().y(), EPSILON);
    assertEquals(-3.0, bbox.minimum().z(), EPSILON);
    assertEquals(3.0, bbox.maximum().z(), EPSILON);
  }
}
