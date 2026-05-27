package edu.cmu.cs.dennisc.scenegraph.util;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.Box;
import edu.cmu.cs.dennisc.scenegraph.FillingStyle;
import edu.cmu.cs.dennisc.scenegraph.ShadingStyle;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BoundingBoxDecoratorTest {
  @Test
  public void constructorConfiguresWireframeAppearancesAndBoxGeometry() {
    BoundingBoxDecorator decorator = new BoundingBoxDecorator();

    assertSame(decorator.getSgFrontAppearance(), decorator.frontFacingAppearance.getValue());
    assertSame(decorator.getSgBackAppearance(), decorator.backFacingAppearance.getValue());
    assertEquals(Color4f.YELLOW, decorator.getSgFrontAppearance().diffuseColor.getValue());
    assertEquals(Color4f.DARK_GRAY, decorator.getSgBackAppearance().diffuseColor.getValue());
    assertEquals(ShadingStyle.NONE, decorator.getSgFrontAppearance().shadingStyle.getValue());
    assertEquals(ShadingStyle.NONE, decorator.getSgBackAppearance().shadingStyle.getValue());
    assertEquals(FillingStyle.WIREFRAME, decorator.getSgFrontAppearance().fillingStyle.getValue());
    assertEquals(FillingStyle.WIREFRAME, decorator.getSgBackAppearance().fillingStyle.getValue());
    assertTrue(decorator.isPickable.getValue());
    assertEquals(1, decorator.getGeometryCount());
    assertTrue(decorator.getGeometryAt(0) instanceof Box);
  }

  @Test
  public void setBoxUpdatesUnderlyingScenegraphBoxBounds() {
    BoundingBoxDecorator decorator = new BoundingBoxDecorator(false);
    AxisAlignedBox bounds = new AxisAlignedBox(new Point3(-2.0, 1.0, -4.0), new Point3(3.5, 5.0, 6.0));

    decorator.setBox(bounds);

    Box box = (Box) decorator.getGeometryAt(0);
    assertEquals(-2.0, box.getMinimum().x(), 0.0);
    assertEquals(1.0, box.getMinimum().y(), 0.0);
    assertEquals(-4.0, box.getMinimum().z(), 0.0);
    assertEquals(3.5, box.getMaximum().x(), 0.0);
    assertEquals(5.0, box.getMaximum().y(), 0.0);
    assertEquals(6.0, box.getMaximum().z(), 0.0);
    assertTrue(!decorator.isPickable.getValue());
  }

  @Test
  public void setNamePropagatesNamesToOwnedComponentsAndHandlesNull() {
    BoundingBoxDecorator decorator = new BoundingBoxDecorator();

    decorator.setName("bounds");
    assertEquals("bounds.sgFrontAppearance", decorator.getSgFrontAppearance().getName());
    assertEquals("bounds.sgBackAppearance", decorator.getSgBackAppearance().getName());
    assertEquals("bounds.sgBox", decorator.getGeometryAt(0).getName());

    decorator.setName(null);
    assertEquals("null.sgFrontAppearance", decorator.getSgFrontAppearance().getName());
    assertEquals("null.sgBackAppearance", decorator.getSgBackAppearance().getName());
    assertEquals("null.sgBox", decorator.getGeometryAt(0).getName());
  }
}
