package org.alice.stageide.gallerybrowser.shapes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * Contract test for all ShapeDragModel singletons.
 * Verifies singleton identity, placeOnGround, migrationId, and boundingBox
 * for each shape type in one parameterized test class.
 */
@RunWith(Parameterized.class)
public class ShapeDragModelContractTest {

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> shapes() {
    return Arrays.asList(
        shape("Axes", AxesDragModel::getInstance),
        shape("Billboard", BillboardDragModel::getInstance),
        shape("Box", BoxDragModel::getInstance),
        shape("Cone", ConeDragModel::getInstance),
        shape("Cylinder", CylinderDragModel::getInstance),
        shape("Disc", DiscDragModel::getInstance),
        shape("Ground", GroundDragModel::getInstance),
        shape("Sphere", SphereDragModel::getInstance)
    );
  }

  private static Object[] shape(String name, Supplier<? extends ShapeDragModel> factory) {
    return new Object[]{name, factory};
  }

  private final Supplier<? extends ShapeDragModel> factory;

  public ShapeDragModelContractTest(String name, Supplier<? extends ShapeDragModel> factory) {
    this.factory = factory;
  }

  @Test
  public void singletonReturnsSameInstance() {
    assertSame(factory.get(), factory.get());
  }

  @Test
  public void placeOnGroundReturnsTrue() {
    assertTrue(factory.get().placeOnGround());
  }

  @Test
  public void migrationIdIsAvailable() {
    assertNotNull(factory.get().getMigrationId());
  }

  @Test
  public void boundingBoxIsAvailable() {
    assertNotNull(factory.get().getBoundingBox());
  }
}
