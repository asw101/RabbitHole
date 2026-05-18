package org.alice.stageide.gallerybrowser.shapes;

import org.junit.Test;

import static org.junit.Assert.*;

public class SphereDragModelTest {
  @Test
  public void singletonReturnsSameInstance() {
    assertSame(SphereDragModel.getInstance(), SphereDragModel.getInstance());
  }

  @Test
  public void placeOnGroundReturnsTrue() {
    assertTrue(SphereDragModel.getInstance().placeOnGround());
  }

  @Test
  public void migrationIdIsAvailable() {
    assertNotNull(SphereDragModel.getInstance().getMigrationId());
  }

  @Test
  public void boundingBoxIsAvailable() {
    assertNotNull(SphereDragModel.getInstance().getBoundingBox());
  }
}
