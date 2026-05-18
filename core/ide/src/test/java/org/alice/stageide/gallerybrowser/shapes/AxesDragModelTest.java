package org.alice.stageide.gallerybrowser.shapes;

import org.junit.Test;

import static org.junit.Assert.*;

public class AxesDragModelTest {
  @Test
  public void singletonReturnsSameInstance() {
    assertSame(AxesDragModel.getInstance(), AxesDragModel.getInstance());
  }

  @Test
  public void placeOnGroundReturnsTrue() {
    assertTrue(AxesDragModel.getInstance().placeOnGround());
  }

  @Test
  public void migrationIdIsAvailable() {
    assertNotNull(AxesDragModel.getInstance().getMigrationId());
  }

  @Test
  public void boundingBoxIsAvailable() {
    assertNotNull(AxesDragModel.getInstance().getBoundingBox());
  }
}
