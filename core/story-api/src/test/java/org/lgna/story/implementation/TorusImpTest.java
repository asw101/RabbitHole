package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.scale.Resizer;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Dimension3;
import org.junit.Test;
import org.lgna.story.STorus;
import org.lgna.story.implementation.eventhandling.VerticalPrismCollisionHull;

import static org.junit.Assert.assertEquals;

public class TorusImpTest {
  private static final double EPSILON = 1.0e-6;

  @Test
  public void defaultInnerAndOuterRadiusMatchConstructorValues() {
    TorusImp imp = new STorus().getImplementation();

    assertEquals(0.25, imp.innerRadius.getValue(), EPSILON);
    assertEquals(0.5, imp.outerRadius.getValue(), EPSILON);
    assertEquals(0.375, imp.getValueForResizer(Resizer.XZ_PLANE), EPSILON);
    assertEquals(0.125, imp.getValueForResizer(Resizer.Y_AXIS), EPSILON);
  }

  @Test
  public void resizingMajorRadiusAdjustsInnerAndOuterRadiiTogether() {
    TorusImp imp = new STorus().getImplementation();

    imp.setValueForResizer(Resizer.XZ_PLANE, 1.5);

    assertEquals(1.5, imp.getValueForResizer(Resizer.XZ_PLANE), EPSILON);
    assertEquals(1.375, imp.innerRadius.getValue(), EPSILON);
    assertEquals(1.625, imp.outerRadius.getValue(), EPSILON);
  }

  @Test
  public void outerAndInnerRadiusPropertiesClampToMinimumTubeThickness() {
    TorusImp imp = new STorus().getImplementation();

    imp.innerRadius.setValue(2.0);
    assertEquals(2.0, imp.innerRadius.getValue(), EPSILON);
    assertEquals(2.01, imp.outerRadius.getValue(), EPSILON);

    imp.outerRadius.setValue(0.2);
    assertEquals(0.2, imp.outerRadius.getValue(), EPSILON);
    assertEquals(0.19, imp.innerRadius.getValue(), EPSILON);
  }

  @Test
  public void setSizeUpdatesBoundingBoxAndCollisionHull() {
    TorusImp imp = new STorus().getImplementation();

    imp.setSize(new Dimension3(8.0, 2.0, 8.0));

    Dimension3 size = imp.getSize();
    assertEquals(8.0, size.x(), EPSILON);
    assertEquals(2.0, size.y(), EPSILON);
    assertEquals(8.0, size.z(), EPSILON);

    AxisAlignedBox bbox = imp.getDynamicAxisAlignedMinimumBoundingBox(AsSeenBy.SCENE);
    assertEquals(-4.0, bbox.minimum().x(), EPSILON);
    assertEquals(-1.0, bbox.minimum().y(), EPSILON);
    assertEquals(-4.0, bbox.minimum().z(), EPSILON);
    assertEquals(4.0, bbox.maximum().x(), EPSILON);
    assertEquals(1.0, bbox.maximum().y(), EPSILON);
    assertEquals(4.0, bbox.maximum().z(), EPSILON);

    VerticalPrismCollisionHull hull = imp.getCollisionHull();
    assertEquals(4.0, hull.distanceAlong(0.0, 0.0), EPSILON);
  }
}
