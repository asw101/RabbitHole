package org.alice.stageide.gallerybrowser.shapes;

import org.junit.Test;

import static org.junit.Assert.*;

public class BillboardDragModelTest {
  @Test
  public void singletonReturnsSameInstance() {
    assertSame(BillboardDragModel.getInstance(), BillboardDragModel.getInstance());
  }

  @Test
  public void placeOnGroundReturnsTrue() {
    assertTrue(BillboardDragModel.getInstance().placeOnGround());
  }

  @Test
  public void migrationIdIsAvailable() {
    assertNotNull(BillboardDragModel.getInstance().getMigrationId());
  }

  @Test
  public void boundingBoxIsAvailable() {
    assertNotNull(BillboardDragModel.getInstance().getBoundingBox());
  }
}
