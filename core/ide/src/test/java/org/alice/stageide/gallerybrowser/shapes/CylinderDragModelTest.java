package org.alice.stageide.gallerybrowser.shapes;

import org.junit.Test;

import static org.junit.Assert.*;

public class CylinderDragModelTest {
  @Test
  public void singletonReturnsSameInstance() {
    assertSame(CylinderDragModel.getInstance(), CylinderDragModel.getInstance());
  }

  @Test
  public void placeOnGroundReturnsTrue() {
    assertTrue(CylinderDragModel.getInstance().placeOnGround());
  }

  @Test
  public void migrationIdIsAvailable() {
    assertNotNull(CylinderDragModel.getInstance().getMigrationId());
  }

  @Test
  public void boundingBoxIsAvailable() {
    assertNotNull(CylinderDragModel.getInstance().getBoundingBox());
  }
}
