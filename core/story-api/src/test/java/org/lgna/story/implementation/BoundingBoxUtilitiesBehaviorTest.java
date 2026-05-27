package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.math.immutable.AxisAlignedBox;
import org.junit.Test;
import org.lgna.story.SBox;
import org.lgna.story.SGround;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BoundingBoxUtilitiesBehaviorTest {
  @Test
  public void nullScenegraphTransformableReturnsNullBoundingBox() {
    assertNull(BoundingBoxUtilities.getSGTransformableScaledBBox(null, true));
  }

  @Test
  public void unregisteredScenegraphTransformableReturnsNullBoundingBox() {
    assertNull(BoundingBoxUtilities.getSGTransformableScaledBBox(new Transformable(), true));
  }

  @Test
  public void scenegraphOverloadDelegatesToModelImplementationBoundingBox() {
    SGround ground = new SGround();
    GroundImp groundImp = ground.getImplementation();
    AxisAlignedBox expected = groundImp.getAxisAlignedMinimumBoundingBox();

    assertEquals(expected, BoundingBoxUtilities.getSGTransformableScaledBBox(groundImp.getSgComposite(), true));
  }

  @Test
  public void turnableOverloadUsesEntityImplementationComposite() {
    SBox box = new SBox();
    AxisAlignedBox expected = box.getImplementation().getAxisAlignedMinimumBoundingBox();

    assertEquals(expected, BoundingBoxUtilities.getTransformableScaledBBox(box, true));
  }

  @Test
  public void modelImplementationOverloadUsesCompositeBoundingBox() {
    SGround ground = new SGround();
    GroundImp groundImp = ground.getImplementation();
    AxisAlignedBox expected = groundImp.getAxisAlignedMinimumBoundingBox();

    assertEquals(expected, BoundingBoxUtilities.getTransformableScaledBBox(groundImp, true));
  }
}
